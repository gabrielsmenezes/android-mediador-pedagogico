package com.ufms.mediadorpedagogico.data.remote.repository

import com.ufms.mediadorpedagogico.data.remote.client.ApiClient
import com.ufms.mediadorpedagogico.data.remote.entity.ApiAbout
import com.ufms.mediadorpedagogico.domain.boundary.AboutRepository
import com.ufms.mediadorpedagogico.domain.entity.About
import io.reactivex.Single

class DefaultAboutRepository(
    private val apiClient: ApiClient
) : AboutRepository {

    override fun getAboutInformation(): Single<About> {
        return apiClient.getAboutInformation().map(ApiAbout.ApiAboutToAbout::transform)
    }
}

