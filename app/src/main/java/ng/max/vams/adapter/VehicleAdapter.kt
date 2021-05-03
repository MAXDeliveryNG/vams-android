package ng.max.vams.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.layout_vehicle_list_item.view.*
import ng.max.vams.R
import ng.max.vams.data.local.DbVehicle

class VehicleAdapter : ListAdapter<DbVehicle, VehicleAdapter.ViewHolder>(
        object : DiffUtil.ItemCallback<DbVehicle>() {

            override fun areItemsTheSame(oldItem: DbVehicle, newItem: DbVehicle): Boolean =
                    oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: DbVehicle, newItem: DbVehicle): Boolean =
                    oldItem == newItem

        }) {

    private var onItemClickListener: ((position: Int) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemView = layoutInflater.inflate(R.layout.layout_vehicle_list_item, parent, false)
        return ViewHolder(itemView, onItemClickListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun setOnItemClickListener(onItemClickListener: (position: Int) -> Unit) {
        this.onItemClickListener = onItemClickListener
    }

    public override fun getItem(position: Int): DbVehicle = super.getItem(position)

    class ViewHolder(override val containerView: View, onItemClickListener: ((position: Int) -> Unit)?) :
            RecyclerView.ViewHolder(containerView), LayoutContainer {

        init {
            containerView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClickListener?.invoke(position)
                }
            }
        }

        fun bind(item: DbVehicle) {
            item.run {
                containerView.vehicleIdTv.text = maxVehicleId
                containerView.reasonTv.text = "Maintenance"
            }
        }

    }

}