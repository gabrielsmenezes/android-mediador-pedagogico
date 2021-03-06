package com.ufms.mediadorpedagogico.presentation.util.dependecyinjector

import com.ufms.mediadorpedagogico.domain.entity.homework.Homework
import com.ufms.mediadorpedagogico.domain.entity.notice.Notice
import com.ufms.mediadorpedagogico.presentation.about.AboutViewModel
import com.ufms.mediadorpedagogico.presentation.bullying.BullyingViewModel
import com.ufms.mediadorpedagogico.presentation.guild.GuildViewModel
import com.ufms.mediadorpedagogico.presentation.homework.details.HomeworkDetailsViewModel
import com.ufms.mediadorpedagogico.presentation.homework.list.HomeworkListViewModel
import com.ufms.mediadorpedagogico.presentation.landing.SplashViewModel
import com.ufms.mediadorpedagogico.presentation.library.libresource.LibResourceViewModel
import com.ufms.mediadorpedagogico.presentation.library.topic.TopicViewModel
import com.ufms.mediadorpedagogico.presentation.login.LoginViewModel
import com.ufms.mediadorpedagogico.presentation.main.MainViewModel
import com.ufms.mediadorpedagogico.presentation.news.NewsListViewModel
import com.ufms.mediadorpedagogico.presentation.notice.details.NoticeDetailsViewModel
import com.ufms.mediadorpedagogico.presentation.notice.list.NoticeListViewModel
import com.ufms.mediadorpedagogico.presentation.settings.SettingsViewModel
import com.ufms.mediadorpedagogico.presentation.teacher.TeacherListViewModel
import com.ufms.mediadorpedagogico.presentation.util.structure.base.BaseViewModel
import com.ufms.mediadorpedagogico.presentation.calendar.CalendarViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { BaseViewModel() }
    viewModel { MainViewModel(get(), get(), get(), get(), get()) }
    viewModel { SplashViewModel(get()) }
    viewModel { LoginViewModel(get(), get(), get()) }
    viewModel { HomeworkListViewModel(get(), get(), get()) }
    viewModel { (homework: Homework) -> HomeworkDetailsViewModel(homework) }
    viewModel { NoticeListViewModel(get(), get()) }
    viewModel { (notice: Notice) -> NoticeDetailsViewModel(notice) }
    viewModel { NewsListViewModel(get(), get()) }
    viewModel { BullyingViewModel(get()) }
    viewModel { SettingsViewModel(get(), get(), get()) }
    viewModel { GuildViewModel(get()) }
    viewModel { AboutViewModel(get()) }
    viewModel { TopicViewModel(get()) }
    viewModel { (id: Int) -> LibResourceViewModel(id, get()) }
    viewModel { CalendarViewModel(get()) }
    viewModel { TeacherListViewModel(get(), get()) }
}