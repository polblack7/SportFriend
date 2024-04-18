package com.polblack7.sportfriend.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.polblack7.sportfriend.filters.FilterFormUser
import com.polblack7.sportfriend.activities.FormDetailActivity
import com.polblack7.sportfriend.models.ModelForm
import com.polblack7.sportfriend.MyAppliction
import com.polblack7.sportfriend.R
import com.polblack7.sportfriend.activities.FormEditActivity
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
        val uid = model.uid
        var image = ""


        holder.titleTv.text = name

        holder.phoneTv.text = phone
        holder.locationTv.text = location
        holder.descriptionTv.text = description
        holder.dateTv.text = formattedDate
        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(uid!!)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    image = snapshot.child("profileImage").value.toString()
                    try {
                        Glide.with(context)
                            .load(image)
                            .placeholder(R.drawable.shape_default_account)
                            .into(holder.imageIv)
                    } catch (e: Exception) {

                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })



        MyAppliction.loadForm(sportId = sport, holder.sportId)

        holder.itemView.setOnClickListener {
            val intent = Intent(context, FormDetailActivity::class.java)
            intent.putExtra("formId", formId)
            context.startActivity(intent)
        }

        val CurrentUid = FirebaseAuth.getInstance().getCurrentUser()!!.getUid();
        if (uid == CurrentUid) {
            holder.moreBtn.visibility = View.VISIBLE
            holder.moreBtn.setOnClickListener {
                moreOptionsDialog(model, holder)
            }
        } else {
            holder.moreBtn.visibility = View.GONE
        }




    }

    private fun moreOptionsDialog(model: ModelForm, holder: HolderFormUser) {
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

    inner class HolderFormUser(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var formIconIv = binding.formIconIv
        var titleTv = binding.titleTv
        var descriptionTv = binding.descriptionTv
        var moreBtn = binding.moreBtn
        var sportId = binding.sportId
        var dateTv = binding.dateTv
        var phoneTv = binding.phoneTv
        var locationTv = binding.locationTv
        var imageIv = binding.formIconIv
    }




}