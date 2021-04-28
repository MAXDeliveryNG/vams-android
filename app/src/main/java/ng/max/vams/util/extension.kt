package ng.max.vams.util

import android.text.TextUtils
import android.view.View

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