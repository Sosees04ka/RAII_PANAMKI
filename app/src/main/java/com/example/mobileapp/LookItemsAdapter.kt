package com.example.mobileapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.mobileapp.models.cloth.ClothWardrobeInfo
import com.example.mobileapp.utils.Utils

class LookItemsAdapter(
    private val items: List<ClothWardrobeInfo>
) : RecyclerView.Adapter<LookItemsAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val imageView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cloth_image, parent, false)
        return ViewHolder(imageView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val picture = items[position].picture
        if (!picture.isNullOrEmpty() && picture != "none") {
            holder.imageView.setImageBitmap(Utils.decodeBase64(picture))
        } else {
            holder.imageView.setImageResource(R.drawable.photo)
        }
    }

    override fun getItemCount(): Int = items.size
}
