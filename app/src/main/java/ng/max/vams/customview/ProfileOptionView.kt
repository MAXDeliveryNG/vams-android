package ng.max.vams.customview

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.layout_profile_option_view.view.*
import ng.max.vams.R

class ProfileOptionView @JvmOverloads constructor(
    context: Context, attributeSet: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attributeSet, defStyleAttr) {


    init {
        inflate(context, R.layout.layout_profile_option_view, this)
        val ta: TypedArray =
            context.obtainStyledAttributes(attributeSet, R.styleable.ProfileOptionView)
        optionTv.text = ta.getString(R.styleable.ProfileOptionView_title)
        val iconDrawable = ta.getDrawable(R.styleable.ProfileOptionView_icon)
        optionIcon.setImageDrawable(iconDrawable)


        ta.recycle()
    }
}