package ng.max.vams.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.layout_vehicle_list_item.view.*
import ng.max.vams.data.local.DbVehicle

class VehicleViewHolder (itemView: View, onItemClickListener: ((position: Int) -> Unit)?):
    BaseViewHolder<DbVehicle>(itemView, onItemClickListener){

    init {
        itemView.setOnClickListener {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                onItemClickListener?.invoke(position)
            }
        }
    }

    override fun bind(item: DbVehicle) {
        itemView.titleTv.text = item.maxVehicleId
        itemView.reasonTv.text = item.lastVehicleMovement?.reason
//        itemView.reasonTv.text = item.lastVehicleMovement?.reason?.name
    }
}