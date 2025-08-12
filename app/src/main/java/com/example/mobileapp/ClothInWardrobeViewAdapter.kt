package com.example.mobileapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.mobileapp.models.cloth.ClothPreview
import com.example.mobileapp.utils.Utils


class ClothInWardrobeViewAdapter(
    private val items: MutableList<ClothPreview>
) : RecyclerView.Adapter<ClothInWardrobeViewAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val id:TextView = view.findViewById(R.id.hiddenInput)
        val imageView: ImageView = view.findViewById(R.id.imageView)
        val itemName: TextView = view.findViewById(R.id.item_name)
        init {
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.id.text = item.id.toString()

        if (!item.picture.isNullOrEmpty() && item.picture != "none") {
            val imageBytes = Utils.decodeBase64(item.picture)
            holder.imageView.setImageBitmap(imageBytes)
        } else {
            holder.imageView.setImageResource(R.drawable.photo) // запасное изображение
        }

        holder.itemName.text = item.product_display_name
    }


    fun updateItems(newItems: List<ClothPreview>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }


    override fun getItemCount(): Int = items.size
}
