package uz.kmax.timora.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import uz.kmax.timora.data.room.repository.TaskRepository
import uz.kmax.timora.domain.entity.Task

class TaskViewModel(private val repository: TaskRepository) : ViewModel() {
    val allTasks = repository.getAllTasks()

    fun insert(task: Task) = viewModelScope.launch { repository.insert(task) }
    fun delete(task: Task) = viewModelScope.launch { repository.delete(task) }
}