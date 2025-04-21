package uz.kmax.timora.data.tools.security

import android.util.Base64

object Decode {

    // Saltni ByteArray shakliga qaytarish
    fun getSaltAsByteArray(salt: String): ByteArray {
        return Base64.decode(salt, Base64.DEFAULT)
    }

    // Saltni ByteArray dan String ga o‘tkazish
    fun encodeSalt(saltBytes: ByteArray): String {
        return Base64.encodeToString(saltBytes, Base64.DEFAULT)
    }

}