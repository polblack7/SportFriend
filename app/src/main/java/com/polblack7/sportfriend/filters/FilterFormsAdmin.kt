package com.polblack7.sportfriend.filters

import android.widget.Filter
import com.polblack7.sportfriend.models.ModelForm
import com.polblack7.sportfriend.adapters.AdapterForms

class FilterFormsAdmin : Filter {

    var filterList: ArrayList<ModelForm>

    var adapterForms: AdapterForms

    constructor(filterList: ArrayList<ModelForm>, adapterForms: AdapterForms) {
        this.filterList = filterList
        this.adapterForms = adapterForms
    }

    override fun performFiltering(constraint: CharSequence?): FilterResults {
        val results = FilterResults()

        if (constraint != null && constraint.isNotEmpty()) {
            val search = constraint.toString().toUpperCase()

            val filteredModels = ArrayList<ModelForm>()

            for (i in filterList.indices) {
                if (filterList[i].name.toUpperCase().contains(search)) {
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

    override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
        adapterForms.formArrayList = results!!.values as ArrayList<ModelForm>
        adapterForms.notifyDataSetChanged()
    }


}