package com.commonsware.todo.repo

class ToDoRepository {
    var items = listOf<ToDoModel>()

    fun save(model: ToDoModel) {
        items = if (items.any { it.id == model.id }) {
            items.map { if (it.id == model.id) model else it }
        } else {
            items + model
        }
    }

    fun find(modelId: String?) = items.find { it.id == modelId }

    fun delete(modelId: String) {
        items = items.filter { it.id != modelId }
    }
}