package com.polblack7.sportfriend.activities

import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.polblack7.sportfriend.MyAppliction
import com.polblack7.sportfriend.R
import com.polblack7.sportfriend.databinding.ActivityProfileEditBinding

class ProfileEditActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileEditBinding

    private lateinit var firebaseAuth: FirebaseAuth

    private var imageUri: Uri? = null

    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityProfileEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setCanceledOnTouchOutside(false)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        firebaseAuth = FirebaseAuth.getInstance()
        loadUserInfo()

        binding.backBtn.setOnClickListener {
            onBackPressed()

        }

        binding.profileImage.setOnClickListener {
            imagePick()

        }

        binding.updateBtn.setOnClickListener {
            validateData()
        }

    }

    private var name = ""
    private var phone = ""
    private var status = ""
    private fun validateData() {
        name = binding.nameEt.text.toString().trim()
        phone = binding.phoneEt.text.toString().trim()
        status = binding.statusEt.text.toString().trim()

        if (name.isEmpty()) {
            Toast.makeText(this, "Please enter name", Toast.LENGTH_SHORT).show()
        } else if (phone.isEmpty()) {
            Toast.makeText(this, "Please enter phone", Toast.LENGTH_SHORT).show()
        } else {

            if (imageUri == null) {
                updateUserInfo("")
            } else {
                uploadImage()
            }

        }
    }

    private fun uploadImage() {
        progressDialog.setMessage("Uploading image")
        progressDialog.show()

        val filePathAndName = "ProfileImages/${firebaseAuth.uid}"

        val storageReference = FirebaseStorage.getInstance().getReference(filePathAndName)
        storageReference.putFile(imageUri!!)
            .addOnSuccessListener { taskSnapshot ->

                val uriTask: Task<Uri> = taskSnapshot.storage.downloadUrl
                while (!uriTask.isSuccessful);
                val uploadedImageUrl = "${uriTask.result}"

                updateUserInfo(uploadedImageUrl)

            }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                Toast.makeText(this, "Failed to upload image: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateUserInfo(s: String) {
        progressDialog.setMessage("Updating info")
        progressDialog.show()

        val hashMap: HashMap<String, Any> = HashMap()
        hashMap["login"] = name
        hashMap["phone"] = phone
        if (status.isNotEmpty()) {
            hashMap["status"] = status
        } else {
            hashMap["status"] = ""
        }
        if (s.isNotEmpty()) {
            hashMap["profileImage"] = s
        }

        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(firebaseAuth.uid!!)
            .updateChildren(hashMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(this, "Profile updated", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                Toast.makeText(this, "Failed to update profile: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadUserInfo() {
        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(firebaseAuth.uid!!)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val name = "${snapshot.child("login").value}"
                    val email = "${snapshot.child("email").value}"
                    val phone = "${snapshot.child("phone").value}"
                    val timestamp = "${snapshot.child("timeStamp").value}"
                    val image = "" + snapshot.child("profileImage").value
                    val uid = "${snapshot.child("uid").value}"
                    val userType = "${snapshot.child("userType").value}"
                    val status = "${snapshot.child("status").value}"

                    val date = MyAppliction.formatTimestamp(timestamp.toLong())

                    binding.nameEt.setText(name)
                    binding.phoneEt.setText(phone)
                    binding.statusEt.setText(status)

                    try {
                        Glide.with(this@ProfileEditActivity)
                            .load(image)
                            .placeholder(R.drawable.shape_default_account)
                            .into(binding.profileImage)
                    } catch (e: Exception) {

                    }


                }

                override fun onCancelled(error: DatabaseError) {
                    //
                }
            })
    }

    private fun imagePick() {

        val popupMenu = PopupMenu(this, binding.profileImage)
        popupMenu.menu.add(Menu.NONE, 0, 0, "Camera")
        popupMenu.menu.add(Menu.NONE, 1, 1, "Gallery")
        popupMenu.show()

        popupMenu.setOnMenuItemClickListener { item ->
            val id = item.itemId
            if (id == 0) {
                pickImageCamera()
            } else if (id == 1) {
                pickImageGallery()
            }

            true
        }
    }

    private fun pickImageCamera() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "Temp_Title")
        values.put(MediaStore.Images.Media.DESCRIPTION, "Temp_Description")

        imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        cameraActivityResultLauncher.launch(intent)
    }

    private fun pickImageGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        galleryActivityResultLauncher.launch(intent)
    }

    private val cameraActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data


                binding.profileImage.setImageURI(imageUri)
            } else {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show()
            }
        })

    private val galleryActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                imageUri = data!!.data

                binding.profileImage.setImageURI(imageUri)
            } else {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show()
            }
        })
}

