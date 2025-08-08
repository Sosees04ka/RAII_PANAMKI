package com.example.mobileapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class LookItem(val imageResId: Int)

class MyLookRecyclerViewAdapter(
    private val items: List<LookItem>
) : RecyclerView.Adapter<MyLookRecyclerViewAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView1: ImageView = view.findViewById(R.id.imageView1)
        val imageView2: ImageView = view.findViewById(R.id.imageView2)
        val imageView3: ImageView = view.findViewById(R.id.imageView3)
        val imageView4: ImageView = view.findViewById(R.id.imageView4)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_item2, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.imageView1.setImageResource(item.imageResId)
        holder.imageView2.setImageResource(item.imageResId)
        holder.imageView3.setImageResource(item.imageResId)
        holder.imageView4.setImageResource(item.imageResId)
    }

    override fun getItemCount(): Int = items.size
}
