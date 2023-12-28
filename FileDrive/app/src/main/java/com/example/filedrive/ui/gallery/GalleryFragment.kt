package com.example.filedrive.ui.gallery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.filedrive.R
import com.example.filedrive.databinding.FragmentGalleryBinding
import com.example.filedrive.ui.ImageAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class GalleryFragment : Fragment() {

    //Declarations
    private lateinit var dbRef: DatabaseReference
    private lateinit var recyclerView: RecyclerView
    private lateinit var listImages: ArrayList<String>



    private var _binding: FragmentGalleryBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        val root: View = binding.root




        //=>  Code Starting
        recyclerView= root.findViewById (R.id.recycler)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        listImages = arrayListOf()
        var imageLoader = root.findViewById<ProgressBar>(R.id.imageLoader)
        var noImage = root.findViewById<ImageView> (R.id.noImageFound)


        // Initialize dbRef after Firebase initialization
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        userId?.let {
            dbRef = FirebaseDatabase.getInstance().getReference("Users").child(userId)
                .child("galleryImagesUrl")
        }

        dbRef.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    noImage.visibility = View.GONE
                    for (image in snapshot.children){
                        val currentImage = image.getValue(String::class.java)
                        listImages.add(currentImage!!)  // !! means shouldn't be null
                    }
                    imageLoader.visibility= View.GONE
                    recyclerView.adapter = ImageAdapter (requireContext(), listImages)
                }
                else{
                    imageLoader.visibility= View.GONE
                    noImage.visibility = View.VISIBLE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), error.toString(), Toast.LENGTH_SHORT).show()
            }
        })



        return root
    }




    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}