package uz.kmax.timora.data.tools.security

import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import android.util.Base64
import java.security.SecureRandom

object Crypt {

    fun hashPassword(password: String,salt : ByteArray): String {
        val spec = PBEKeySpec(password.toCharArray(), salt, 10000, 256)
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val hash = factory.generateSecret(spec).encoded
        return Base64.encodeToString(hash, Base64.NO_WRAP)
    }

    fun generateSalt():ByteArray{
        val salt = ByteArray(16)
        SecureRandom().nextBytes(salt)
        return salt
    }
}