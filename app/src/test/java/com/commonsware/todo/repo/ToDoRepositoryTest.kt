package com.commonsware.todo.repo

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
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
        underTest = ToDoRepository(TestStore())
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

class TestStore : ToDoEntity.Store {
    private val _items =
        MutableLiveData<List<ToDoEntity>>().apply { value = listOf() }

    override fun all(): LiveData<List<ToDoEntity>> = _items

    override suspend fun save(vararg entities: ToDoEntity) {
        entities.forEach { entity ->
            _items.value = if (current().any { it.id == entity.id }) {
                current().map { if (it.id == entity.id) entity else it }
            } else {
                current() + entity
            }
        }
    }

    override suspend fun delete(vararg entities: ToDoEntity) {
        entities.forEach { entity ->
            _items.value = current().filter { it.id != entity.id }
        }
    }

    override fun find(modelId: String): LiveData<ToDoEntity> =
        Transformations.map(_items) {
            it.find { model -> model.id == modelId }
        }

    private fun current() = _items.value!!
}