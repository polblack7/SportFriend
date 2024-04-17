package com.polblack7.sportfriend.activities

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.polblack7.sportfriend.databinding.ActivityFormEditBinding

class FormEditActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFormEditBinding

    private companion object{
        private const val TAG = "FormEditActivity"
    }

    private var sportId = ""

    private lateinit var progressDialog: ProgressDialog

    private lateinit var sportArrayList: ArrayList<String>

    private lateinit var sportIdArrayList: ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFormEditBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        sportId = intent.getStringExtra("formId")!!

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setCanceledOnTouchOutside(false)

        loadSports()
        loadFormInfo()

        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

        binding.sportTv.setOnClickListener {
            sportDialog()
        }

        binding.submitBtn.setOnClickListener {
            validateData()
        }

    }

    private fun loadFormInfo() {
        Log.d(TAG, "loadFormInfo: ")

        val ref = FirebaseDatabase.getInstance().getReference("Forms")
        ref.child(sportId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val title = "${snapshot.child("name").value}"
                    val description = "${snapshot.child("description").value}"
                    val selectedsportId = "${snapshot.child("sportId").value}"

                    binding.titleEt.setText(title)
                    binding.descriptionEt.setText(description)

                    Log.d(TAG, "onDataChange: $title")
                    Log.d(TAG, "onDataChange: $description")
                    Log.d(TAG, "onDataChange: $selectedsportId")

                    val refSport = FirebaseDatabase.getInstance().getReference("Sports")
                    refSport.child(selectedsportId)
                        .addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                val sport = "${snapshot.child("sport").value}"
                                binding.sportTv.text = sport
                            }

                            override fun onCancelled(error: DatabaseError) {
                            }
                        })
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

    private var title = ""
    private var description = ""
    private fun validateData() {
        title = binding.titleEt.text.toString().trim()
        description = binding.descriptionEt.text.toString().trim()

        if (title.isEmpty()) {
            Toast.makeText(this, "Enter Title", Toast.LENGTH_SHORT).show()
        } else if (description.isEmpty()) {
            Toast.makeText(this, "Enter Description", Toast.LENGTH_SHORT).show()
        } else if (selectedSport.isEmpty()) {
            Toast.makeText(this, "Select Sport", Toast.LENGTH_SHORT).show()
        } else {
            updateData()
        }
    }

    private fun updateData() {
        Log.d(TAG, "updateData: ")

        progressDialog.setMessage("Updating...")
        progressDialog.show()

        val hashMap = HashMap<String, Any>()
        hashMap["name"] = "$title"
        hashMap["description"] = "$description"
        hashMap["sportId"] = "$selectedSportId"

        val ref = FirebaseDatabase.getInstance().getReference("Forms")
        ref.child(sportId)
            .updateChildren(hashMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Log.d(TAG, "updateData: success")
                Toast.makeText(this, "Updated", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Log.d(TAG, "updateData: failed")
                progressDialog.dismiss()
                Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
            }
    }

    private var selectedSportId = ""
    private var selectedSport = ""

    private fun sportDialog() {
        val sportsArray = arrayOfNulls<String>(sportArrayList.size)
        for (i in sportArrayList.indices) {
            sportsArray[i] = sportArrayList[i]
        }

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Select Sport")
            .setItems(sportsArray) { dialog, which ->
                selectedSport = sportsArray[which]!!
                selectedSportId = sportIdArrayList[which]
                binding.sportTv.text = selectedSport
            }
            .show()
    }

    private fun loadSports() {
        Log.d(TAG, "loadSports: ")

        sportArrayList = ArrayList()
        sportIdArrayList = ArrayList()

        val ref = FirebaseDatabase.getInstance().getReference("Sports")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                sportArrayList!!.clear()
                sportIdArrayList!!.clear()
                for (ds in snapshot.children) {
                    val sport = "${ds.child("sport").value}"
                    val sportId = "${ds.child("id").value}"

                    sportArrayList!!.add(sport)
                    sportIdArrayList!!.add(sportId)

                    Log.d(TAG, "onDataChange: $sportId")
                    Log.d(TAG, "onDataChange: $sport")
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
}