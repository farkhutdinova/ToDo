package com.commonsware.todo.repo

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.jraska.livedata.test
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.shouldBeEmpty
import org.amshove.kluent.shouldContainSame
import org.amshove.kluent.shouldEqual
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ToDoRepositoryTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var underTest: ToDoRepository

    @Before
    fun setUp() {
        underTest = ToDoRepository()
    }

    @Test
    fun `can add items`() {
        val testModel = ToDoModel("test model")

        underTest.apply {
            items.test().value().shouldBeEmpty()

            runBlocking { save(testModel) }

            items.test().value() shouldContainSame listOf(testModel)

            find(testModel.id).test().value() shouldEqual testModel
        }
    }

    @Test
    fun `can modify items`() {
        val testModel = ToDoModel("test model")
        val replacement = testModel.copy(notes = "some changes")

        underTest.apply {
            items.test().value().shouldBeEmpty()

            runBlocking { save(testModel) }

            items.test().value() shouldContainSame listOf(testModel)

            runBlocking { save(replacement) }

            items.test().value() shouldContainSame listOf(replacement)
        }
    }

    @Test
    fun `can delete items`() {
        val testModel = ToDoModel("test model")

        underTest.apply {
            items.test().value().shouldBeEmpty()

            runBlocking { save(testModel) }

            items.test().value() shouldContainSame listOf(testModel)

            runBlocking { delete(testModel) }

            items.test().value().shouldBeEmpty()
        }
    }
}
