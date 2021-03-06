package ng.max.vams.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.layout_reason_list_item.view.*
import ng.max.vams.R
import ng.max.vams.data.remote.response.Reason

class ReasonViewHolder (itemView: View, onItemClickListener: ((position: Int) -> Unit)?):
    BaseViewHolder<Reason>(itemView, onItemClickListener){

    init {
        itemView.setOnClickListener {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                onItemClickListener?.invoke(position)
            }
        }
    }

    override fun bind(item: Reason) {
        itemView.reasonItemView.setShowText(item.name)
        itemView.reasonItemView.reason_divider
        val imageResource: Int = when(item.slug){
            "maintenance" -> {
                R.drawable.ic_maintenance
            }
            "transfer" -> {
                R.drawable.ic_transfer
            }
            "time_off" -> {
                R.drawable.ic_timeoff
            }
            "retrieved" -> {
                R.drawable.ic_retreival
            }
            "completed_hp" -> {
                R.drawable.ic_hp_complete
            }
            "new" -> {
                R.drawable.ic_new
            }
            "activated" -> {
                R.drawable.ic_activated
            }
            else -> {
                R.drawable.ic_bike
            }
        }
        itemView.reasonItemView.setShowImage(imageResource)
    }
}