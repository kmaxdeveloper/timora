package uz.kmax.timora.presentation.dialog.tool

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import uz.kmax.timora.databinding.DialogLogoutBinding

class LogOutDialog {

    private var closeClickListener : (()-> Unit)? = null
    fun setOnCloseListener(f: ()-> Unit){ closeClickListener = f }

    private var logOutClickListener : (()-> Unit)? = null
    fun setLogOutCloseListener(f: ()-> Unit){ logOutClickListener = f }

    fun show(context : Context){
        val dialog = Dialog(context,android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        val binding = DialogLogoutBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(binding.root)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)

        binding.logOutNo.setOnClickListener {
            dialog.dismiss()
        }

        binding.logOutYes.setOnClickListener {
            logOutClickListener?.invoke()
            dialog.dismiss()
        }

        dialog.show()
    }
}