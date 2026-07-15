package com.dadaschatpos.ui.settings

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dadaschatpos.data.DefaultData
import com.dadaschatpos.data.model.ItemEntity
import com.dadaschatpos.databinding.ItemManageItemBinding
import com.dadaschatpos.util.CurrencyFormatter
import com.dadaschatpos.util.ImageLoader

class ManageItemsAdapter(
    private val onEdit: (ItemEntity) -> Unit,
    private val onDelete: (ItemEntity) -> Unit
) : ListAdapter<ItemEntity, ManageItemsAdapter.ManageItemViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ManageItemViewHolder {
        val binding = ItemManageItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ManageItemViewHolder(binding, onEdit, onDelete)
    }

    override fun onBindViewHolder(holder: ManageItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ManageItemViewHolder(
        private val binding: ItemManageItemBinding,
        private val onEdit: (ItemEntity) -> Unit,
        private val onDelete: (ItemEntity) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ItemEntity) = with(binding) {
            manageItemNameText.text = item.name
            manageItemCategoryText.text = item.category
            manageItemPriceText.text = CurrencyFormatter.format(item.price)
            ImageLoader.load(manageItemImage, DefaultData.displayImageFor(item))
            editItemButton.setOnClickListener { onEdit(item) }
            deleteItemButton.setOnClickListener { onDelete(item) }
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<ItemEntity>() {
        override fun areItemsTheSame(oldItem: ItemEntity, newItem: ItemEntity): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: ItemEntity, newItem: ItemEntity): Boolean = oldItem == newItem
    }
}
