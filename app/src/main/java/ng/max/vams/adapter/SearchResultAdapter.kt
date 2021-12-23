package ng.max.vams.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import ng.max.vams.R
import ng.max.vams.data.remote.response.RemoteVehicle

class SearchResultsAdapter(
    context: Context,
    var items: ArrayList<RemoteVehicle> = ArrayList()
) :
    ArrayAdapter<RemoteVehicle>(context, R.layout.spinner_item, items) {

    override fun getCount(): Int {
        return items.size
    }

    override fun getItem(position: Int): RemoteVehicle? {
        return items[position]
    }

    fun update(items: ArrayList<RemoteVehicle>?) {
        if (items == null) {
            this.items = ArrayList()
        } else {
            this.items = items
        }

        notifyDataSetChanged()
    }

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: TextView =
            LayoutInflater.from(context).inflate(
                R.layout.spinner_item, parent, false
            ) as TextView
        view.text = getItem(position)?.plateNumber

        return view
    }
}