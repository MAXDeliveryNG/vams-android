package ng.max.vams.customview

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.layout_movement_type_state_view.view.*
import ng.max.vams.R

class MovementTypeStateView @JvmOverloads constructor(
    context: Context, attributeSet: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attributeSet, defStyleAttr) {


    init {
        inflate(context, R.layout.layout_movement_type_state_view, this)
        val ta: TypedArray =
            context.obtainStyledAttributes(attributeSet, R.styleable.MovementTypeStateView)
        typeTitleTv.text = ta.getString(R.styleable.MovementTypeStateView_title)
        typeSubtitleTv.text = ta.getString(R.styleable.MovementTypeStateView_subTitle)
        val iconDrawable = ta.getDrawable(R.styleable.MovementTypeStateView_icon)
        typeIcon.setImageDrawable(iconDrawable)


        ta.recycle()
    }
}