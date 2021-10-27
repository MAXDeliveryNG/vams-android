package ng.max.vams.adapter

import android.view.View
import kotlinx.android.synthetic.main.layout_reason_list_item.view.*
import ng.max.vams.R
import ng.max.vams.data.remote.response.Reason

class ReasonViewHolder (itemView: View, onItemClickListener: ((position: Int) -> Unit)?):
    BaseViewHolder<Reason>(itemView, onItemClickListener){

//    init {
//        itemView.setOnClickListener {
//            val position = adapterPosition
//            if (position != RecyclerView.NO_POSITION) {
//                onItemClickListener?.invoke(position)
//            }
//        }
//    }

    override fun bind(item: Reason) {
        itemView.titleTv.text = item.name
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
            else -> {
                R.drawable.ic_bike
            }
        }
        itemView.placeholderIV.setImageResource(imageResource)
    }
}