package ng.max.vams.customview

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.layout_vehicle_data_view.view.*
import ng.max.vams.R

class VehicleDataView @JvmOverloads constructor(
    context: Context, attributeSet: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attributeSet, defStyleAttr) {

    init {
        inflate(context, R.layout.layout_vehicle_data_view, this)
        val ta: TypedArray =
            context.obtainStyledAttributes(attributeSet, R.styleable.VehicleDataView)
        titleTv.text = ta.getString(R.styleable.VehicleDataView_title)
        subtitleTV.text = ta.getString(R.styleable.VehicleDataView_subtitle)

        ta.recycle()
    }

    fun setSubtitle(value: String){
        subtitleTV.text = value
    }
}