package uz.kmax.timora.presentation.dialog.work

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import uz.kmax.timora.databinding.DialogDeleteNotificationBinding

class DeleteNotification {
    private var closeClickListener : (()-> Unit)? = null
    fun setOnNotifyCloseListener(f: ()-> Unit){ closeClickListener = f }

    private var deleteClickListener : (()-> Unit)? = null
    fun setDeleteNotifyCloseListener(f: ()-> Unit){ deleteClickListener = f }

    fun show(context : Context){
        val dialog = Dialog(context,android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        val binding = DialogDeleteNotificationBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(binding.root)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)

        binding.deleteNo.setOnClickListener {
            dialog.dismiss()
        }

        binding.deleteYes.setOnClickListener {
            deleteClickListener?.invoke()
            dialog.dismiss()
        }

        dialog.show()
    }
}