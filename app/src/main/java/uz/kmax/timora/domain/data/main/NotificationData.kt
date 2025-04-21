package uz.kmax.timora.domain.data.main

import uz.kmax.timora.data.enums.TaskPriority

data class NotificationData(
    var id : String = "",
    var title : String = "",
    var description : String = "",
    var dueDate : Long = 0L,
    var isCompleted : Boolean = false,
    var isTurned : Boolean = true,
    var priority : TaskPriority = TaskPriority.MEDIUM,
    var createdAt : Long = 0L,
    var location : LocationData? = null,
    var type : Int = 0
)