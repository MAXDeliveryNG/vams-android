package ng.max.vams.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ng.max.vams.R
import ng.max.vams.data.local.DbVehicle
import ng.max.vams.data.remote.response.Reason

class BaseAdapter : RecyclerView.Adapter<BaseViewHolder<*>>() {


    private var onItemClickListener: ((position: Int) -> Unit)? = null
    var adapterList = emptyList<Any>()
        set(newList) {
            field = newList
            notifyDataSetChanged()
        }
    var viewType = 0
    var selectedItemPosition = -1


    fun setOnItemClickListener(onItemClickListener: (position: Int) -> Unit) {
        this.onItemClickListener = onItemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        val layoutInflater = LayoutInflater.from(parent.context)

        return when(viewType){
            0 -> {
                val itemView = layoutInflater.inflate(R.layout.layout_vehicle_list_item, parent, false)
                VehicleViewHolder(itemView, onItemClickListener)
            }
            1 -> {
                val itemView = layoutInflater.inflate(R.layout.layout_reason_list_item, parent, false)
                ReasonViewHolder(itemView, onItemClickListener)
            }
            else -> {
                val itemView = layoutInflater.inflate(R.layout.layout_form_list_item, parent, false)
                FormListItemVieHolder(itemView, onItemClickListener)
            }
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        val item = adapterList[position]
        when (holder) {
            is ReasonViewHolder -> {
                holder.bind(item as Reason)
            }
            is VehicleViewHolder -> {
                holder.bind(item as DbVehicle)
            }
            is FormListItemVieHolder -> {
                holder.isItemSelected = selectedItemPosition == position
                holder.bind(item as String)
            }
        }
    }

    override fun getItemCount(): Int {
        return adapterList.count()
    }

    override fun getItemViewType(position: Int): Int {
        return when(viewType){
            0 -> TYPE_ASSET
            1 -> TYPE_REASON
            else -> TYPE_FORM_LIST_ITEM
        }
    }

    companion object {
        private const val TYPE_ASSET = 0
        private const val TYPE_REASON = 1
        private const val TYPE_FORM_LIST_ITEM = 2
    }
}