package com.example.filedrive

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage


class Signup : AppCompatActivity() {

    private lateinit var dbRef : DatabaseReference
    private  var dbStorage = Firebase.storage

    private lateinit var imageView: ImageView
    private lateinit var btnGallery: Button
    private lateinit var btnUploadImage: Button

    private var uri : Uri? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)


        supportActionBar?.hide()

        dbStorage = FirebaseStorage.getInstance()

        var signUpBtn = findViewById <Button>  (R.id.signup_btn)
        btnUploadImage = findViewById  (R.id.imageUploadBtn)
        btnGallery  =  findViewById   (R.id.imagePikerBtn)
        imageView = findViewById (R.id.signupImageShow)

        var galleryImage = registerForActivityResult(
            ActivityResultContracts.GetContent(),
            ActivityResultCallback {Url ->
                Url?.let {
                    imageView.setImageURI(Url)
                    uri = Url
                }
            }
        )

        btnGallery.setOnClickListener {
            galleryImage.launch("image/*")
        }

        btnUploadImage.setOnClickListener {

            var progress = findViewById <LinearLayout>  (R.id.progress_id)
            var form =     findViewById <LinearLayout>  (R.id.form_container)
            var checkbox = findViewById <CheckBox> (R.id.imageChecked)

            //upload image to fireBase store
            uri?.let { // Check if uri is not null
                form.visibility =     View.GONE
                progress.visibility = View.VISIBLE

                dbStorage.getReference("Profile Images").child(System.currentTimeMillis().toString())
                    .putFile(it)
                    .addOnSuccessListener { task ->
                        task.metadata?.reference?.downloadUrl
                            ?.addOnSuccessListener {downloadUri->

                                uri = downloadUri
                                form.visibility =     View.VISIBLE
                                progress.visibility = View.GONE
                                checkbox.visibility = View.VISIBLE
                                imageView.visibility = View.GONE
                                btnUploadImage.visibility = View.GONE
                                btnGallery.visibility = View.GONE

                                Toast.makeText(this, "Image Uploaded", Toast.LENGTH_SHORT).show()
                            }
                            ?.addOnFailureListener { exception ->
                                Toast.makeText(this, exception.toString(), Toast.LENGTH_SHORT).show()
                            }
                    }
            } ?: run {
                Toast.makeText(this, "Please select an image first", Toast.LENGTH_SHORT).show()
            }
        }

        signUpBtn.setOnClickListener {
            post_signup_data_to_firebase()
        }

    }


    private fun post_signup_data_to_firebase() {

        //    decleration
        var name =     findViewById<EditText> (R.id.name_id)
        var email =    findViewById<EditText> (R.id.email_id)
        var password = findViewById<EditText> (R.id.password_id)

        //    convert to string
        var name_ =     name.text.toString()
        var email_ =    email.text.toString()
        var password_ = password.text.toString()


        //    Validation
        if(name_.isEmpty())
        {
            name.error = "enter your name"
            name.requestFocus()
            return
        }
        if(email_.isEmpty())
        {
            email.error = "enter valid email address"
            email.requestFocus()
            return
        }
        if(password_.isEmpty())
        {
            password.error = "enter valid email address"
            password.requestFocus()
            return
        }





        //    Send data to firebase
        val auth: FirebaseAuth = FirebaseAuth.getInstance()

        var progress = findViewById<LinearLayout>  (R.id.progress_id)
        var form =     findViewById<LinearLayout>  (R.id.form_container)

        form.visibility =     View.GONE
        progress.visibility = View.VISIBLE

        auth.createUserWithEmailAndPassword(email_,password_).addOnCompleteListener {

            form.visibility=View.VISIBLE
            progress.visibility=View.GONE

            if (it.isSuccessful) {

                //     Store user Data in DB
                val userId = FirebaseAuth.getInstance().currentUser?.uid
                dbRef = FirebaseDatabase.getInstance().getReference("Users").child(userId.toString())
                val signupDetails = SignupClass(name_,email_,uri.toString())
                dbRef.setValue(signupDetails)

                //Toster
                Toast.makeText(this, "Signup Successful", Toast.LENGTH_SHORT).show()
                name.text = null
                email.text = null
                password.text = null
                var home = Intent(this,MainActivity::class.java)
                startActivity(home)
            }
            else
                Toast.makeText(this,"Signup Fail : " +it.exception,Toast.LENGTH_SHORT).show()
        }
    }

    fun signinIntent(view: View){
        startActivity(Intent(this, Login::class.java))
    }

}


class SignupClass( name_: String, email_: String,uri:String) {
    var name:String=""
    var email:String=""
    var imageUrl:String=""
    init {
        this.name = name_
        this.email = email_
        this.imageUrl = uri
    }
}
