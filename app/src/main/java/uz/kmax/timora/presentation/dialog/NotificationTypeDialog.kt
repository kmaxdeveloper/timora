package uz.kmax.timora.presentation.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import uz.kmax.timora.databinding.DialogSetTypeBinding
import java.util.Date

class NotificationTypeDialog {
    private var typeClickListener : ((type : Int)-> Unit)? = null
    fun setOnTypeListener(f: (type : Int)-> Unit){ typeClickListener = f }

    fun show(context: Context){
        val dialog = Dialog(context,android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        val binding = DialogSetTypeBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(binding.root)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)

        binding.closeBtn.setOnClickListener {
            dialog.dismiss()
        }

        binding.timeNotificationBtn.setOnClickListener {
            typeClickListener?.invoke(1)
            dialog.dismiss()
        }

        binding.locationNotificationBtn.setOnClickListener {
            typeClickListener?.invoke(2)
            dialog.dismiss()
        }

        dialog.show()
    }
}