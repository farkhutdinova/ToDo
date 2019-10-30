package com.commonsware.todo

import android.app.Application
import com.commonsware.todo.repo.ToDoRepository
import org.koin.android.ext.android.startKoin
import org.koin.dsl.module.module

class ToDoApp: Application() {

    private val koinModule = module {
        single { ToDoRepository() }
    }

    override fun onCreate() {
        super.onCreate()

        startKoin(this, listOf(koinModule))
    }
}