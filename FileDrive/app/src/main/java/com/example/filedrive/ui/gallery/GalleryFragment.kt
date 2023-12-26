package com.example.filedrive.ui.gallery

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
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
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage

class GalleryFragment : Fragment() {

    private lateinit var dbRef: DatabaseReference
    private  var dbStorage= Firebase.storage
    private lateinit var uploadImage : ImageView

    private var uri : Uri ?= null



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

        var galleryImage = registerForActivityResult(
            ActivityResultContracts.GetContent(),
          ActivityResultCallback  {Url->
                Url?.let{
                    uri = Url
                }
            }
        )

        uploadImage.setOnClickListener{
            galleryImage.launch("image/*")
            UploadImage()
        }

        return root
    }

    private fun UploadImage() {
        uri?.let {
            var userId = FirebaseAuth.getInstance().currentUser?.uid
            dbStorage.getReference("Gallery Images").child(userId.toString()).child(System.currentTimeMillis().toString())
                .putFile(it)
                .addOnSuccessListener { task->
                    task.metadata?.reference?.downloadUrl?.addOnSuccessListener { url ->
                        uri = url
                        Toast.makeText(requireContext(), "upload sucess", Toast.LENGTH_SHORT).show()
                    }
                        ?.addOnFailureListener{
                            Toast.makeText(requireContext(), it.toString(), Toast.LENGTH_SHORT).show()
                        }
                }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}