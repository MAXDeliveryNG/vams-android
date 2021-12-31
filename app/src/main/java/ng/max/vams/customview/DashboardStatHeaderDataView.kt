package ng.max.vams.customview

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.layout_dashboard_stat_header_data_view.view.*
import ng.max.vams.R

class DashboardStatHeaderDataView @JvmOverloads constructor(
    context: Context, attributeSet: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attributeSet, defStyleAttr) {


    init {
        inflate(context, R.layout.layout_dashboard_stat_header_data_view, this)
        val ta: TypedArray =
            context.obtainStyledAttributes(attributeSet, R.styleable.DashboardStatHeaderDataView)
        try {
            headerTitleTv.text = ta.getString(R.styleable.DashboardStatHeaderDataView_title)
            headerCountTv.text = ta.getString(R.styleable.DashboardStatHeaderDataView_count)
            headerSubTitleTv.text = ta.getString(R.styleable.DashboardStatHeaderDataView_subtitle)
            headerCountTv.setTextColor(ta.getColor(R.styleable.DashboardStatHeaderDataView_countColor, 0))
        } finally {
            ta.recycle()
        }
    }

    fun rotateArrow(angle: Float){
        arrowView.rotation = angle
    }
    fun setSubtitle(subtitle: String){
        headerSubTitleTv.text = subtitle
    }
    fun setCount(count: String){
        headerCountTv.text = count
    }

}
