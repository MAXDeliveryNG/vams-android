package ng.max.vams.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.layout_notification_view_item.view.*
import ng.max.vams.data.local.DbVehicle

class NotificationItemViewHolder (itemView: View, onItemClickListener: ((position: Int) -> Unit)?):
    BaseViewHolder<DbVehicle>(itemView, onItemClickListener){

    init {
        itemView.notificationContinueBtn.setOnClickListener {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                onItemClickListener?.invoke(position)
            }
        }
    }

    override fun bind(item: DbVehicle) {
        itemView.vehicleTypeTv.text = item.lastVehicleMovement?.vehicleType
        itemView.manufacturerTv.text = item.manufacturer?.name
        itemView.locationFromTv.text = item.lastVehicleMovement?.locationFromName!!.lowercase().replaceFirstChar{it.titlecase()}
        itemView.locationToTv.text = item.lastVehicleMovement.locationToName!!.lowercase().replaceFirstChar{it.titlecase()}
    }
}