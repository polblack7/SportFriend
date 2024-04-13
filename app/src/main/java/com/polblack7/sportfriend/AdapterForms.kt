package com.polblack7.sportfriend
import android.widget.TextView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView
import com.polblack7.sportfriend.databinding.RowFormsAdminBinding
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.widget.Filter
import android.widget.Filterable
import androidx.appcompat.app.AlertDialog


class AdapterForms : RecyclerView.Adapter<AdapterForms.HolderForm>, Filterable {

    private lateinit var binding: RowFormsAdminBinding

    private  var context: Context

    public var formArrayList: ArrayList<ModelForm>
    private var filterList: ArrayList<ModelForm>

    private var filter: FilterFormsAdmin? = null



    constructor(context: Context, formArrayList: ArrayList<ModelForm>) : super() {
        this.context = context
        this.formArrayList = formArrayList
        this.filterList = formArrayList
    }





    override fun getFilter(): Filter {
        if (filter == null) {
            filter = FilterFormsAdmin(filterList, this)
        }
        return filter as FilterFormsAdmin
    }

    inner class HolderForm(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTv: TextView = binding.titleTv
        val sportTv: TextView = binding.sportId
        val phoneTv: TextView = binding.phoneTv
        val descriptionTv: TextView = binding.descriptionTv
        val timestampTv: TextView = binding.dateTv
        val locationTV: TextView = binding.locationTv
        val moreBtn = binding.moreBtn


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
        val location = model.location
        val timestamp = model.timestamp
        val formattedDate = MyAppliction.formatTimestamp(timestamp)

        holder.nameTv.text = name
        holder.sportTv.text = sport
        holder.phoneTv.text = phone
        holder.locationTV.text = location
        holder.descriptionTv.text = description
        holder.timestampTv.text = formattedDate

        MyAppliction.loadForm(sportId = sport, holder.sportTv)

        holder.moreBtn.setOnClickListener {
            moreOptionsDialog(model, holder)
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(context, FormDetailActivity::class.java)
            intent.putExtra("formId", formId)
            context.startActivity(intent)
        }




    }

    private fun moreOptionsDialog(model: ModelForm, holder: AdapterForms.HolderForm) {
        val formId = model.id
        val name = model.name

        val options = arrayOf("Edit", "Delete")

        val builder = AlertDialog.Builder(context)
        builder.setTitle("Choose Option")
            .setItems(options) { dialog, which ->
                if (which == 0) {
                    // Edit
                    val intent = Intent(context, FormEditActivity::class.java)
                    intent.putExtra("formId", formId)
                    context.startActivity(intent)
                } else {
                    // Delete
                    MyAppliction.deleteForm(context, formId, name)

                }
            }
            .show()


    }
}