package com.ufms.mediadorpedagogico.domain.interactor.user

import com.ufms.mediadorpedagogico.domain.boundary.UserRepository
import com.ufms.mediadorpedagogico.domain.entity.User
import io.reactivex.Single

class SignUp(private val userRepository: UserRepository) {

    fun execute(fields: Fields): Single<User> {
        return Single.just(
            FormFields()
                .withName(fields.name)
                .withEmail(fields.email)
                .withPhoneNumber(fields.phoneNumber)
                .withCpf(fields.cpf)
                .withPassword(fields.password)
                .withPasswordConfirmation(fields.passwordConfirmation)
        )
            .doOnSuccess { formFields -> if (!formFields.isValid) throw formFields.exception }
            .flatMap { _ -> userRepository.signUp(fields) }
            .doAfterSuccess { userRepository.cacheUser(it) }
    }

    class Fields(
        val name: String,
        val email: String,
        val phoneNumber: String,
        val cpf: String,
        val password: String,
        val passwordConfirmation: String,
        val avatar: String?
    )
}
