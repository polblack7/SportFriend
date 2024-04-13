package com.polblack7.sportfriend

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.polblack7.sportfriend.databinding.RowFormUserBinding

class AdapterFormUser : RecyclerView.Adapter<AdapterFormUser.HolderFormUser>, Filterable {

    private var context: Context

    public var formArrayList: ArrayList<ModelForm>

    var filterList: ArrayList<ModelForm>

    private lateinit var binding: RowFormUserBinding

    private var filter: FilterFormUser? = null

    constructor(context: Context, formArrayList: ArrayList<ModelForm>) : super() {
        this.context = context
        this.formArrayList = formArrayList
        this.filterList = formArrayList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderFormUser {
        binding = RowFormUserBinding.inflate(LayoutInflater.from(context), parent, false)

        return HolderFormUser(binding.root)
    }

    override fun getItemCount(): Int {
        return formArrayList.size
    }

    override fun getFilter(): Filter {
        if (filter == null) {
            filter = FilterFormUser(filterList, this)
        }
        return filter as FilterFormUser
    }

    override fun onBindViewHolder(holder: HolderFormUser, position: Int) {
        val model = formArrayList[position]
        val formId = model.id
        val name = model.name
        val sport = model.sportId
        val phone = model.phone
        val description = model.description
        val location = model.location
        val timestamp = model.timestamp
        val formattedDate = MyAppliction.formatTimestamp(timestamp)

        holder.titleTv.text = name

        holder.phoneTv.text = phone
        holder.locationTv.text = location
        holder.descriptionTv.text = description
        holder.dateTv.text = formattedDate

        MyAppliction.loadForm(sportId = sport, holder.sportId)

        holder.itemView.setOnClickListener {
            val intent = Intent(context, FormDetailActivity::class.java)
            intent.putExtra("formId", formId)
            context.startActivity(intent)
        }

    }

    inner class HolderFormUser(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var formIconIv = binding.formIconIv
        var titleTv = binding.titleTv
        var descriptionTv = binding.descriptionTv
        var moreBtn = binding.moreBtn
        var sportId = binding.sportId
        var dateTv = binding.dateTv
        var phoneTv = binding.phoneTv
        var locationTv = binding.locationTv
    }




}