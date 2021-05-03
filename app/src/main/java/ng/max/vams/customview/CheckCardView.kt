package ng.max.vams.customview

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.card.MaterialCardView
import kotlinx.android.synthetic.main.layout_check_card_view.view.*
import kotlinx.android.synthetic.main.layout_check_state_view.view.*
import ng.max.vams.R

class CheckCardView @JvmOverloads constructor(
        context: Context, attributeSet: AttributeSet? = null, defStyleAttr: Int = 0
) : MaterialCardView(context, attributeSet, defStyleAttr) {


    init {
        inflate(context, R.layout.layout_check_card_view, this)
        val ta: TypedArray =
                context.obtainStyledAttributes(attributeSet, R.styleable.CheckCardView)
        checkCardTitleTv.text = ta.getString(R.styleable.CheckCardView_title)
        checkCardSubtitleTv.text = ta.getString(R.styleable.CheckCardView_subTitle)
        checkCardCountTv.text = ta.getString(R.styleable.CheckCardView_count)
        checkCardCountTv.setTextColor(ta.getColor(R.styleable.CheckCardView_countColor, 0))

        ta.recycle()
    }

    fun setCount(count : Int){
        checkCardCountTv.text = count.toString()
    }

    fun setDate(date: String){
        checkCardSubtitleTv.text = date
    }


}