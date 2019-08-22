package com.ufms.mediadorpedagogico.presentation.password

import android.content.Context
import android.content.Intent
import androidx.databinding.DataBindingUtil
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.ufms.mediadorpedagogico.R
import com.ufms.mediadorpedagogico.databinding.ActivityRecoverPasswordBinding
import com.ufms.mediadorpedagogico.presentation.structure.base.BaseActivity
import com.ufms.mediadorpedagogico.presentation.structure.base.BaseViewModel
import com.ufms.mediadorpedagogico.presentation.util.extensions.*
import com.ufms.mediadorpedagogico.presentation.util.viewmodels.Placeholder
import org.koin.android.ext.android.inject

class RecoverPasswordActivity : BaseActivity() {
    override val baseViewModel: BaseViewModel get() = viewModel

    private lateinit var binding: ActivityRecoverPasswordBinding
    private val viewModel: RecoverPasswordViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_recover_password)
        binding.submitButton.setOnClickListener { viewModel.onSubmitButtonClick() }
        binding.emailInput.observeChanges(viewModel::onEmailChanged)
        showHomeButton()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_send, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_send) {
            viewModel.onSubmitButtonClick()
        }
        return handleMenuItemClick(item) || super.onOptionsItemSelected(item)
    }

    override fun subscribeUi() {
        super.subscribeUi()
        viewModel.showEmailFieldError.observeEvent(this) { showEmailFieldError() }
        viewModel.placeholder.observe(this, ::onNextPlaceholder)
        viewModel.close.observeEvent(this) { if (it == true) close() }
    }


    private fun onNextPlaceholder(placeholder: Placeholder?) {
        placeholder?.let { binding.includedLoading.placeholder = it }
    }

    private fun showEmailFieldError() {
        binding.emailInput.setError(R.string.validation_email)
    }

    private fun close() {
        finish()
    }

    companion object {

        fun createIntent(context: Context): Intent {
            return Intent(context, RecoverPasswordActivity::class.java)
        }
    }
}