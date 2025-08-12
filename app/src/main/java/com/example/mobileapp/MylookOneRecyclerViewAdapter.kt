package com.example.mobileapp

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mobileapp.models.cloth.ClothWardrobeInfo
import com.example.mobileapp.utils.Utils

class MylookOneRecyclerViewAdapter(
    private val values: MutableList<ClothWardrobeInfo>
) : RecyclerView.Adapter<MylookOneRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_item4, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]

        // Цвет
        val r = item.base_color.getOrNull(0) ?: 0
        val g = item.base_color.getOrNull(1) ?: 0
        val b = item.base_color.getOrNull(2) ?: 0
        val color = Color.rgb(r, g, b)
        holder.colorView.setBackgroundColor(color)

        // Картинка
        if (item.picture.isNotEmpty() && item.picture != "none") {
            val bitmap = Utils.decodeBase64(item.picture)
            holder.picture.setImageBitmap(bitmap)
        } else {
            holder.picture.setImageResource(R.drawable.photo)
        }

        // Остальные поля
        holder.nameCloth.text = item.product_display_name
        holder.subCategory.text = item.sub_category
        holder.masterCategory.text = item.master_category
        holder.usage.text = item.usage
    }

    fun updateItems(newItems: List<ClothWardrobeInfo>) {
        values.clear()
        values.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val picture: ImageView = view.findViewById(R.id.picture)
        val nameCloth: TextView = view.findViewById(R.id.nameCloth)
        val subCategory: TextView = view.findViewById(R.id.subCategory)
        val masterCategory: TextView = view.findViewById(R.id.masterCategory)
        val usage: TextView = view.findViewById(R.id.usage)
        val colorView: View = view.findViewById(R.id.color)
    }
}
