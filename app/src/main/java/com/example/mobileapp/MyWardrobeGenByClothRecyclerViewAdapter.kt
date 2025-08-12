package com.example.mobileapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobileapp.models.cloth.ClothPreview
import com.example.mobileapp.models.cloth.ClothWardrobeInfo
import com.example.mobileapp.models.look.GeneratedOutfitsInfo

class MyWardrobeGenByClothRecyclerViewAdapter(
    private val values: MutableList<GeneratedOutfitsInfo>
) : RecyclerView.Adapter<MyWardrobeGenByClothRecyclerViewAdapter.ViewHolder>() {

    var onItemClickListener: ((GeneratedOutfitsInfo) -> Unit)? = null
    var onFavClickListener: ((GeneratedOutfitsInfo) -> Unit)? = null

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val outfitTitle: TextView = view.findViewById(R.id.outfitTitle)
        val horizontalRecyclerView: RecyclerView = view.findViewById(R.id.horizontalRecyclerView)
        val favButton: ImageButton = view.findViewById(R.id.favButton)

        fun bind(item: GeneratedOutfitsInfo) {
            // Показываем баллы (score) как заголовок
            val text = String.format("%.1f%%", item.score * 100)
            outfitTitle.text = text
            if (item.like_status) {
                favButton.setImageResource(R.drawable.ic_heart_filled)
            } else {
                favButton.setImageResource(R.drawable.ic_heart_outline)
            }
            // Преобразуем ClothWardrobeInfo -> ClothPreview для вложенного адаптера
            val clothPreviews = item.items.map { wardrobeItem: ClothWardrobeInfo ->
                ClothPreview(
                    id = wardrobeItem.id,
                    picture = wardrobeItem.picture,
                    product_display_name = wardrobeItem.product_display_name
                )
            }

            // Создаём адаптер для горизонтального списка
            val adapter = ClothInWardrobeViewAdapter(clothPreviews.toMutableList())

            horizontalRecyclerView.layoutManager =
                LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)
            horizontalRecyclerView.setHasFixedSize(true)
            horizontalRecyclerView.isNestedScrollingEnabled = false
            horizontalRecyclerView.adapter = adapter

            // Обработка клика по всему элементу
            itemView.setOnClickListener {
                onItemClickListener?.invoke(item)
            }

            // Обработка клика по кнопке избранного
            favButton.setOnClickListener {
                onFavClickListener?.invoke(item)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_item3, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(values[position])
    }

    override fun getItemCount(): Int = values.size
}
