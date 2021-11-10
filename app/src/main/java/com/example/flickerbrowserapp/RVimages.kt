package com.example.flickerbrowserapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.flickerbrowserapp.databinding.ItemImagesBinding

class RVimages(val activity: MainActivity, var imageArray: ArrayList<Images>): RecyclerView.Adapter<RVimages.ViewHolder>() {
    class ViewHolder(val binding: ItemImagesBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemImagesBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val nextImage = imageArray[position]
        holder.binding.apply{
            tvImages.text = nextImage.title
            Glide.with(activity).load(nextImage.link).into(imgSh)
            llSh.setOnClickListener {
                activity.displayFullImage(nextImage.link)
            }
        }
    }

    override fun getItemCount()= imageArray.size
}