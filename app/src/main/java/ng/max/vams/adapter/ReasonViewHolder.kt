package ng.max.vams.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.layout_reason_list_item.view.*
import ng.max.vams.R
import ng.max.vams.data.remote.response.Reason

class ReasonViewHolder (itemView: View, onItemClickListener: ((position: Int) -> Unit)?):
    BaseViewHolder<Reason>(itemView, onItemClickListener){

    init {
        containerView.setOnClickListener {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                onItemClickListener?.invoke(position)
            }
        }
    }

    override fun bind(item: Reason) {
        itemView.titleTv.text = item.name
        itemView.subtitleTv.text = itemView.context.getString(R.string.default_reason_subtitle)
    }
}