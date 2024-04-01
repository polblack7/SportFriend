package com.polblack7.sportfriend

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.polblack7.sportfriend.databinding.ActivityAdminDashboardBinding

class AdminDashboardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAdminDashboardBinding

    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var sportArrayList: ArrayList<ModelSport>

    private lateinit var adapterSport: AdapterSport
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAdminDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()
        checkUser()

        binding.searchEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                try {
                    adapterSport.filter.filter(s)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        loadSports()

        binding.logOut.setOnClickListener {
            firebaseAuth.signOut()
            checkUser()
        }

        binding.createCategory.setOnClickListener {
            startActivity(Intent(this, SportAddActivity::class.java))
        }

        binding.addSport.setOnClickListener {
            startActivity(Intent(this, RequestAddActivity::class.java))
        }
    }

    private fun loadSports() {
        sportArrayList = ArrayList()
        val ref = FirebaseDatabase.getInstance().getReference("Sports")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                sportArrayList.clear()
                for (ds in snapshot.children) {
                    val modelSport = ds.getValue(ModelSport::class.java)
                    sportArrayList.add(modelSport!!)
                }
                adapterSport = AdapterSport(this@AdminDashboardActivity, sportArrayList)
                binding.sportList.adapter = adapterSport
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@AdminDashboardActivity, "" + error.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun checkUser() {
        val firebaseUser =firebaseAuth.currentUser
        if (firebaseUser == null) {
            startActivity(Intent(this@AdminDashboardActivity, MainActivity::class.java))
            finish()
        } else {
            val email = firebaseUser.email
            binding.userName.text = email
        }
    }
}