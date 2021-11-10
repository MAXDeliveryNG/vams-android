package ng.max.vams.adapter

import android.content.Context
import android.widget.ArrayAdapter
import android.widget.Filter

/**
 * Source: https://gist.githubusercontent.com/rmirabelle/7670cb01627ac530ce3e5d47d9785258/raw/93b7b3f5b426ddfd54ffd574cb31733b385ae1c1/NoFilterArrayAdapter.kt
 */
open class SpinnerAdapter<String>(
    context: Context,
    layout: Int,
    var values: Array<String>
) : ArrayAdapter<String>(context, layout, values) {

    protected val filterThatDoesNothing = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val results = FilterResults()
            results.values = values
            results.count = values.size
            return results
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            notifyDataSetChanged()
        }
    }

    override fun getFilter(): Filter {
        return filterThatDoesNothing
    }
}