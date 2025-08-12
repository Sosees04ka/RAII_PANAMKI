package com.example.mobileapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobileapp.models.cloth.ClothPreview
import com.example.mobileapp.models.look.GeneratedOutfitsInfo

class LooksAdapter(
    private val values: MutableList<GeneratedOutfitsInfo>
) : RecyclerView.Adapter<LooksAdapter.ViewHolder>() {

    var onItemClickListener: ((GeneratedOutfitsInfo) -> Unit)? = null
    var onFavClickListener: ((GeneratedOutfitsInfo) -> Unit)? = null

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)
        val favButton: ImageButton = view.findViewById(R.id.favButton)
        init {
            itemView.isClickable = true
            itemView.isFocusable = true
        }

        fun bind(item: GeneratedOutfitsInfo) {
            favButton.setImageResource(
                if (item.like_status) R.drawable.ic_heart_filled
                else R.drawable.ic_heart_outline
            )

            recyclerView.layoutManager = GridLayoutManager(itemView.context, 2)
            recyclerView.adapter = LookItemsAdapter(item.items)

            itemView.setOnClickListener { onItemClickListener?.invoke(item) }

            favButton.setOnClickListener {
                onFavClickListener?.invoke(item)
                it.isPressed = false // чтобы убрать залипание
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_item2, parent, false)
        return ViewHolder(view)
    }

    fun updateItems(newItems: MutableList<GeneratedOutfitsInfo>) {
        values.clear()
        values.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.bind(item)
    }


    override fun getItemCount(): Int = values.size
}

