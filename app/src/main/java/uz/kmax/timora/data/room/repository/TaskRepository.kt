package uz.kmax.timora.data.room.repository

import uz.kmax.timora.data.room.dao.TaskDao
import uz.kmax.timora.domain.entity.Task

class TaskRepository(private val taskDao: TaskDao) {
    fun getAllTasks() = taskDao.getAllTasks()
    suspend fun insert(task: Task) = taskDao.insert(task)
    suspend fun delete(task: Task) = taskDao.delete(task)
}