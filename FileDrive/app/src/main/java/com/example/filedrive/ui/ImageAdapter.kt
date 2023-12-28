package com.example.filedrive.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.filedrive.R
import android.content.Context

class ImageAdapter(
    private val context: Context,
    private val listImages: ArrayList<String>
) : RecyclerView.Adapter<ImageAdapter.ViewHolder> () {

    class ViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView){
        val imageView: ImageView = itemView.findViewById(R.id.imageView2)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item_image,parent,false)

        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(context).load(listImages[position]).into(holder.imageView)
    }

    override fun getItemCount(): Int {
        return listImages.size
    }
}