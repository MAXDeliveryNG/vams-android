package ng.max.vams.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class BaseViewHolder<T>(containerView: View, onItemClickListener: ((position: Int) -> Unit)?) :
    RecyclerView.ViewHolder(containerView){

    init {
        containerView.setOnClickListener {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                onItemClickListener?.invoke(position)
            }
        }
    }
    abstract fun bind(item: T)
}