package ng.max.vams.adapter

import android.view.View
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.layout_form_list_item.view.*
import ng.max.vams.R
import ng.max.vams.data.remote.response.SubReason
import ng.max.vams.util.gone
import ng.max.vams.util.hide
import ng.max.vams.util.show

class FormListItemVieHolder (itemView: View, onItemClickListener: ((position: Int) -> Unit)?):
    BaseViewHolder<Any>(itemView, onItemClickListener){
    var isItemSelected = false


    override fun bind(item: Any) {
        if (item is SubReason) {
            itemView.listItem.text = item.name
            itemView.subtitleTV.text = item.definition
        }else{
            itemView.listItem.text = item as String
            itemView.subtitleTV.gone()
        }
        if (isItemSelected){
            itemView.listItem.setTextColor(ContextCompat.getColor(itemView.context, R.color.very_dark_gray))
            itemView.selectedIcon.show()
        }else{
            itemView.listItem.setTextColor(ContextCompat.getColor(itemView.context, R.color.very_dark_gray_50))
            itemView.selectedIcon.hide()
        }
    }
}