package uz.kmax.timora.data.tools.auth

import java.util.regex.Pattern

class Regex(str: String?) {
    companion object {
        /**
         * - Emailni aniqlash uchun Regular Exception (REGEX)
         * - Bu kod Emaillikka tekshiradi .
         */
        fun emailCheck(email: String) {
            val email_Regex = "^([a-z0-9._%+-]{3,20})+@([a-z0-9.-]{4,20})+\\.([a-z]){2,6}+$"
            val pattern = Pattern.compile(email_Regex)
            val matcher = pattern.matcher(email)
            var checkEmail = false
            while (matcher.find()) {
                checkEmail = true
            }
            if (checkEmail) {
                println("COOL Next Step ->")
            } else {

            }
        }

        /**
         * - Password Check code
         * - Bu kod Parollikka tekshiradi :
         * - Bitta Katta harf
         * - Bitta Kichik harf
         * - Bitta belgi
         * - Uzunligi min : 8 , max : 20
         */
        fun passwordCheck(password: String) {
            val password_Regex =
                "^(?=.*[0-9])(?=.*[A-Z])(?=.*[a-z])(?=.*[@#$%^&\\-+=()])(?=\\S+$).{8,20}$"
            val pattern = Pattern.compile(password_Regex)
            val matcher = pattern.matcher(password)
            var checkPassword = false
            while (matcher.find()) {
                checkPassword = true
            }
            if (checkPassword) {
                println("Well Done Next Step -> ")
            } else {

            }
        }

        /**
         * - Bu kod Ismlikka tekshiradi
         * - Agar foydalanuvchi behosdan String o'rniga raqam yoki belgi kiritib qo'ysa Xatolik paydo bo'ladi .
         * - Uzunligi Min : 3 , Max : 10.
         * -  */
        fun checkName(name: String) {
            val name_Regex = "^([a-z|A-Z]){3,10}+$"
            val pattern = Pattern.compile(name_Regex)
            val matcher = pattern.matcher(name)
            var checkName = false
            while (matcher.find()) {
                checkName = true
            }
            if (checkName) {
                println("Good Job $name Next Step -> ")
            } else {

            }
        }

        /**
         * - Yoshni aniqlash uchun Regular Exception
         * - 0 dan to 999 gacha kiritish mumkin
         */
        fun ageCheck(age: String): Boolean {
            val ageRegex = "^([0-9]){1,3}+$"
            val pattern = Pattern.compile(ageRegex)
            val matcher = pattern.matcher(age)
            while (matcher.find()) {
                return true
            }
            return false
        }
    }
}