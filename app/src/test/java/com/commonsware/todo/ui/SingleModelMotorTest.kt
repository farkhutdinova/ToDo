package com.commonsware.todo.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.commonsware.todo.repo.ToDoModel
import com.commonsware.todo.repo.ToDoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SingleModelMotorTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testModel = ToDoModel("test model")
    private val repo = mock(ToDoRepository::class)
    private lateinit var underTest: SingleModelMotor

    @Before
    fun setUp() {
        When calling repo.find(testModel.id) itReturns MutableLiveData<ToDoModel>().apply {
            value = testModel
        }
        underTest = SingleModelMotor(repo, testModel.id, Dispatchers.Default)
    }

    @Test
    fun `initial state`() {
        underTest.states.observeForever { it.item shouldEqual testModel }
    }

    @Test
    fun `actions pass through to repo`() {
        val replacement = testModel.copy("whatevs")
        underTest.save(replacement)
        runBlocking { Verify on repo that repo.save(replacement) was called }
        underTest.delete(replacement)
        runBlocking { Verify on repo that repo.delete(replacement) was called }
    }
}