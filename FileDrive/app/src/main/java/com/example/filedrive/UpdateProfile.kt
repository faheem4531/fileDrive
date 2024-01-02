package com.example.filedrive

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.example.filedrive.ui.UrlDataClass
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
class UpdateProfile : AppCompatActivity() {

    private var profileUri : Uri? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_profile)


        supportActionBar?.hide()

//        Starting code
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val dbRef = FirebaseDatabase.getInstance().getReference("Users").child(userId.toString())


        var updatedImage = findViewById<ImageView>(R.id.updateProfileImage)
        var updateName = findViewById<EditText>(R.id.updateName)
        var updateEmail = findViewById<EditText>(R.id.updateEmail)
        var saveBtn = findViewById<Button>(R.id.updateProfileBtn)

        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userName = snapshot.child("name").getValue(String::class.java)
                val userEmail = snapshot.child("email").getValue(String::class.java)
                val pofileImage = snapshot.child("imageUrl").getValue(String::class.java)

                profileUri = Uri.parse(pofileImage)
                updateName.setText(userName)
                updateEmail.setText(userEmail)

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@UpdateProfile, "Error: "+error.message, Toast.LENGTH_SHORT).show()
            }

        })

        updateEmail.setOnClickListener{
            updateEmail.error = "error"

            val location = IntArray(2)
            updateEmail.getLocationOnScreen(location)

            val toast = Toast.makeText(this, "Email Account can't be changed", Toast.LENGTH_SHORT)
            toast.setGravity(Gravity.TOP or Gravity.START, location[0] + 0, location[1] + updateEmail.height)
            toast.show()
        }

        saveBtn.setOnClickListener {
            updateData()
        }
    }

    private fun updateData() {
        var updateName = findViewById<EditText>(R.id.updateName)
        var updateName_ = updateName.text.toString()
        if(updateName_.isEmpty())
        {
            updateName.error = "Enter your name"
            updateName.requestFocus()
            return
        }

        val userId = FirebaseAuth.getInstance().currentUser?.uid
        userId?.let { uid ->
            val dbRef = FirebaseDatabase.getInstance().getReference("Users").child(uid)

            dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                    val userName = snapshot.child("name").getValue(String::class.java)
                    if (userName != null && userName != updateName_) {
                        dbRef.child("name").setValue(updateName_)
                            .addOnSuccessListener {
                                Toast.makeText(this@UpdateProfile, "Name Updated", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener { exception ->
                                Toast.makeText(this@UpdateProfile, "Failed to update name: ${exception.message}", Toast.LENGTH_SHORT).show()
                            }
                    }}
                }
                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@UpdateProfile, error.toString(), Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}
