package ng.max.vams.customview

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.layout_check_state_view.view.*
import ng.max.vams.R

class CheckStateView @JvmOverloads constructor(
    context: Context, attributeSet: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attributeSet, defStyleAttr) {


    init {
        inflate(context, R.layout.layout_check_state_view, this)
        val ta: TypedArray =
            context.obtainStyledAttributes(attributeSet, R.styleable.CheckStateView)
        checkTitleTv.text = ta.getString(R.styleable.CheckStateView_title)
        checkSubtitleTv.text = ta.getString(R.styleable.CheckStateView_subTitle)
        val iconDrawable = ta.getDrawable(R.styleable.CheckStateView_icon)
        checkIcon.setImageDrawable(iconDrawable)


        ta.recycle()
    }
}