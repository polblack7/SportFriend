package com.polblack7.sportfriend

import android.content.Intent
import android.os.Bundle
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import androidx.core.view.MarginLayoutParamsCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.polblack7.sportfriend.activities.RequestAddActivity
import com.polblack7.sportfriend.adapters.AdapterFormUser
import com.polblack7.sportfriend.databinding.FragmentFormsUserBinding
import com.polblack7.sportfriend.models.ModelForm


class FormsUserFragment : Fragment {

    private lateinit var binding: FragmentFormsUserBinding

    companion object {
        private const val TAG = "FORMS_USER_TAG"

        public fun newInstance(sportId: String, sport: String, uid: String) : FormsUserFragment{
            val fragment = FormsUserFragment()
            val args = Bundle()
            args.putString("sportId", sportId);
            args.putString("sport", sport);
            args.putString("uid", uid);
            fragment.arguments = args
            return fragment
        }
    }

    private var sportId = ""
    private var sport = ""
    private var uid = ""

    private lateinit var formArrayList: ArrayList<ModelForm>
    private lateinit var adapterFormUser: AdapterFormUser

    constructor()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val args = arguments
        if (args != null) {
            sportId = args.getString("sportId")!!
            sport = args.getString("sport")!!
            uid = args.getString("uid")!!
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentFormsUserBinding.inflate(LayoutInflater.from(context), container, false)

        Log.d(TAG, "onCreateView: sport: $sport")

        ViewCompat
            .setOnApplyWindowInsetsListener(binding.addFab) { view, insets ->
                view.updateLayoutParams<MarginLayoutParams> {
                    bottomMargin = insets.systemWindowInsetBottom
                }
                insets
            }

        if (sport == "All") {
            loadAllForms()
        } else if (sport == "Most Viewed") {
            loadMostViewedForms("viewsCount")
        } else if (sport == "My Forms") {
            loadUserForms()
        } else {
            loadFormsBySport()
        }

        binding.searchEt.addTextChangedListener ( object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                try {
                    adapterFormUser.filter.filter(s)
                } catch (e: Exception) {
                    Log.d(TAG, "onTextChanged: ${e.message}")
                }
            }

            override fun afterTextChanged(s: android.text.Editable?) {

            }
        })

        binding.addFab.setOnClickListener {
            val intent = Intent(context, RequestAddActivity::class.java)
            startActivity(intent)
        }


        // Inflate the layout for this fragment
        return binding.root
    }

    private fun loadAllForms() {
        formArrayList = ArrayList()
        val ref = FirebaseDatabase.getInstance().getReference("Forms")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                formArrayList.clear()
                for (ds in snapshot.children) {
                    val modelForm = ds.getValue(ModelForm::class.java)
                    formArrayList.add(modelForm!!)
                }
                adapterFormUser = AdapterFormUser(context!!, formArrayList)
                binding.formsRv.adapter = adapterFormUser
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d(TAG, "onCancelled: ${error.message}")
            }
        })
    }

    private fun loadUserForms() {
        formArrayList = ArrayList()
        val ref = FirebaseDatabase.getInstance().getReference("Forms")
        val CurrentUid = FirebaseAuth.getInstance().getCurrentUser()!!.getUid();
        ref.orderByChild("uid").equalTo(CurrentUid)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    formArrayList.clear()
                    for (ds in snapshot.children) {
                        val modelForm = ds.getValue(ModelForm::class.java)
                        formArrayList.add(modelForm!!)
                    }
                    adapterFormUser = AdapterFormUser(context!!, formArrayList)
                    binding.formsRv.adapter = adapterFormUser
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d(TAG, "onCancelled: ${error.message}")
                }
            })
    }

    private fun loadMostViewedForms(views: String) {
        formArrayList = ArrayList()
        val ref = FirebaseDatabase.getInstance().getReference("Forms")
        ref.orderByChild(views).limitToLast(10)
            .addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                formArrayList.clear()
                for (ds in snapshot.children) {
                    val modelForm = ds.getValue(ModelForm::class.java)
                    formArrayList.add(modelForm!!)
                }
                formArrayList.reverse()
                adapterFormUser = AdapterFormUser(context!!, formArrayList)
                binding.formsRv.adapter = adapterFormUser
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d(TAG, "onCancelled: ${error.message}")
            }
        })
    }


    private fun loadFormsBySport() {
        formArrayList = ArrayList()
        val ref = FirebaseDatabase.getInstance().getReference("Forms")
        ref.orderByChild("sportId").equalTo(sportId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    formArrayList.clear()
                    for (ds in snapshot.children) {
                        val modelForm = ds.getValue(ModelForm::class.java)
                        formArrayList.add(modelForm!!)
                    }
                    adapterFormUser = AdapterFormUser(context!!, formArrayList)
                    binding.formsRv.adapter = adapterFormUser
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d(TAG, "onCancelled: ${error.message}")
                }
            })
    }


}