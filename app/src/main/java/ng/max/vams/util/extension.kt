package ng.max.vams.util

import android.app.Activity
import android.content.Context
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.fragment.DialogFragmentNavigator
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.findNavController
import ng.max.vams.R
import ng.max.vams.util.Constant.DATETIME_DISPLAY_FORMAT
import ng.max.vams.util.Constant.SERVER_DATE_FORMAT
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

fun String?.notEmpty(): Boolean {
    return !TextUtils.isEmpty(this)
}

fun View.gone(){
    this.visibility = View.GONE
}

fun View.show(){
    this.visibility = View.VISIBLE
}

fun View.hide(){
    this.visibility = View.INVISIBLE
}

fun Fragment.showDialog(title : String, message : String, shouldLogout: Boolean = false) {
    val bundle = bundleOf("title" to title, "message" to message, "shouldLogOut" to shouldLogout)
    findNavController().navigate(R.id.appDialogFragment, bundle)
}

fun Fragment.hideKeypad(activity: Activity, view: View) {
    val inputMethodManager = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.applicationWindowToken, 0)
}

fun String.formatDate(): String{


    val dateFormatIn = SimpleDateFormat(SERVER_DATE_FORMAT, Locale.getDefault())
    return try {
        val dateIn = dateFormatIn.parse(this)!!
        val offset = TimeZone.getDefault().getOffset(dateIn.time)
        val cal = Calendar.getInstance().apply {
            time = dateIn
            add(Calendar.MILLISECOND, offset)
        }
        val dateFormatOut = SimpleDateFormat(DATETIME_DISPLAY_FORMAT, Locale.getDefault())
        dateFormatOut.format(cal.time)
    }catch (exception: ParseException) {
        SimpleDateFormat(DATETIME_DISPLAY_FORMAT, Locale.getDefault()).format(Date())
    } catch (exception: Exception) {
        SimpleDateFormat(DATETIME_DISPLAY_FORMAT, Locale.getDefault()).format(Date())
    }
}

fun Fragment.navigate(directions: NavDirections) {
    val controller = findNavController()
    val currentDestination = (controller.currentDestination as? FragmentNavigator.Destination)?.className
        ?: (controller.currentDestination as? DialogFragmentNavigator.Destination)?.className
    if (currentDestination == this.javaClass.name) {
        controller.navigate(directions)
    }
}