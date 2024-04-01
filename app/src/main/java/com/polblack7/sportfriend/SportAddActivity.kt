package com.polblack7.sportfriend

import android.app.ProgressDialog
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.polblack7.sportfriend.databinding.ActivitySportAddBinding

class SportAddActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySportAddBinding

    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySportAddBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

        binding.submitBtn.setOnClickListener {
            validateData()
        }
    }

    private fun validateData() {
        val sport = binding.sportNameEt.text.toString().trim()
        if (sport.isEmpty()) {
            binding.sportNameEt.error = "Please enter sport"
        } else {
            addSport()
        }
    }

    private fun addSport() {
        val sport = binding.sportNameEt.text.toString().trim()
        progressDialog.setMessage("Adding new sport")
        progressDialog.show()
        val timestamp = System.currentTimeMillis()
        val hashMap = HashMap<String, Any>()
        hashMap["id"] = "$timestamp"
        hashMap["sport"] = sport
        hashMap["timestamp"] = timestamp
        hashMap["uid"] = "${firebaseAuth.uid!!}"
        val ref = FirebaseDatabase.getInstance().getReference("Sports")
        ref.child("$timestamp")
            .setValue(hashMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(this, "Sport added", Toast.LENGTH_SHORT).show()

            }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                Toast.makeText(this, "Failed to add sport: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}