package ng.max.vams.adapter

import android.view.View
import kotlinx.android.synthetic.main.layout_items.view.*

class ItemsViewHolder(itemView: View, onItemClickListener: ((position: Int) -> Unit)?):
    BaseViewHolder<String>(itemView, onItemClickListener){


    override fun bind(item: String) {
        itemView.itemTV.text = item
    }
}