package com.polblack7.sportfriend

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
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
import com.polblack7.sportfriend.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    //firebase auth
    private lateinit var firebaseAuth: FirebaseAuth

    //progress dialog
    private lateinit var progressDialog: ProgressDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.backbtn.setOnClickListener {
            onBackPressed()
        }

        binding.signUpRedirect.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        binding.buttonLogin.setOnClickListener {
            validateData()

        }
    }

    private var email = ""
    private var password = ""
    private fun validateData() {
        email = binding.emailField2.text.toString().trim()
        password = binding.passwordField2.text.toString().trim()

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Email doesn't exist", Toast.LENGTH_LONG).show()
        } else if (password.isEmpty()) {
            Toast.makeText(this, "Incorrect password", Toast.LENGTH_LONG).show()
        } else {
            loginUser()
        }
    }

    private fun loginUser() {
        progressDialog.setMessage("Logging in")
        progressDialog.show()

        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                //TODO: Check if user is admin or user.
                checkUser()
            }
            .addOnFailureListener { e->
                progressDialog.dismiss()
                Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
            }
    }

    private fun checkUser() {
        progressDialog.setMessage("Checking user")

        val firebaseUser = firebaseAuth.currentUser!!

        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(firebaseUser.uid)
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    progressDialog.dismiss()

                    val userType = snapshot.child("userType").value

                    if (userType == "user") {
                        startActivity(Intent(this@LoginActivity, UserDashboardActivity::class.java))
                        finish()
                    } else if (userType == "admin") {
                        startActivity(Intent(this@LoginActivity, AdminDashboardActivity::class.java))
                        finish()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    //
                }

            })
    }
}