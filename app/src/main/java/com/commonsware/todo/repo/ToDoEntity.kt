package com.commonsware.todo.repo

import androidx.lifecycle.LiveData
import androidx.room.*
import java.util.*

@Entity(tableName = "todos", indices = [Index(value = ["id"])])
data class ToDoEntity(
    val description: String,
    @field:PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val notes: String = "",
    val createdOn: Calendar = Calendar.getInstance(),
    val isCompleted: Boolean = false
) {
    @Dao
    interface Store {
        @Query("SELECT * FROM todos ORDER BY description")
        fun all(): LiveData<List<ToDoEntity>>

        @Query("SELECT * FROM todos WHERE id = :modelId")
        fun find(modelId: String): LiveData<ToDoEntity>

        @Query("SELECT * FROM todos WHERE isCompleted = :isCompleted ORDER BY description")
        fun filtered(isCompleted: Boolean): LiveData<List<ToDoEntity>>

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        suspend fun save(vararg entities: ToDoEntity)

        @Delete
        suspend fun delete(vararg entities: ToDoEntity)
    }

    constructor(model: ToDoModel) : this(
        id = model.id,
        description = model.description,
        isCompleted = model.isCompleted,
        notes = model.notes,
        createdOn = model.createdOn
    )

    fun toModel(): ToDoModel {
        return ToDoModel(
            id = id,
            description = description,
            isCompleted = isCompleted,
            notes = notes,
            createdOn = createdOn
        )
    }
}