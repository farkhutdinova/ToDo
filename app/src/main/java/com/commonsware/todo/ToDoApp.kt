package com.commonsware.todo

import android.app.Application
import android.text.format.DateUtils
import com.commonsware.todo.repo.ToDoDatabase
import com.commonsware.todo.repo.ToDoRepository
import com.commonsware.todo.report.RosterReport
import com.commonsware.todo.ui.SingleModelMotor
import com.commonsware.todo.ui.roster.RosterMotor
import com.github.jknack.handlebars.Handlebars
import com.github.jknack.handlebars.Helper
import org.koin.android.ext.android.startKoin
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module
import java.util.*

class ToDoApp : Application() {

    private val koinModule = module {
        single {
            val db: ToDoDatabase = get()
            ToDoRepository(db.todoStore())
        }
        single { ToDoDatabase.newInstance(androidContext()) }
        single {
            Handlebars().apply {
                registerHelper("dateFormat", Helper<Calendar> { value, _ ->
                    DateUtils
                        .getRelativeDateTimeString(
                            androidContext(), value.timeInMillis,
                            DateUtils.MINUTE_IN_MILLIS, DateUtils.WEEK_IN_MILLIS, 0
                        )
                })
            }
        }
        single { RosterReport(androidContext(), get()) }
        viewModel { RosterMotor(get(), get(), get()) }
        viewModel { (modelId: String) -> SingleModelMotor(get(), modelId) }
    }

    override fun onCreate() {
        super.onCreate()

        startKoin(this, listOf(koinModule))
    }
}