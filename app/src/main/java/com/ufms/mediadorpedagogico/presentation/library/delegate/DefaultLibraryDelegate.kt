package com.ufms.mediadorpedagogico.presentation.library.delegate

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ufms.mediadorpedagogico.domain.entity.library.LibContent
import com.ufms.mediadorpedagogico.domain.entity.library.LibResource
import com.ufms.mediadorpedagogico.domain.entity.library.Topic
import com.ufms.mediadorpedagogico.domain.extensions.defaultSched
import com.ufms.mediadorpedagogico.domain.interactor.library.GetLibResources
import com.ufms.mediadorpedagogico.domain.interactor.library.GetTopic
import com.ufms.mediadorpedagogico.presentation.util.extensions.defaultPlaceholders
import com.ufms.mediadorpedagogico.presentation.util.resources.SchedulerProvider
import com.ufms.mediadorpedagogico.presentation.util.structure.arch.Event
import com.ufms.mediadorpedagogico.presentation.util.viewmodels.Placeholder
import io.reactivex.rxkotlin.subscribeBy

class DefaultLibraryDelegate(
    private val getTopic: GetTopic,
    private val getLibResources: GetLibResources,
    private val schedulerProvider: SchedulerProvider
) : LibraryDelegate {

    override val topics: LiveData<List<Topic>> get() = _topics
    override val libResources: LiveData<Event<List<LibResource>>> get() = _libResources
    override val noContentReturned: LiveData<Event<Unit>> get() = _noContentReturned

    private val _topics = MutableLiveData<List<Topic>>()
    private val _libResources = MutableLiveData<Event<List<LibResource>>>()
    private val _noContentReturned = MutableLiveData<Event<Unit>>()
    private var pageNumber = 0
    private var isLoading = false
    private var thereAreMoreToLoad = true

    override fun getTopics(
        onSuccess: (List<Topic>) -> Unit,
        onFailure: (Throwable) -> Unit,
        placeholderAction: (Placeholder) -> Unit
    ) {
        getTopic
            .execute()
            .defaultSched(schedulerProvider)
            .defaultPlaceholders(placeholderAction)
            .subscribeBy(onFailure) {
                _topics.value = it
            }
    }

    override fun getLibResources(
        id: Int,
        onSuccess: (List<LibResource>) -> Unit,
        onFailure: (Throwable) -> Unit,
        placeholderAction: (Placeholder) -> Unit
    ) {
        if (!isLoading && thereAreMoreToLoad) {
            isLoading = true
            getLibResources
                .execute(pageNumber, id)
                .defaultSched(schedulerProvider)
                .defaultPlaceholders(placeholderAction)
                .subscribeBy(onFailure, ::onLibResourcesSuccess)
        }
    }

    private fun onLibResourcesSuccess(libContent: LibContent?) {
        libContent?.content?.run {
            isLoading = false
            thereAreMoreToLoad = size == 10
            if (isEmpty()) {
                _noContentReturned.value = Event(Unit)
            } else {
                _libResources.value = Event(this)
            }
        }
        pageNumber++
    }
}