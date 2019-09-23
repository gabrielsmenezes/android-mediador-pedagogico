package com.ufms.mediadorpedagogico.presentation.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ufms.mediadorpedagogico.domain.extensions.defaultSched
import com.ufms.mediadorpedagogico.domain.interactor.user.InvalidFieldsException
import com.ufms.mediadorpedagogico.domain.interactor.user.LoginForm
import com.ufms.mediadorpedagogico.domain.interactor.user.SignIn
import com.ufms.mediadorpedagogico.presentation.util.extensions.defaultPlaceholders
import com.ufms.mediadorpedagogico.presentation.util.resources.SchedulerProvider
import com.ufms.mediadorpedagogico.presentation.util.structure.base.BaseViewModel
import io.reactivex.rxkotlin.subscribeBy

class LoginViewModel(
    private val signIn: SignIn,
    private val schedulerProvider: SchedulerProvider
) : BaseViewModel() {

    val showGroupFieldError: LiveData<Boolean> get() = showClassKeyFieldErrorLiveData
    val showNameFieldError: LiveData<Boolean> get() = showNameFieldErrorLiveData
    val goToMain: LiveData<Boolean> get() = goToMainLiveData

    private val showClassKeyFieldErrorLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private val showNameFieldErrorLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private val goToMainLiveData: MutableLiveData<Boolean> = MutableLiveData()

    private var form = LoginForm()

    fun onClassKeyChanged(classKey: String) {
        form.classKey = classKey
    }

    fun onNameChanged(name: String) {
        form.name = name
    }

    fun onSubmitClicked() {
        form.useForm(this::submit)?.let(::showFieldErrors)
    }

    private fun submit(classKey: String, password: String) {
        signIn.default(classKey, password)
            .defaultPlaceholders(this::setPlaceholder)
            .defaultSched(schedulerProvider)
            .subscribeBy(this::onFailure) {
                showClassKeyFieldErrorLiveData.value = false
                showNameFieldErrorLiveData.value = false
                goToMainLiveData.value = true
            }
            .let(disposables::add)
    }

    private fun onFailure(throwable: Throwable) {
        if (throwable is InvalidFieldsException) {
            showFieldErrors(throwable)
        }
//        } else {
//            setDialog(throwable, this::onSubmitClicked)
//        }
        setDialog(throwable, this::onSubmitClicked)
    }

    private fun showFieldErrors(e: InvalidFieldsException) {
        for (field in e.getFields()) {
            showFieldError(field)
        }
        setDialog(e, this::onSubmitClicked)
    }

    private fun showFieldError(field: Int) {
        when (field) {
            InvalidFieldsException.CLASS_KEY -> showClassKeyFieldErrorLiveData.value = true
            InvalidFieldsException.NAME -> showNameFieldErrorLiveData.value = true
        }
    }
}