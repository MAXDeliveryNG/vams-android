package ng.max.vams.customview

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.layout_dashboard_stat_data_view.view.*
import ng.max.vams.R

class DashboardStatDataView @JvmOverloads constructor(
    context: Context, attributeSet: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attributeSet, defStyleAttr) {


    init {
        inflate(context, R.layout.layout_dashboard_stat_data_view, this)
        val ta: TypedArray =
            context.obtainStyledAttributes(attributeSet, R.styleable.DashboardStatDataView)
        try {
            itemNameTv.text = ta.getString(R.styleable.DashboardStatDataView_title)
            itemCountTv.text = ta.getString(R.styleable.DashboardStatDataView_count)
        } finally {
            ta.recycle()
        }
    }

    fun setData(count: String){
        itemCountTv.text = count
    }

}
