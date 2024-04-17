package com.polblack7.sportfriend.activities

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.polblack7.sportfriend.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {
    //view binding
    private lateinit var binding:ActivityRegisterBinding
    //firebase auth
    private lateinit var firebaseAuth: FirebaseAuth
    //progress dialog
    private lateinit var progressDialog: ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setCanceledOnTouchOutside(false)

        //previous screen
        binding.backbtn.setOnClickListener {
            onBackPressed()
        }

        binding.buttonRegister.setOnClickListener {
            validateData()
        }
    }
    private var login = ""
    private var email = ""
    private var password = ""
    private var phone = ""

    private fun validateData() {
        login = binding.loginFieldReg2.text.toString().trim()
        email = binding.emailFieldReg2.text.toString().trim()
        password = binding.passwordFieldReg2.text.toString().trim()
        phone = binding.phoneFieldReg2.text.toString().trim()
        val cPassword = binding.cPasswordFieldReg2.text.toString().trim()

        if (login.isEmpty()) {
            Toast.makeText(this, "Enter your name", Toast.LENGTH_LONG).show()
        } else if (email.isEmpty()) {
            Toast.makeText(this, "Enter your email", Toast.LENGTH_LONG).show()
        } else if (password.isEmpty()) {
            Toast.makeText(this, "Enter your password", Toast.LENGTH_LONG).show()
        } else if (cPassword.isEmpty()) {
            Toast.makeText(this, "Confirm your password", Toast.LENGTH_LONG).show()
        } else if (cPassword != password) {
            Toast.makeText(this, "Password doesn't match", Toast.LENGTH_LONG).show()
        } else if (phone.isEmpty()) {
            Toast.makeText(this, "Enter your phone number", Toast.LENGTH_LONG).show()
        } else {
            createUser()
        }
    }

    private fun createUser() {
        progressDialog.setMessage("Creating account")
        progressDialog.show()

        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                updateUserInfo()
            }
            .addOnFailureListener{e->
                progressDialog.dismiss()
                Toast.makeText(this, "Failed creating account. Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateUserInfo(){
        progressDialog.setMessage("Saving info")

        val timeStamp = System.currentTimeMillis()

        val uid = firebaseAuth.uid

        val hashMap: HashMap<String, Any?> = HashMap()
        hashMap["uid"] = uid
        hashMap["email"] = email
        hashMap["login"] = login
        hashMap["profileImage"] = ""
        hashMap["userType"] = "user"
        hashMap["status"] = ""
        hashMap["timeStamp"] = timeStamp
        hashMap["phone"] = phone

        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(uid!!)
            .setValue(hashMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                progressDialog.dismiss()
                Toast.makeText(this, "Account created", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@RegisterActivity, UserDashboardActivity::class.java))
                finish()
            }
            .addOnFailureListener {e->
                progressDialog.dismiss()
                Toast.makeText(this, "Failed saving info. Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}