package com.polblack7.sportfriend.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.polblack7.sportfriend.R
import com.polblack7.sportfriend.databinding.ActivityAdminDashboardBinding

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminDashboardBinding

    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)
        firebaseAuth = FirebaseAuth.getInstance()
        Handler().postDelayed(Runnable {
            checkUser()
        }, 2000)
    }

    private fun checkUser() {
        var firebaseUser = firebaseAuth.currentUser
        if (firebaseUser == null) {
            startActivity(Intent(this, MainActivity::class.java))
        } else {
            val firebaseUser = firebaseAuth.currentUser!!

            val ref = FirebaseDatabase.getInstance().getReference("Users")
            ref.child(firebaseUser.uid)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {


                        val userType = snapshot.child("userType").value

                        if (userType == "user") {
                            startActivity(Intent(this@SplashActivity, UserDashboardActivity::class.java))
                            finish()
                        } else if (userType == "admin") {
                            startActivity(Intent(this@SplashActivity, AdminDashboardActivity::class.java))
                            finish()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        //
                    }

                })
        }
    }
}