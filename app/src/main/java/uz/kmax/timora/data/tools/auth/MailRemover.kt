package uz.kmax.timora.data.tools.auth

object MailRemover {
    fun remove(mailAddress : String):String{
        var newAddress = ""
        for (i in mailAddress.indices) {
            if (mailAddress[i] != '.' || mailAddress[i] != '@') {
                newAddress += "${mailAddress[i]}"
            }
        }
        return newAddress
    }
}