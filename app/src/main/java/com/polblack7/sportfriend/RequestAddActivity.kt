package com.polblack7.sportfriend

import android.app.AlertDialog
import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.polblack7.sportfriend.databinding.ActivityRequestAddBinding


class RequestAddActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRequestAddBinding

    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var progressDialog: ProgressDialog

    private lateinit var sportArrayList: ArrayList<ModelSport>

    private var TAG = "REQUEST_ADD_TAG"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRequestAddBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        loadSports()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.sportType.setOnClickListener {
            sportPickDialog()
        }

        binding.submitBtn.setOnClickListener {
            validateData()
        }

        binding.backBtn.setOnClickListener {
            onBackPressed()
        }
    }

    private var name = ""
    private var phone = ""

    private var location = ""
    private var description = ""
    private var sport = ""


    private fun validateData() {
        Log.d(TAG, "validateData: Validating data")
        name = binding.sportNameEt.text.toString().trim()
        phone = binding.sportContactEt.text.toString().trim()
        location = binding.sportLocationEt.text.toString().trim()
        description = binding.playerDescriptionEt.text.toString().trim()
        sport = binding.sportType.text.toString().trim()

        if (name.isEmpty()) {
            Toast.makeText(this, "Please enter name", Toast.LENGTH_SHORT).show()
        } else if (phone.isEmpty()) {
            Toast.makeText(this, "Please enter phone", Toast.LENGTH_SHORT).show()
        } else if (location.isEmpty()) {
            Toast.makeText(this, "Please enter location", Toast.LENGTH_SHORT).show()
        } else if (description.isEmpty()) {
            Toast.makeText(this, "Please enter description", Toast.LENGTH_SHORT).show()
        } else if (sport.isEmpty()) {
            Toast.makeText(this, "Please pick sport", Toast.LENGTH_SHORT).show()
        } else {
            addRequest()
        }
    }

    private fun addRequest() {
        Log.d(TAG, "addRequest: Adding Form")
        progressDialog.setMessage("Adding new form")

        val uid = firebaseAuth.uid
        val timestamp = System.currentTimeMillis()

        val hashMap = HashMap<String, Any>()
        hashMap["uid"] = "$uid"
        hashMap["id"] = "$timestamp"
        hashMap["name"] = "$name"
        hashMap["phone"] = "$phone"
        hashMap["location"] = "$location"
        hashMap["description"] = "$description"
        hashMap["sportId"] = "$selectedSportId"
        hashMap["timestamp"] = timestamp
        hashMap["viewsCount"] = 0

        val ref = FirebaseDatabase.getInstance().getReference("Forms")
        ref.child("$timestamp")
            .setValue(hashMap)
            .addOnSuccessListener {
                Log.d(TAG, "addRequest: ")
                progressDialog.dismiss()
                Toast.makeText(this, "Form added", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Log.d(TAG, "addRequest: Failed to add form: ${e.message}")
                progressDialog.dismiss()
                Toast.makeText(this, "Failed to add form: ${e.message}", Toast.LENGTH_SHORT).show()
            }


    }

    private fun loadSports() {
        Log.d(TAG, "loadSports: Loading sports")
        sportArrayList = ArrayList()
        val ref = FirebaseDatabase.getInstance().getReference("Sports")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                sportArrayList.clear()
                for (ds in snapshot.children) {
                    val model = ds.getValue(ModelSport::class.java)
                    sportArrayList.add(model!!)
                    Log.d(TAG, "onDataChange: ${model.sport}")
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private var selectedSportId = ""
    private var selectedSport = ""

    private fun sportPickDialog() {
        Log.d(TAG, "sportPickDialog: Showing sport pick dialog")

        val sportsArray = arrayOfNulls<String>(sportArrayList.size)
        for (i in sportArrayList.indices) {
            sportsArray[i] = sportArrayList[i].sport
        }

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Pick Sport")
            .setItems(sportsArray) { dialog, which ->
                selectedSportId = sportArrayList[which].id!!
                selectedSport = sportArrayList[which].sport!!
                binding.sportType.text = selectedSport

                Log.d(TAG, "sportPickDialog: Selected sport ID: $selectedSportId")
                Log.d(TAG, "sportPickDialog: Selected sport: $selectedSport")
            }
            .show()
    }


}