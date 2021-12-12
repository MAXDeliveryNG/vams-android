package ng.max.vams.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.layout_retrieved_item.view.*
import ng.max.vams.data.remote.response.RetrivalChecklistItem
import ng.max.vams.databinding.LayoutRetrievedItemBinding

class RetrievedItemsAdapter : RecyclerView.Adapter<RetrievedItemsAdapter.RetrievedItemsViewholder>() {

    var retrievedItemsMap = mutableMapOf<String, Boolean>()
    var retrievedItems: List<RetrivalChecklistItem> = emptyList()
        set(newList) {
            field = newList
            notifyDataSetChanged()
        }
    var selectedItems: List<RetrivalChecklistItem> = emptyList()
        set(newList) {
            field = newList
            for (item in field){
                retrievedItemsMap[item.name] = true
            }
        }
    private var onItemClickListener: ((position: Int, isChecked: Boolean)->Unit)? = null


    override fun getItemCount(): Int = retrievedItems.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RetrievedItemsViewholder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val bnd = LayoutRetrievedItemBinding.inflate(layoutInflater, parent, false)
        return RetrievedItemsViewholder(
            bnd.root, onItemClickListener)
    }

    override fun onBindViewHolder(holder: RetrievedItemsAdapter.RetrievedItemsViewholder, position: Int) {
        retrievedItems[position].let { holder.bind(it) }
    }

    fun setOnItemClickListener(onItemClickListener: (position: Int, isChecked: Boolean)->Unit) {
        this.onItemClickListener = onItemClickListener
    }

    inner class RetrievedItemsViewholder(private val containerView: View, onItemClickListener: ((position: Int, isChecked: Boolean)->Unit)?) :
        RecyclerView.ViewHolder(containerView){

        init {

            containerView.itemCB.setOnCheckedChangeListener { _, isChecked ->
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClickListener?.invoke(position, isChecked)
                }
            }
        }

        fun bind(item: RetrivalChecklistItem) = with(itemView) {
            itemCB.text = item.name
            itemCB.isChecked = retrievedItemsMap[item.id] ?: false
        }
    }
}