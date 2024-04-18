package com.polblack7.sportfriend.activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.polblack7.sportfriend.MyAppliction
import com.polblack7.sportfriend.R
import com.polblack7.sportfriend.databinding.ActivityFormDetailBinding

class FormDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFormDetailBinding

    private var formId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFormDetailBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        formId = intent.getStringExtra("formId")!!

        MyAppliction.incrementViewsCount(formId)

        loadFormDetails()

        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

        binding.submitBtn.setOnClickListener {

        }
    }

    private fun loadFormDetails() {
        val ref = FirebaseDatabase.getInstance().getReference("Forms")
        ref.child(formId)
            .addListenerForSingleValueEvent(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val sportId = snapshot.child("sportId").value.toString()
                    val description = snapshot.child("description").value.toString()
                    val viewsCount = snapshot.child("viewsCount").value.toString()
                    val phone = snapshot.child("phone").value.toString()
                    val location = snapshot.child("location").value.toString()
                    val timestamp = snapshot.child("timestamp").value.toString()
                    val title = snapshot.child("name").value.toString()
                    val uid = snapshot.child("uid").value.toString()
                    val ref = FirebaseDatabase.getInstance().getReference("Users")
                    ref.child(uid!!)
                        .addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                val image = snapshot.child("profileImage").value.toString()
                                try {
                                    Glide.with(this@FormDetailActivity)
                                        .load(image)
                                        .placeholder(R.drawable.shape_default_account)
                                        .into(binding.formIconIv)
                                } catch (e: Exception) {

                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                            }
                        })


                    val date = MyAppliction.formatTimestamp(timestamp.toLong())
                    MyAppliction.loadName(uid, binding.accountNameTv)

                    MyAppliction.loadForm(sportId, binding.sportTv)


                    binding.titleTv.text = title
                    binding.descriptionTv.text = description
                    binding.viewsTv.text = viewsCount
                    binding.phoneTv.text = phone
                    binding.locationTv.text = location
                    binding.dateTv.text = date


                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }
}