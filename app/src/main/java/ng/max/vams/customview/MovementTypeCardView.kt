package ng.max.vams.customview

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.View
import com.google.android.material.card.MaterialCardView
import kotlinx.android.synthetic.main.home_fragment.view.*
import kotlinx.android.synthetic.main.layout_movement_type_card_view.view.*
import ng.max.vams.R

class MovementTypeCardView @JvmOverloads constructor(
        context: Context, attributeSet: AttributeSet? = null, defStyleAttr: Int = 0
) : MaterialCardView(context, attributeSet, defStyleAttr) {


    init {
        inflate(context, R.layout.layout_movement_type_card_view, this)
        val ta: TypedArray =
                context.obtainStyledAttributes(attributeSet, R.styleable.MovementTypeCardView)
        cardTitleTv.text = ta.getString(R.styleable.MovementTypeCardView_title)
        cardSubtitleTv.text = ta.getString(R.styleable.MovementTypeCardView_subTitle)
        cardCountTv.text = ta.getString(R.styleable.MovementTypeCardView_count)
        cardCountTv.setTextColor(ta.getColor(R.styleable.MovementTypeCardView_countColor, 0))
        card_detail_wrap.visibility = View.GONE
        linedivider.visibility = View.GONE
        ta.recycle()
    }

    fun setCount(count : Int){
        cardCountTv.text = count.toString()
    }

    fun setDate(date: String){
        cardSubtitleTv.text = date
    }

    fun setVisibility(clicked: Boolean){
        if(clicked) {
            card_detail_wrap.visibility = View.GONE
            linedivider.visibility = View.GONE
        }else{
            card_detail_wrap.visibility = View.VISIBLE
            linedivider.visibility = View.VISIBLE
        }
    }


}