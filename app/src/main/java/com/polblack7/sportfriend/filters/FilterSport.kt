package com.polblack7.sportfriend.filters

import android.widget.Filter
import com.polblack7.sportfriend.models.ModelSport
import com.polblack7.sportfriend.adapters.AdapterSport
import java.util.ArrayList


class FilterSport : Filter {
    private var filterList: ArrayList<ModelSport>

    private var adapterSport: AdapterSport

    constructor(filterList: ArrayList<ModelSport>, adapterSport: AdapterSport) {
        this.filterList = filterList
        this.adapterSport = adapterSport
    }

    override fun performFiltering(constraint: CharSequence?): FilterResults {
        val results = FilterResults()
        if (constraint != null && constraint.isNotEmpty()) {
            val search = constraint.toString().toUpperCase()
            val filterSports = ArrayList<ModelSport>()
            for (i in filterList.indices) {
                if (filterList[i].sport!!.toUpperCase().contains(search)) {
                    filterSports.add(filterList[i])
                }
            }
            results.count = filterSports.size
            results.values = filterSports
        } else {
            results.count = filterList.size
            results.values = filterList
        }
        return results
    }

    override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
        adapterSport.sportArrayList = results!!.values as ArrayList<ModelSport>
        adapterSport.notifyDataSetChanged()

    }


}