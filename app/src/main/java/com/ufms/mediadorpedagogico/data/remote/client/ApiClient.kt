package com.ufms.mediadorpedagogico.data.remote.client

import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import com.ufms.mediadorpedagogico.BuildConfig
import com.ufms.mediadorpedagogico.data.remote.entity.*
import com.ufms.mediadorpedagogico.data.remote.entity.homework.ApiHomeworkContent
import com.ufms.mediadorpedagogico.data.remote.entity.library.ApiLibContent
import com.ufms.mediadorpedagogico.data.remote.entity.library.ApiLibResource
import com.ufms.mediadorpedagogico.data.remote.entity.library.ApiTopic
import com.ufms.mediadorpedagogico.data.remote.entity.news.ApiNewsContent
import com.ufms.mediadorpedagogico.data.remote.entity.notice.ApiNoticeContent
import com.ufms.mediadorpedagogico.domain.entity.Teacher
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.SingleTransformer
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.IOException
import java.net.SocketTimeoutException
import java.text.DateFormat

object ApiClient {

    private const val apiEndpoint = BuildConfig.API_ENDPOINT
    private lateinit var retrofit: Retrofit
    private lateinit var authInterceptor: AuthInterceptor
    private var apiServiceSingleton: ApiService? = null

    private val apiServices: ApiService get() = apiServiceSingleton ?: buildApiServices()

    fun signIn(classKey: String, name: String): Single<ApiUser> {
        return makeRequest(apiServices.signIn(classKey, name))
    }

    fun getListOfHomework(id: Int,pageNumber: Int, classKey: String): Single<ApiHomeworkContent> {
        return makeRequest(apiServices.getListOfHomework(id, pageNumber, classKey))
    }

    fun getListOfNotice(pageNumber: Int): Single<ApiNoticeContent> {
        return makeRequest(apiServices.getListOfNotices(pageNumber))
    }

    fun getListOfNews(pageNumber: Int): Single<ApiNewsContent> {
        return makeRequest(apiServices.getListOfNews(pageNumber))
    }

    fun getBullyingInformation(): Single<ApiBullying> {
        return makeRequest(apiServices.getBullyingInformation())
    }

    fun getGuildInformation(): Single<ApiGuild> {
        return makeRequest(apiServices.getGuildInformation())
    }

    fun getAboutInformation(): Single<ApiAbout> {
        return makeRequest(apiServices.getAboutInformation())
    }

    fun getCalendar(): Single<ApiCalendar> {
        return makeRequest(apiServices.getCalendar())
    }

    fun getTopics(): Single<List<ApiTopic>> {
        return makeRequest(apiServices.getTopics())
    }

    fun getLibResources( pageNumber: Int, topicId: Int): Single<ApiLibContent> {
        return makeRequest(apiServices.getLibResources(pageNumber, topicId))
    }

    fun getTeachers(): Single<List<ApiTeacher>> {
        return makeRequest(apiServices.getTeachers())
    }

    /**
     *
     * - ApiService, Retrofit, AuthInterceptor builders
     * - Response and Request Handler Methods
     *
     **/

    private fun buildApiServices(): ApiService {
        val okHttpClientBuilder = okHttpClientBuilder()
        retrofit = Retrofit.Builder()
            .client(okHttpClientBuilder.build())
            .baseUrl(apiEndpoint)
            .addConverterFactory(
                GsonConverterFactory.create(
                    GsonBuilder()
                        .serializeNulls()
                        .setDateFormat(DateFormat.FULL)
                        .create()
                )
            )
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()

        with(retrofit.create(ApiService::class.java)) {
            apiServiceSingleton = this
            return this
        }
    }

    private fun okHttpClientBuilder(): OkHttpClient.Builder {
        authInterceptor = AuthInterceptor()
        val okHttpClientBuilder = OkHttpClient.Builder()
        okHttpClientBuilder.addInterceptor(HttpLoggingInterceptor().setLevel(resolveLevelInterceptor()))
        okHttpClientBuilder.addInterceptor(authInterceptor)
        return okHttpClientBuilder
    }

    private fun resolveLevelInterceptor() =
        if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE

    private fun <T> verifyResponseException(): SingleTransformer<Response<T>, Response<T>> {
        return SingleTransformer { upstream ->
            upstream.doOnSuccess { response ->
                if (!response.isSuccessful) {
                    throw RequestException.httpError(response.code(), response.errorBody())
                }
            }
        }
    }

    private fun <T> verifyRequestException(): SingleTransformer<Response<T>, Response<T>> {
        return SingleTransformer { upstream ->
            upstream.onErrorResumeNext { t ->
                when (t) {
                    is RequestException -> Single.error(t)
                    is SocketTimeoutException -> Single.error(RequestException.timeoutError(t))
                    is IOException -> Single.error(RequestException.networkError(t))
                    else -> Single.error(RequestException.unexpectedError(t))
                }
            }
        }
    }

    private fun <T> unwrap(): SingleTransformer<Response<T>, T> {
        return SingleTransformer { upstream ->
            upstream.map<T> { it.body()!! }
        }
    }

    private fun <T> makeRequest(request: Single<Response<T>>): Single<T> {
        return request.compose(verifyResponseException())
            .compose(verifyRequestException())
            .compose(unwrap())
    }
}
