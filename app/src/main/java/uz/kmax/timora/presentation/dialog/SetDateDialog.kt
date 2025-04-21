package uz.kmax.timora.presentation.dialog

import android.app.TimePickerDialog
import android.content.Context
import android.icu.util.Calendar
import androidx.core.graphics.scaleMatrix
import androidx.fragment.app.FragmentActivity
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.CalendarConstraints.DateValidator
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import java.util.Date

class SetDateDialog {
    fun show(context: Context,fragment : FragmentActivity,onDateTimeSelected : (Long) -> Unit){
        val calendar = Calendar.getInstance()
        val today = calendar.apply {
            set(Calendar.HOUR_OF_DAY,0)
            set(Calendar.MINUTE,0)
            set(Calendar.SECOND,0)
            set(Calendar.MILLISECOND,0)
        }
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Sanani tanlang :")
            .setSelection((MaterialDatePicker.todayInUtcMilliseconds()))
            .setCalendarConstraints(CalendarConstraints.Builder()
                .setValidator(DateValidatorPointForward.from(today.timeInMillis))
                .setStart(today.timeInMillis).build())
            .build()
        datePicker.addOnPositiveButtonClickListener { selectedDate->
            calendar.timeInMillis = selectedDate
            TimePickerDialog(context,
                { _, hour,minute->
                calendar.set(Calendar.HOUR_OF_DAY,hour)
                calendar.set(Calendar.MINUTE,minute)
                    onDateTimeSelected(calendar.timeInMillis)
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
                true
            ).show()
        }

        datePicker.show(fragment.supportFragmentManager,"DATE_PICKER")
    }
}