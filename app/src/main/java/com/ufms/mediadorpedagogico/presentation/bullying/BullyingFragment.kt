package com.ufms.mediadorpedagogico.presentation.bullying

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ufms.mediadorpedagogico.R
import com.ufms.mediadorpedagogico.databinding.FragmentBullyingBinding
import com.ufms.mediadorpedagogico.domain.entity.Bullying
import com.ufms.mediadorpedagogico.presentation.util.extensions.observeAction
import com.ufms.mediadorpedagogico.presentation.util.structure.base.BaseFragment
import com.ufms.mediadorpedagogico.presentation.util.structure.base.BaseViewModel
import com.ufms.mediadorpedagogico.presentation.util.viewmodels.Placeholder
import org.koin.android.ext.android.inject

class BullyingFragment : BaseFragment() {
    override val toolbarTitle: String
        get() = getString(R.string.bullying)

    override val baseViewModel: BaseViewModel get() = viewModel

    private lateinit var binding: FragmentBullyingBinding
    private val viewModel: BullyingViewModel by inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentBullyingBinding.inflate(inflater, container, false)
        lifecycle.addObserver(viewModel)
        setupUi()
        return binding.root
    }

    override fun subscribeUi() {
        super.subscribeUi()
        with(viewModel) {
            placeholder.observeAction(viewLifecycleOwner, ::onNextPlaceholder)
            bullyingReceived.observeAction((viewLifecycleOwner), ::onBullyingReceived)
        }
    }

    private fun onBullyingReceived(bullying: Bullying?) {
        bullying?.let {
            binding.bullying = it
        }
    }

    private fun setupUi() {

    }

    private fun onNoContentReturned(noContentReturned: Boolean?) {

    }

    private fun onNextPlaceholder(placeholder: Placeholder?) {
        placeholder?.let { binding.loadingPlaceholder.placeholder = it }
    }

    companion object {
        fun createIntent(context: Context): Intent {
            return Intent(context, BullyingFragment::class.java)
        }
    }
}