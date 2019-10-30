package com.commonsware.todo.repo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class ToDoRepository {
    private val _items = MutableLiveData<List<ToDoModel>>().apply { value = listOf() }
    val items: LiveData<List<ToDoModel>> = _items

    fun save(model: ToDoModel) {
        _items.value = if (current().any { it.id == model.id }) {
            current().map { if (it.id == model.id) model else it }
        } else {
            current() + model
        }
    }

    fun find(modelId: String?) = current().find { it.id == modelId }

    fun delete(modelId: String) {
        _items.value = current().filter { it.id != modelId }
    }

    private fun current() = _items.value!!
}