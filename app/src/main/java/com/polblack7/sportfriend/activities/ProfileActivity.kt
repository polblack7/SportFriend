package com.polblack7.sportfriend.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.polblack7.sportfriend.MyAppliction
import com.polblack7.sportfriend.R
import com.polblack7.sportfriend.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {


    private lateinit var binding: ActivityProfileBinding

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        firebaseAuth = FirebaseAuth.getInstance()
        loadUserInfo()

        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

        binding.editBtn.setOnClickListener {
            startActivity(Intent(this, ProfileEditActivity::class.java))
        }



    }

    private fun loadUserInfo() {
        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(firebaseAuth.uid!!)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val name = "${snapshot.child("login").value}"
                    val email = "${snapshot.child("email").value}"
                    val phone = "${snapshot.child("phone").value}"
                    val timestamp = "${snapshot.child("timeStamp").value}"
                    val image = "" + snapshot.child("profileImage").value
                    val uid = "${snapshot.child("uid").value}"
                    val userType = "${snapshot.child("userType").value}"
                    val status = "${snapshot.child("status").value}"

                    val date = MyAppliction.formatTimestamp(timestamp.toLong())
                    binding.nameTv.text = name
                    binding.emailTv.text = email
                    binding.phoneTv.text = phone
                    binding.AccountTypeTv.text = userType
                    binding.MembershipTv.text = date
                    binding.statusTv.text = status

                    try {
                        Glide.with(this@ProfileActivity)
                            .load(image)
                            .placeholder(R.drawable.shape_default_account)
                            .into(binding.profileImage)
                    } catch (e: Exception) {

                    }


                }

                override fun onCancelled(error: DatabaseError) {
                    //
                }
            })
    }
}