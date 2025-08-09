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


class MyClothRecyclerViewAdapter(
    private val items: MutableList<ClothPreview>
) : RecyclerView.Adapter<MyClothRecyclerViewAdapter.ViewHolder>() {

    var onItemClickListener: ((ClothPreview) -> Unit)? = null
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val id:TextView = view.findViewById(R.id.hiddenInput)
        val imageView: ImageView = view.findViewById(R.id.imageView)
        val itemName: TextView = view.findViewById(R.id.item_name)
        init {
            view.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = items[position]
                    onItemClickListener?.invoke(item)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        val imageBytes = Utils.decodeBase64(item.picture)
        holder.id.text = (item.id.toString())
        holder.imageView.setImageBitmap(imageBytes)
        holder.itemName.text = item.product_display_name
    }

    fun updateItems(newItems: List<ClothPreview>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }


    override fun getItemCount(): Int = items.size
}
