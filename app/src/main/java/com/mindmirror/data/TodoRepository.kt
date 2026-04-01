package com.mindmirror.data

import kotlinx.coroutines.flow.Flow

class TodoRepository(private val todoDao: TodoDao) {
    
    fun getAllTodos(): Flow<List<TodoEntity>> = todoDao.getAllTodos()

    suspend fun addTodo(text: String): Long {
        return todoDao.insert(TodoEntity(text = text))
    }

    suspend fun updateTodo(todo: TodoEntity) {
        todoDao.update(todo)
    }

    suspend fun toggleTodoComplete(todo: TodoEntity) {
        todoDao.update(todo.copy(isCompleted = !todo.isCompleted))
    }

    suspend fun deleteTodo(todo: TodoEntity) {
        todoDao.delete(todo)
    }

    suspend fun deleteTodoById(id: Long) {
        todoDao.deleteById(id)
    }
}
