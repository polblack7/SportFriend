package com.polblack7.sportfriend

import android.widget.TextView
import android.view.View
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView
import com.polblack7.sportfriend.databinding.RowSportsBinding
import android.content.Context
import android.view.ViewGroup
import android.view.LayoutInflater
import android.app.AlertDialog
import android.content.Intent
import android.widget.Filter
import android.widget.Toast
import com.google.firebase.database.FirebaseDatabase
import android.widget.Filterable

class AdapterSport : RecyclerView.Adapter<AdapterSport.HolderSport>, Filterable {
    private  var context: Context

    public var sportArrayList: ArrayList<ModelSport>

    private var filterList: ArrayList<ModelSport>

    private var filterSport: FilterSport? = null

    private lateinit var binding: RowSportsBinding

    constructor(context: Context, sportArrayList: ArrayList<ModelSport>) {
        this.context = context
        this.sportArrayList = sportArrayList
        this.filterList = sportArrayList
    }



    inner class HolderSport(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val sportTv: TextView = binding.sportName
        val deleteBtn: ImageButton = binding.deleteBtn
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderSport {
        binding = RowSportsBinding.inflate(LayoutInflater.from(context), parent, false)
        return HolderSport(binding.root)
    }

    override fun getItemCount(): Int {
        return sportArrayList.size
    }

    override fun onBindViewHolder(holder: HolderSport, position: Int) {
        val model = sportArrayList[position]
        val id = model.id
        val sport = model.sport
        val timestamp = model.timestamp
        val uid = model.uid

        holder.sportTv.text = sport

        holder.deleteBtn.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Delete")
                .setMessage("Are you sure you want to delete $sport?")
                .setPositiveButton("DELETE") { dialog, which ->
                    Toast.makeText(context, "Deleting", Toast.LENGTH_SHORT).show()
                    deleteSport(model, holder)
                }
                .setNegativeButton("CANCEL") { dialog, which ->
                    dialog.dismiss()
                }
                .show()
        }

        holder.itemView.setOnClickListener{
            val intent = Intent(context, SportListAdminActivity::class.java)
            intent.putExtra("SportId", id)
            intent.putExtra("Sport", sport)
            context.startActivity(intent)
        }

    }

    private fun deleteSport(model: ModelSport, holder: HolderSport) {
        val ref = FirebaseDatabase.getInstance().getReference("Sports")
        ref.child(model.id!!)
            .removeValue()
            .addOnSuccessListener {
                Toast.makeText(context, "Sport deleted", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to delete", Toast.LENGTH_SHORT).show()
            }
    }

    override fun getFilter(): Filter {
        if (filterSport == null) {
            filterSport = FilterSport(filterList, this)
        }
        return filterSport as FilterSport
    }
}
