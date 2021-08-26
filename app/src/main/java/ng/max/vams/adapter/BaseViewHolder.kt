package ng.max.vams.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer

abstract class BaseViewHolder<T>(override val containerView: View, onItemClickListener: ((position: Int) -> Unit)?) :
    RecyclerView.ViewHolder(containerView), LayoutContainer{
    abstract fun bind(item: T)
}