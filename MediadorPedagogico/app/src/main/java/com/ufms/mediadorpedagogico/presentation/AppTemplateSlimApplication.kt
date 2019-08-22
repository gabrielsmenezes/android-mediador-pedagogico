package com.ufms.mediadorpedagogico.presentation

import androidx.multidex.MultiDexApplication
import com.facebook.stetho.Stetho
import com.ufms.mediadorpedagogico.presentation.structure.dependecyinjector.applicationModule
import com.ufms.mediadorpedagogico.presentation.structure.dependecyinjector.interactorModule
import com.ufms.mediadorpedagogico.presentation.structure.dependecyinjector.repositoryModule
import com.ufms.mediadorpedagogico.presentation.structure.dependecyinjector.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class AppTemplateSlimApplication : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        Stetho.initializeWithDefaults(this)
        startKoin {
            androidLogger()
            androidContext(this@AppTemplateSlimApplication)
            modules(listOf(interactorModule, repositoryModule ,applicationModule, viewModelModule))
        }
    }
}