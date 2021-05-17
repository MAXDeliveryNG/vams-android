package ng.max.vams.util

import java.text.SimpleDateFormat
import java.util.*

class Helper {

    companion object {

        val supportedLocation = listOf("Bodija", "Eleyele", "Oregun", "Akure", "Gbamu Gbamu")
        fun getFormattedDate(): String {
            var tmp = SimpleDateFormat("d", Locale.getDefault())
            val date = Calendar.getInstance()

            var str: String = tmp.format(date.timeInMillis)

            str = if (date.get(Calendar.DAY_OF_MONTH) in 11..13) {
                str + "th "
            } else {
                when {
                    str.endsWith("1") -> str + "st "
                    str.endsWith("2") -> str + "nd "
                    str.endsWith("3") -> str + "rd "
                    else -> str + "th "
                }
            }

            tmp = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
            str += tmp.format(date.timeInMillis)

            return "Today, $str"
        }
    }
}

const val ARG_OBJECT = "object"
