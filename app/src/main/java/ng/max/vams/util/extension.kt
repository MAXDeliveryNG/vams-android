package ng.max.vams.util

import android.text.TextUtils
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import ng.max.vams.R

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