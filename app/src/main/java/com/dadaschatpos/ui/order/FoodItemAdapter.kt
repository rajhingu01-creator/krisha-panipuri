package com.dadaschatpos.ui.order

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dadaschatpos.data.DefaultData
import com.dadaschatpos.data.model.ItemEntity
import com.dadaschatpos.databinding.ItemFoodBinding
import com.dadaschatpos.util.CurrencyFormatter
import com.dadaschatpos.util.ImageLoader

class FoodItemAdapter(
    private val onAddClick: (ItemEntity) -> Unit
) : ListAdapter<ItemEntity, FoodItemAdapter.FoodItemViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodItemViewHolder {
        val binding = ItemFoodBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FoodItemViewHolder(binding, onAddClick)
    }

    override fun onBindViewHolder(holder: FoodItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class FoodItemViewHolder(
        private val binding: ItemFoodBinding,
        private val onAddClick: (ItemEntity) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ItemEntity) = with(binding) {
            itemNameText.text = item.name
            itemPriceText.text = CurrencyFormatter.format(item.price)
            ImageLoader.load(itemImage, DefaultData.displayImageFor(item))
            addButton.setOnClickListener { onAddClick(item) }
            root.setOnClickListener { onAddClick(item) }
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<ItemEntity>() {
        override fun areItemsTheSame(oldItem: ItemEntity, newItem: ItemEntity): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: ItemEntity, newItem: ItemEntity): Boolean = oldItem == newItem
    }
}
