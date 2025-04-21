package uz.kmax.timora.domain.data.main

data class UserData(
    var id: String = "",
    var userName: String = "",
    var password : String = "",
    var passwordSalt : String = "",
    var name: String = "",
    var email: String = "",
    var avatarUrl: String = "",
    var createdAt: Long = 0L,
    var loginType: Int = 0,
    var gender : Int = 0
)