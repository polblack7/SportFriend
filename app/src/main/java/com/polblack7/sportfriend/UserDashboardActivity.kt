package com.polblack7.sportfriend

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.polblack7.sportfriend.databinding.ActivityUserDashboardBinding

class UserDashboardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUserDashboardBinding

    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var sportArrayList: ArrayList<ModelSport>
    private lateinit var viewPagerAdapter: ViewPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserDashboardBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        checkUser()

        setupWithViewPagerAdapter(binding.viewPager)
        binding.tabLayout.setupWithViewPager(binding.viewPager)

        binding.logOut.setOnClickListener {
            firebaseAuth.signOut()
            checkUser()
        }




    }

    private fun setupWithViewPagerAdapter(viewPager: ViewPager) {
        viewPagerAdapter = ViewPagerAdapter(supportFragmentManager, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, this)

        sportArrayList = ArrayList()

        val ref = FirebaseDatabase.getInstance().getReference("Sports")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                sportArrayList.clear()
                val modelAll = ModelSport("01", "All", 1, "")
                val modelMostViewed = ModelSport("01", "Most Viewed", 1, "")
                val modelUser = ModelSport("01", "My Forms", 1, "")
                sportArrayList.add(modelAll)
                sportArrayList.add(modelMostViewed)
                sportArrayList.add(modelUser)
                viewPagerAdapter.addFragment(
                    FormsUserFragment.newInstance(
                        "${modelAll.id}",
                        "${modelAll.sport}",
                        "${modelAll.uid}"

                ), modelAll.sport
                )
                viewPagerAdapter.addFragment(
                    FormsUserFragment.newInstance(
                        "${modelMostViewed.id}",
                        "${modelMostViewed.sport}",
                        "${modelMostViewed.uid}"
                ), modelMostViewed.sport
                )
                viewPagerAdapter.addFragment(
                    FormsUserFragment.newInstance(
                        "${modelUser.id}",
                        "${modelUser.sport}",
                        "${modelUser.uid}"
                ), modelUser.sport
                )

                viewPagerAdapter.notifyDataSetChanged()

                for (ds in snapshot.children) {
                    val model = ds.getValue(ModelSport::class.java)
                    sportArrayList.add(model!!)

                    viewPagerAdapter.addFragment(
                        FormsUserFragment.newInstance(
                            "${model.id}",
                            "${model.sport}",
                            "${model.uid}"

                        ), model.sport
                    )
                    viewPagerAdapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })

        viewPager.adapter = viewPagerAdapter
    }



    class ViewPagerAdapter(fm: FragmentManager, behavior: Int, context: Context) : FragmentPagerAdapter(fm, behavior) {
        private val fragmentsList: ArrayList<FormsUserFragment> = ArrayList()

        private val fragmentTitleList: ArrayList<String> = ArrayList()

        private val context: Context

        init {
            this.context = context
        }
        override fun getCount(): Int {
            return fragmentsList.size
        }

        override fun getItem(position: Int): Fragment {
            return fragmentsList[position]
        }

        override fun getPageTitle(position: Int): CharSequence {
            return fragmentTitleList[position]
        }

        public fun addFragment(fragment: FormsUserFragment, title: String) {
            fragmentsList.add(fragment)
            fragmentTitleList.add(title)
        }

    }

    private fun checkUser() {
        val firebaseUser =firebaseAuth.currentUser
        if (firebaseUser == null) {
            startActivity(Intent(this@UserDashboardActivity, MainActivity::class.java))
            finish()
        } else {
            val email = firebaseUser.email
            binding.userName.text = email
        }
    }
}