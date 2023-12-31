package com.example.filedrive.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.filedrive.R
import android.content.Context
import android.content.Intent
import com.example.filedrive.ViewFullImage

class ImageAdapter(
    private val context: Context,
    private val listImages: ArrayList<String>,
    private val imageClickListener: (String) -> Unit,
    private val imageLongClickListener: (String) -> Unit
) : RecyclerView.Adapter<ImageAdapter.ViewHolder> () {

    class ViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView){
        val imageView: ImageView = itemView.findViewById(R.id.imageView2)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item_image,parent,false)

        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val imageUrl = listImages[position]

        Glide.with(context).load(listImages[position]).into(holder.imageView)

        // Click listener
        holder.imageView.setOnClickListener {
            // Pass the clicked image URL to the click listener
            val intent = Intent(context, ViewFullImage::class.java)
            intent.putExtra("IMAGE_URL", imageUrl)
            context.startActivity(intent)

            imageClickListener.invoke(imageUrl)
        }

        // Long click listener
        holder.imageView.setOnLongClickListener {
            // Pass the long-clicked image URL to the long click listener
            imageLongClickListener.invoke(imageUrl)
            true // Consume the long click event
        }
    }

    override fun getItemCount(): Int {
        return listImages.size
    }
}