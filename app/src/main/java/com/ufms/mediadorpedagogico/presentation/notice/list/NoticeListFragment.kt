package com.ufms.mediadorpedagogico.presentation.notice.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ufms.mediadorpedagogico.R
import com.ufms.mediadorpedagogico.databinding.FragmentNoticeListBinding
import com.ufms.mediadorpedagogico.domain.entity.notice.Notice
import com.ufms.mediadorpedagogico.presentation.util.extensions.*
import com.ufms.mediadorpedagogico.presentation.util.structure.base.BaseFragment
import com.ufms.mediadorpedagogico.presentation.util.structure.base.BaseViewModel
import com.ufms.mediadorpedagogico.presentation.util.structure.navigation.navigateSafe
import com.ufms.mediadorpedagogico.presentation.util.viewmodels.Placeholder
import org.koin.android.ext.android.inject

class NoticeListFragment : BaseFragment() {

    override val titleHelp: String get() = getString(R.string.help_notice_list_title)
    override val descriptionHelp: String get() = getString(R.string.help_notice_list_description)
    override val toolbarTitle: String get() = getString(R.string.activity_notice_label)
    override val baseViewModel: BaseViewModel get() = viewModel

    var noticeListAdapter: NoticeListAdapter? = null
    private var moreNoticesToBeLoaded = true
    private var isLoadingMoreNotice = false
    lateinit var binding: FragmentNoticeListBinding
    private val viewModel: NoticeListViewModel by inject()
    private val navController by lazy { findNavController() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentNoticeListBinding.inflate(inflater, container, false)
        setupRecyclerView()
        return binding.root
    }

    override fun subscribeUi() {
        super.subscribeUi()
        with(viewModel) {
            placeholder.observeAction(viewLifecycleOwner, ::onNextPlaceholder)
            noticeContent.observeEvent(viewLifecycleOwner, ::onNoticeContentLoaded)
            noContentReturned.observeEvent(viewLifecycleOwner, ::onNoContentReturned)
        }
    }

    override fun openHelp() {
        navController.navigateSafe(NoticeListFragmentDirections.actionNoticeListFragmentToHelpBottomSheet(titleHelp, descriptionHelp))
    }

    private fun setupRecyclerView() {
        noticeListAdapter.ifNull {
            noticeListAdapter = NoticeListAdapter(::setupOnItemClicked)
        }
        with(binding.recyclerViewNotice) {
            if (adapter == null) {
                layoutManager = LinearLayoutManager(context)
                adapter = noticeListAdapter
                addOnScrollListener(setLoadMoreNoticesOnScroll())
            }
        }
    }

    private fun setLoadMoreNoticesOnScroll(): RecyclerView.OnScrollListener {
        return object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                with(binding.recyclerViewNotice) {
                    val totalItemCount = layoutManager?.itemCount
                    var lastVisibleItem =
                        (layoutManager as? LinearLayoutManager)?.findLastVisibleItemPosition()
                    lastVisibleItem = lastVisibleItem?.run { this + 1 }
                    if (totalItemCount == lastVisibleItem && moreNoticesToBeLoaded && !isLoadingMoreNotice) {
                        isLoadingMoreNotice = true
                        viewModel.loadMoreNotice()
                    }
                }
            }
        }
    }

    private fun onNoticeContentLoaded(noticeContent: List<Notice>?) {
        safeLet(noticeContent, noticeListAdapter) { notices, adapter ->
            adapter.setItems(notices)
            isLoadingMoreNotice = false
        }
    }

    fun onNoContentReturned(noContentReturned: Boolean?) {
        noContentReturned?.let {
            if (noticeListAdapter?.itemCount == 0) {
                binding.includedPlaceholderNoResults.root.setVisible(true)
            } else {
                moreNoticesToBeLoaded = false
            }
        }
    }

    private fun setupOnItemClicked(notice: Notice) {
        navController.navigateSafe(
            NoticeListFragmentDirections.actionNoticeListFragmentToNoticeDetailsFragment().setNotice(
                notice
            )
        )
    }

    private fun onNextPlaceholder(placeholder: Placeholder?) {
        placeholder?.let { binding.placeholder = it }
    }
}