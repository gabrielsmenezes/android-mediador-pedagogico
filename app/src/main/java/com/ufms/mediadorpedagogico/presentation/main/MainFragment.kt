package com.ufms.mediadorpedagogico.presentation.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.ufms.mediadorpedagogico.R
import com.ufms.mediadorpedagogico.databinding.FragmentMainBinding
import com.ufms.mediadorpedagogico.presentation.util.extensions.*
import com.ufms.mediadorpedagogico.presentation.util.structure.base.BaseFragment
import com.ufms.mediadorpedagogico.presentation.util.structure.base.BaseViewModel
import com.ufms.mediadorpedagogico.presentation.util.structure.navigation.navigateSafe
import com.ufms.mediadorpedagogico.presentation.util.viewmodels.Placeholder
import org.koin.android.ext.android.inject

class MainFragment : BaseFragment() {
    override val titleHelp: String get() = getString(R.string.help_main_title)
    override val descriptionHelp: String get() = getString(R.string.help_main_description)
    override val toolbarTitle: String get() = getString(R.string.main)
    override val baseViewModel: BaseViewModel get() = viewModel

    private lateinit var binding: FragmentMainBinding
    private val viewModel: MainViewModel by inject()
    private val navController by lazy { findNavController() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentMainBinding.inflate(inflater, container, false)
        setupUi()
        return binding.root
    }

    override fun subscribeUi() {
        super.subscribeUi()
        with(viewModel) {
            placeholder.observeAction(viewLifecycleOwner, ::onNextPlaceholder)
            noContentReturned.observeEvent(viewLifecycleOwner, ::onNoContentReturned)
            playTour.observeEvent(viewLifecycleOwner, ::hasAlreadyPlayed)
            today.observeAction(viewLifecycleOwner, ::onToday)
        }
    }

    override fun openHelp() {
        navController.navigateSafe(
            MainFragmentDirections.actionMainFragmentToHelpBottomSheet(
                titleHelp,
                descriptionHelp
            )
        )
    }

    private fun setupUi() {
        viewModel.tourWasPlayed()
        with(binding) {
            cardViewHomework.setOnClickListener(::goToHomework)
            cardViewNotice.setOnClickListener(::goToNotice)
            cardViewNews.setOnClickListener(::goToNews)
            cardViewSettings.setOnClickListener(::goToSettings)
            cardViewBullying.setOnClickListener(::goToBullying)
            cardViewGuild.setOnClickListener(::goToGuild)
            cardViewAbout.setOnClickListener(::goToAbout)
            cardViewCalendar.setOnClickListener(::goToCalendar)
            cardViewLibrary.setOnClickListener(::goToLibrary)
        }
    }

    private fun onToday(today: String?) {
        binding.todayText.text = today
    }

    private fun hasAlreadyPlayed(playTour: Boolean?) {
        playTour?.let {
            if (it) {
                setupUi()
            } else {
                activity?.let { MainTourHelper.setupMainTour(binding, it, ::setupUi) }
            }
        }
    }

    private fun goToNotice() {
        navController.navigateSafe(MainFragmentDirections.actionMainFragmentToNoticeListFragment())
    }

    private fun goToNews() {
        navController.navigateSafe(MainFragmentDirections.actionMainFragmentToNewsListFragment())
    }

    private fun goToHomework() {
        navController.navigateSafe(MainFragmentDirections.actionMainFragmentToHomeworkListFragment())
    }

    private fun goToBullying() {
        navController.navigateSafe(MainFragmentDirections.actionMainFragmentToBullyingFragment())
    }

    private fun goToGuild() {
        navController.navigateSafe(MainFragmentDirections.actionMainFragmentToGuildFragment())
    }

    private fun goToSettings() {
        navController.navigateSafe(MainFragmentDirections.actionMainFragmentToSettingsFragment())
    }

    private fun goToAbout() {
        navController.navigateSafe(MainFragmentDirections.actionMainFragmentToAboutFragment())
    }

    private fun goToLibrary() {
        navController.navigateSafe(MainFragmentDirections.actionMainFragmentToTopicFragment())
    }

    private fun goToCalendar() {
        navController.navigateSafe(
            MainFragmentDirections.actionMainFragmentToWebViewFragment()
        )
    }

    private fun onNoContentReturned(noContentReturned: Boolean?) {
        noContentReturned?.let {
            binding.includedPlaceholderNoResults.root.setVisible(true)
        }
    }

    private fun onNextPlaceholder(placeholder: Placeholder?) {
        placeholder?.let { binding.loadingPlaceholder.placeholder = it }
    }

    companion object {
        const val HELP_WIDTH = 400
    }
}