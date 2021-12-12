package ng.max.vams.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ng.max.vams.data.local.DbVehicle
import ng.max.vams.data.remote.response.Reason
import ng.max.vams.databinding.LayoutFormListItemBinding
import ng.max.vams.databinding.LayoutItemsBinding
import ng.max.vams.databinding.LayoutReasonListItemBinding
import ng.max.vams.databinding.LayoutVehicleListItemBinding

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
                val bnd = LayoutVehicleListItemBinding.inflate(layoutInflater, parent, false)
                VehicleViewHolder(bnd.root, onItemClickListener)
            }
            1 -> {
                val bnd = LayoutReasonListItemBinding.inflate(layoutInflater, parent, false)
                ReasonViewHolder(bnd.root, onItemClickListener)
            }
            2 -> {
                val bnd = LayoutFormListItemBinding.inflate(layoutInflater, parent, false)
                FormListItemVieHolder(bnd.root, onItemClickListener)
            }
            else -> {
                val bnd = LayoutItemsBinding.inflate(layoutInflater, parent, false)
                ItemsViewHolder(bnd.root, onItemClickListener)
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
                holder.bind(item)
            }
            is ItemsViewHolder -> {
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
            2 -> TYPE_FORM_LIST_ITEM
            else -> TYPE_RETRIEVED_ITEM
        }
    }

    companion object {
        private const val TYPE_ASSET = 0
        private const val TYPE_REASON = 1
        private const val TYPE_FORM_LIST_ITEM = 2
        private const val TYPE_RETRIEVED_ITEM = 3
    }
}