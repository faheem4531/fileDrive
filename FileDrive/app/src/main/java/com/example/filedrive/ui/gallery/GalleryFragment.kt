package com.example.filedrive.ui.gallery

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.filedrive.R
import com.example.filedrive.databinding.FragmentGalleryBinding
import com.example.filedrive.ui.ImageAdapter
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.storage
import androidx.activity.result.ActivityResultCallback

class GalleryFragment : Fragment() {

    //storing variables
    private  var dbStorage= Firebase.storage
    private lateinit var uploadImage : ImageView
    private lateinit var progresGallery : ProgressBar

    private var uri : Uri?= null

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



        uploadImage  = root.findViewById (R.id.uploadImage)
        progresGallery = root.findViewById (R.id.uploadImageProgress)
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

        var galleryImage = registerForActivityResult(
            ActivityResultContracts.GetContent(),
            ActivityResultCallback  {url->
                url?.let{
                    uri = url
                    uploadImage(url)
                }
            }
        )

        uploadImage.setOnClickListener{
            galleryImage.launch("image/*")
        }


        dbRef.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                listImages.clear()
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

    private fun uploadImage(imageUri: Uri) {

        imageUri?.let {
            uploadImage.visibility = View.GONE
            progresGallery.visibility = View.VISIBLE

            var userId = FirebaseAuth.getInstance().currentUser?.uid

            dbStorage.getReference("Gallery Images").child(userId.toString())
                .child(System.currentTimeMillis().toString())
                .putFile(imageUri)
                .addOnSuccessListener { task ->
                    task.metadata?.reference?.downloadUrl?.addOnSuccessListener { url ->
                        uri = url
                        Toast.makeText(requireContext(), "upload sucess", Toast.LENGTH_SHORT).show()


                        //store image url in realTime database
                        dbRef.push().setValue(uri.toString()).addOnFailureListener {
                            Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_SHORT).show()
                        }

                        uploadImage.visibility = View.VISIBLE
                        progresGallery.visibility = View.GONE
                    }
                        ?.addOnFailureListener {
                            Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_SHORT)
                                .show()
                        }
                }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}