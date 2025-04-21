package uz.kmax.timora.domain.data.main

data class NotificationListData(
    var id : String = "",
    var title : String = "",
    var message : String = "",
    var timestamp: Long = 0L,
    var isReady : Boolean = false,
    var type : Int = 0
)