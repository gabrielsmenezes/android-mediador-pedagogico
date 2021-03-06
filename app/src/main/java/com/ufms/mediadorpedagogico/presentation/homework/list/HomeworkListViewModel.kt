package com.ufms.mediadorpedagogico.presentation.homework.list

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.OnLifecycleEvent
import com.ufms.mediadorpedagogico.domain.entity.homework.Homework
import com.ufms.mediadorpedagogico.domain.entity.homework.HomeworkContent
import com.ufms.mediadorpedagogico.domain.extensions.defaultSched
import com.ufms.mediadorpedagogico.domain.interactor.homework.GetHomework
import com.ufms.mediadorpedagogico.domain.interactor.user.GetPersistedUser
import com.ufms.mediadorpedagogico.domain.interactor.user.InvalidFieldsException
import com.ufms.mediadorpedagogico.presentation.util.extensions.defaultPlaceholders
import com.ufms.mediadorpedagogico.presentation.util.resources.SchedulerProvider
import com.ufms.mediadorpedagogico.presentation.util.structure.arch.Event
import com.ufms.mediadorpedagogico.presentation.util.structure.base.BaseViewModel
import io.reactivex.rxkotlin.subscribeBy

class HomeworkListViewModel(
    private val schedulerProvider: SchedulerProvider,
    private val getHomework: GetHomework,
    private val getPersistedUser: GetPersistedUser
) : BaseViewModel() {

    val errors: LiveData<Event<InvalidFieldsException>> get() = _errors
    val homeworkContent: LiveData<Event<List<Homework>>> get() = _homeworkContent
    val noContentReturned: LiveData<Event<Boolean>> get() = _noContentReturned

    private val _errors: MutableLiveData<Event<InvalidFieldsException>> = MutableLiveData()
    private val _homeworkContent: MutableLiveData<Event<List<Homework>>> = MutableLiveData()
    private val _noContentReturned: MutableLiveData<Event<Boolean>> = MutableLiveData()
    private var pageNumber = 0
    private var isLoading = false
    private var thereAreMoreToLoad = true

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    override fun onCreate() {
        super.onCreate()
        loadMoreHomework()
    }

    fun loadMoreHomework() {
        val classKey = getPersistedUser.execute()?.classKey
        classKey?.let {
            if (!isLoading && thereAreMoreToLoad) {
                isLoading = true
                getHomework.execute(pageNumber, it)
                    .defaultPlaceholders(this::setPlaceholder)
                    .defaultSched(schedulerProvider)
                    .subscribeBy(this::onFailure, this::onSuccess)
                    .let(disposables::add)
            }
        }
    }

    private fun onFailure(throwable: Throwable) {
        setDialog(throwable)
        _noContentReturned.value = Event(true)
    }

    private fun onSuccess(content: HomeworkContent) {
        content.content?.run {
            isLoading = false
            thereAreMoreToLoad = size == 10
            if (isEmpty()) {
                _noContentReturned.value = Event(true)
            } else {
                _homeworkContent.value = Event(this)
            }
        }
        pageNumber++
    }
}