package uz.kmax.timora.data.tools.auth

import java.util.regex.Pattern

object VerifyRegistration {

    fun emailCheck(email: String) : Boolean{
        val emailRegex = "^([a-z0-9._%+-]{3,20})+@([a-z0-9.-]{4,20})+\\.([a-z]){2,6}+$"
        val pattern = Pattern.compile(emailRegex)
        val matcher = pattern.matcher(email)
        var checkEmail = false
        while (matcher.find()) {
            checkEmail = true
        }
        return checkEmail
    }

    fun passwordCheck(password: String) : Boolean{
        val passwordRegex =
            "^(?=.*[0-9])(?=.*[A-Z])(?=.*[a-z])(?=.*[@#$%^&\\-+=()])(?=\\S+$).{8,20}$"
        val pattern = Pattern.compile(passwordRegex)
        val matcher = pattern.matcher(password)
        var checkPassword = false
        while (matcher.find()) {
            checkPassword = true
        }
        return  checkPassword
    }

    fun checkName(name: String): Boolean {
        val nameRegex = "^([a-z|A-Z]){3,10}+$"
        val pattern = Pattern.compile(nameRegex)
        val matcher = pattern.matcher(name)
        var checkName = false
        while (matcher.find()) {
            checkName = true
        }
        return checkName
    }
}