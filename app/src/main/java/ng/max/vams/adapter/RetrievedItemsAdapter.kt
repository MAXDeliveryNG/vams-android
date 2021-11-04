package ng.max.vams.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.layout_retrieved_item.view.*
import ng.max.vams.databinding.LayoutRetrievedItemBinding

class RetrievedItemsAdapter : RecyclerView.Adapter<RetrievedItemsAdapter.RetrievedItemsViewholder>() {

    var retrievedItemsMap = mutableMapOf<String, Boolean>()
    var recoveredItems: List<String> = emptyList()
        set(newList) {
            field = newList
            notifyDataSetChanged()
        }
    var selectedItems: List<String> = emptyList()
        set(newList) {
            field = newList
            for (item in field){
                retrievedItemsMap[item] = true
            }
        }
    private var onItemClickListener: ((position: Int, isChecked: Boolean)->Unit)? = null


    override fun getItemCount(): Int = recoveredItems.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RetrievedItemsViewholder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val bnd = LayoutRetrievedItemBinding.inflate(layoutInflater, parent, false)
        return RetrievedItemsViewholder(
            bnd.root, onItemClickListener)
    }

    override fun onBindViewHolder(holder: RetrievedItemsAdapter.RetrievedItemsViewholder, position: Int) {
        holder.bind(recoveredItems[position])
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

        fun bind(item: String) = with(itemView) {
            itemCB.text = item
            itemCB.isChecked = retrievedItemsMap[item] ?: false
        }
    }
}