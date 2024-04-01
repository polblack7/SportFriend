package com.polblack7.sportfriend
import android.app.Application
import android.widget.TextView
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import java.util.Locale
import java.util.Calendar
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MyAppliction: Application()   {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }

    companion object{
        fun formatTimestamp(timestamp: Long): String {
            val cal = Calendar.getInstance(Locale.ENGLISH)
            cal.timeInMillis = timestamp
            return android.text.format.DateFormat.format("dd/MM/yyyy", timestamp).toString()
        }

        fun loadForm(sportId: String, sportTv: TextView ) {
            val ref = FirebaseDatabase.getInstance().getReference("Forms")
            ref.child(sportId)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val sport = "${snapshot.child(" sport ").value}"

                        sportTv.text = sport
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }
                })

            }
        }
    }
