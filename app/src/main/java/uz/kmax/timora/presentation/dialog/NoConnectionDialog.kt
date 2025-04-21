package uz.kmax.timora.presentation.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import uz.kmax.timora.databinding.DialogNoConnectionBinding

class NoConnectionDialog {

    private var closeClickListener : (()-> Unit)? = null
    fun setOnCloseListener(f: ()-> Unit){ closeClickListener = f }

    fun show(context : Context){
        val dialog = Dialog(context,android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        val binding = DialogNoConnectionBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(binding.root)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)

        dialog.show()
    }
}