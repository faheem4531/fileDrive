package com.example.filedrive.ui.gallery

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.findViewTreeViewModelStoreOwner
import androidx.navigation.fragment.findNavController
import com.example.filedrive.R
import com.example.filedrive.databinding.FragmentGalleryBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage

class GalleryFragment : Fragment() {

    //storing variables
    private lateinit var dbRef: DatabaseReference
    private  var dbStorage= Firebase.storage
    private lateinit var uploadImage : ImageView
    private lateinit var progresGallery : ProgressBar

    private var uri : Uri ?= null

    //fetching declaration
    private lateinit var listView : ListView
    private lateinit var showImages: ImageView
    private lateinit var adapter: ArrayAdapter<String>
    private val imagesUrlArray = mutableListOf("")


    private var _binding: FragmentGalleryBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        val root: View = binding.root


        // Code Starting
        dbStorage = FirebaseStorage.getInstance()
        uploadImage  = root.findViewById (R.id.uploadImage)
        progresGallery = root.findViewById (R.id.uploadImageProgress)
        showImages = root.findViewById  (R.id.displayImages)
        listView = root.findViewById (R.id.imageListView)


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

//        dbRef.addValueEventListener(object : ValueEventListener{
//            override fun onDataChange(snapshot: DataSnapshot) {
//                imagesUrlArray.clear()
//                for (image in snapshot.children){
//                    val currentImage = image.getValue(String::class.java)
//                    currentImage?.let {
//                        imagesUrlArray.add(it)
//                    }
//                }
//                adapter.notifyDataSetChanged()
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                Log.e("Firebase", "Error: ${error.message}")
//            }
//        })

//        Apply Adapter
//        adapter = ArrayAdapter(requireContext(),R.layout.fragment_gallery,imagesUrlArray )
//        listView.adapter= adapter


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