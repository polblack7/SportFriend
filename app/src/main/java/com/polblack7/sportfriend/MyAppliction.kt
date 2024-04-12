package com.polblack7.sportfriend
import android.app.AlertDialog
import android.app.Application
import android.app.ProgressDialog
import android.content.Context
import android.util.Log
import android.widget.TextView
import android.widget.Toast

import com.google.firebase.FirebaseApp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import java.util.Locale
import java.util.Calendar
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MyAppliction: Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }

    companion object {
        fun formatTimestamp(timestamp: Long): String {
            val cal = Calendar.getInstance(Locale.ENGLISH)
            cal.timeInMillis = timestamp
            return android.text.format.DateFormat.format("dd/MM/yyyy", timestamp).toString()
        }

        fun deleteForm(context: Context, formId: String, formTitle: String) {
            val TAG = "deleteForm"

            Log.d(TAG, "deleteForm: deleting")

            val progressDialog = ProgressDialog(context)
            progressDialog.setTitle("Please wait")
            progressDialog.setMessage("Deleting form $formTitle")
            progressDialog.setCanceledOnTouchOutside(false)
            progressDialog.show()

            Log.d(TAG, "deleteForm: deleting from storage")
            val ref = FirebaseDatabase.getInstance().getReference("Forms")
            ref.child(formId)
                .removeValue()
                .addOnSuccessListener {
                    progressDialog.dismiss()
                    Log.d(TAG, "deleteForm: success")
                    Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    progressDialog.dismiss()
                    Log.d(TAG, "deleteForm: failed")
                    Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show()
                }

        }

        fun loadForm(sportId: String, sportTv: TextView) {
            val ref = FirebaseDatabase.getInstance().getReference("Sports")
            ref.child(sportId)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val sport = "${snapshot.child("sport").value}"

                        sportTv.text = sport
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }
                })

        }
    }



}
