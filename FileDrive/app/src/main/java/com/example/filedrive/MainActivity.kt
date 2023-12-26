package com.example.filedrive

import android.os.Bundle
import android.view.Menu
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.filedrive.databinding.ActivityMainBinding


import android.content.Intent
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.core.net.toUri
import androidx.navigation.ui.NavigationUI
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
//import com.example.navmanuapp.databinding.ActivityNavigationViewBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var currentUser: FirebaseUser


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)


        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)



//started code from here
        var auth: FirebaseAuth=FirebaseAuth.getInstance()

        //function call to get username and email to show on header
        displayUserInfo(auth)

        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_logout -> {
                    auth.signOut()
                    startActivity(Intent(this, Login::class.java))
                    finish()
                    true
                }
                else -> {
                    // Perform fragment navigation for other menu items
                    menuItem.isChecked = true
                    drawerLayout.closeDrawers()
                    val handled = NavigationUI.onNavDestinationSelected(menuItem, navController)
                    handled || super.onOptionsItemSelected(menuItem)
                }
            }
        }
    }



    private fun displayUserInfo(auth: FirebaseAuth) {

        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val dbRef = FirebaseDatabase.getInstance().getReference("Users").child(userId.toString())

        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val dbName = snapshot.child("name").getValue(String::class.java)
                    val dbEmail = snapshot.child("email").getValue(String::class.java)
                    val dbImage = snapshot.child("imageUrl").getValue(String::class.java)

                    val headerView = binding.navView.getHeaderView(0)
                    val userNameTextView: TextView = headerView.findViewById(R.id.userName)
                    val userEmailTextView: TextView = headerView.findViewById(R.id.userEmail)
                    val displayImage : ImageView = headerView.findViewById(R.id.imageView)

                    userNameTextView.text = dbName
                    userEmailTextView.text = dbEmail

                    if (dbImage != null) {
                        Glide.with(this@MainActivity) // Use your activity reference
                            .load(dbImage)
                            .transform(CircleCrop()) // Apply circular transformation
                            .into(displayImage)
                    }
                    //                    else{
                    //                        var dumyImage= "https://firebasestorage.googleapis.com/v0/b/nav-manu-app.appspot.com/o/Profile%20Images%2F1702379430996?alt=media&token=4b126353-1d61-4856-adca-9761736e011a"
                    //                        Glide.with(this@NavigationViewActivity)
                    //                            .load(dumyImage)
                    //                            .transform(CircleCrop())
                    //                            .into(displayImage)
                    //
                    //                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle an error in fetching the data
                val errorMessage = "Error: ${error.message}" // Get the error message from DatabaseError

                // Use applicationContext or pass a valid context to Toast.makeText()
                Toast.makeText(applicationContext, errorMessage, Toast.LENGTH_SHORT).show()
            }

        })

    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.close_app -> {
                finishAffinity()
                true
            }
            R.id.restart_app -> {
                startActivity(Intent(this,Signup::class.java))
                finish()
                true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}