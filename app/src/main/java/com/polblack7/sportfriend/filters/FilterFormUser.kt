package com.polblack7.sportfriend.filters

import android.widget.Filter
import com.polblack7.sportfriend.models.ModelForm
import com.polblack7.sportfriend.adapters.AdapterFormUser

class FilterFormUser : Filter{

    var filterList: ArrayList<ModelForm>

    var adapterFormUser: AdapterFormUser

    constructor(filterList: ArrayList<ModelForm>, adapterFormUser: AdapterFormUser) {
        this.filterList = filterList
        this.adapterFormUser = adapterFormUser
    }

    override fun performFiltering(constraint: CharSequence): FilterResults {
        var constraint: CharSequence? = constraint
        val results = FilterResults()

        if (constraint != null && constraint.isNotEmpty()) {
            constraint = constraint.toString().toUpperCase()
            val filteredModels = ArrayList<ModelForm>()
            for (i in filterList.indices) {
                if (filterList[i].name.toUpperCase().contains(constraint)) {
                    filteredModels.add(filterList[i])
                }
            }
            results.count = filteredModels.size
            results.values = filteredModels
        } else {
            results.count = filterList.size
            results.values = filterList
        }
        return results
    }

    override fun publishResults(constraint: CharSequence, results: FilterResults) {
        adapterFormUser.formArrayList = results.values as ArrayList<ModelForm>
        adapterFormUser.notifyDataSetChanged()
    }

}