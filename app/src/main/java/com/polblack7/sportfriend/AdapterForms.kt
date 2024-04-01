package com.polblack7.sportfriend
import android.widget.TextView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView
import com.polblack7.sportfriend.databinding.RowFormsAdminBinding
import android.content.Context
import android.view.LayoutInflater
import android.widget.Filter
import android.widget.Filterable


class AdapterForms : RecyclerView.Adapter<AdapterForms.HolderForm>, Filterable {

    private lateinit var binding: RowFormsAdminBinding

    private  var context: Context

    var formArrayList: ArrayList<ModelForm>
    private var filterList: ArrayList<ModelForm>

    var filter: FilterFormsAdmin? = null



    constructor(context: Context, formArrayList: ArrayList<ModelForm>) : super() {
        this.context = context
        this.formArrayList = formArrayList
        this.filterList = formArrayList
    }



    inner class HolderForm(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTv: TextView = binding.titleTv
        val sportTv: TextView = binding.sportId
        val phoneTv: TextView = binding.phoneTv
        val descriptionTv: TextView = binding.descriptionTv
        val timestampTv: TextView = binding.dateTv
        val moreBtn = binding.moreBtn


    }

    override fun getFilter(): Filter {
        if (filter == null) {
            filter = FilterFormsAdmin(filterList, this)
        }
        return filter as FilterFormsAdmin
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderForm {
        binding = RowFormsAdminBinding.inflate(LayoutInflater.from(context), parent, false)
        return HolderForm(binding.root)
    }

    override fun getItemCount(): Int {
        return formArrayList.size
    }

    override fun onBindViewHolder(holder: HolderForm, position: Int) {
        val model = formArrayList[position]
        val formId = model.id
        val name = model.name
        val sport = model.sportId
        val phone = model.phone
        val description = model.description
        val timestamp = model.timestamp
        val formattedDate = MyAppliction.formatTimestamp(timestamp)

        holder.nameTv.text = name
        holder.sportTv.text = sport
        holder.phoneTv.text = phone
        holder.descriptionTv.text = description
        holder.timestampTv.text = formattedDate

        MyAppliction.loadForm(sportId = formId, holder.sportTv)




    }
}