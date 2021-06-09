package ng.max.vams.adapter

import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.layout_form_list_item.view.*
import ng.max.vams.R
import ng.max.vams.util.hide
import ng.max.vams.util.show

class FormListItemVieHolder (itemView: View, onItemClickListener: ((position: Int) -> Unit)?):
    BaseViewHolder<String>(itemView, onItemClickListener){
    var isItemSelected = false
    init {
        containerView.setOnClickListener {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                onItemClickListener?.invoke(position)
            }
        }
    }

    override fun bind(item: String) {
        itemView.listItem.text = item
        if (isItemSelected){
            itemView.listItem.setTextColor(ContextCompat.getColor(itemView.context, R.color.very_dark_gray))
            itemView.selectedIcon.show()
        }else{
            itemView.listItem.setTextColor(ContextCompat.getColor(itemView.context, R.color.very_dark_gray_50))
            itemView.selectedIcon.hide()
        }
    }
}