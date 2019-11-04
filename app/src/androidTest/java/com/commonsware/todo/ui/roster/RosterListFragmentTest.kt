package com.commonsware.todo.ui.roster

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.matcher.ViewMatchers.hasChildCount
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.runner.AndroidJUnit4
import com.commonsware.todo.R
import com.commonsware.todo.repo.ToDoModel
import com.commonsware.todo.repo.ToDoRepository
import com.commonsware.todo.ui.MainActivity
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.dsl.module.module
import org.koin.standalone.StandAloneContext.loadKoinModules

@RunWith(AndroidJUnit4::class)
class RosterListFragmentTest {

    @get:Rule
    val instantTaskExecutirRule = InstantTaskExecutorRule()

    private lateinit var repo: ToDoRepository
    private val items = listOf(
        ToDoModel("test model"),
        ToDoModel("another test model"),
        ToDoModel("one more test model")
    )

    @Before
    fun setUp() {
        repo = ToDoRepository()
        loadKoinModules(module {
            single(override = true) { repo }
        })
        runBlocking { items.forEach { repo.save(it) } }
    }

    @Test
    fun testListContents() {
        ActivityScenario.launch(MainActivity::class.java)

        onView(withId(R.id.items)).check(matches(hasChildCount(3)))
    }
}
