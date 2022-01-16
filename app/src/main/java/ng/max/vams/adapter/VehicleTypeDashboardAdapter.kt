package ng.max.vams.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.movement_type_vehicles_item.view.*
import ng.max.vams.data.remote.response.VehicleDashboardPair
import ng.max.vams.databinding.MovementTypeVehiclesItemBinding

class VehicleTypeDashboardAdapter {
//    RecyclerView.Adapter<VehicleTypeDashboardAdapter.VehicleTypeItemsDashboardViewholder>() {
//
//    var vehicleTypeItemsList = emptyList<VehicleDashboardPair>()
//
//    inner class VehicleTypeItemsDashboardViewholder(containerView: View) :
//        RecyclerView.ViewHolder(containerView) {
//        fun bind(item: VehicleDashboardPair) {
//            try{
//                itemView.entryitemonename.text = item.name
//                itemView.entryitemonecount.text = item.count.toString()
//            }catch (ex: NullPointerException){
//
//            }
//        }
//    }
//
//    override fun onCreateViewHolder(
//        parent: ViewGroup,
//        viewType: Int
//    ): VehicleTypeItemsDashboardViewholder {
//        val layoutInflater = LayoutInflater.from(parent.context)
//        val bnd = MovementTypeVehiclesItemBinding.inflate(layoutInflater, parent, false)
//        return VehicleTypeItemsDashboardViewholder(bnd.root)
//    }
//
//    override fun onBindViewHolder(holder: VehicleTypeItemsDashboardViewholder, position: Int) {
//        val item = vehicleTypeItemsList[position]
//        holder.bind(item)
//    }
//
//    override fun getItemCount(): Int = vehicleTypeItemsList.count()
}