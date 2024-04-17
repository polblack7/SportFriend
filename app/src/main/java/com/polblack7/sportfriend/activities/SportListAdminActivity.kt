package com.polblack7.sportfriend.activities

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.polblack7.sportfriend.adapters.AdapterForms
import com.polblack7.sportfriend.databinding.ActivitySportListAdminBinding
import com.polblack7.sportfriend.models.ModelForm

class SportListAdminActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySportListAdminBinding

    private lateinit var adapterForms: AdapterForms

    private lateinit var formsArrayList: ArrayList<ModelForm>

    private companion object{
        private const val TAG = "SportListAdminActivity"
    }

    private var sportId = ""
    private var sport = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySportListAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = intent

            sportId = intent.getStringExtra("SportId")!!
            sport = intent.getStringExtra("Sport")!!



        binding.subTitleTv.text = sport

        loadFormsList()



        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

        binding.searchEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                try {
                    adapterForms.filter!!.filter(s)
                } catch (e: Exception) {
                    Log.d(TAG, "afterTextChanged: ${e.message}")
                }

            }
        })



    }

    private fun loadFormsList() {
        formsArrayList = ArrayList()

        val ref = FirebaseDatabase.getInstance().getReference("Forms")
        ref.orderByChild("sportId").equalTo(sportId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    formsArrayList.clear()
                    for (ds in snapshot.children) {
                        val modelForm = ds.getValue(ModelForm::class.java)
                        if (modelForm != null) {
                            formsArrayList.add(modelForm)
                            Log.d(TAG, "onDataChange: ")
                        }

                    }

                    adapterForms = AdapterForms(this@SportListAdminActivity, formsArrayList)
                    var check = formsArrayList
                    binding.formsRv.adapter = adapterForms
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }
}