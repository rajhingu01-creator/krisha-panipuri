package com.dadaschatpos.ui.bill

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dadaschatpos.data.model.OrderItemEntity
import com.dadaschatpos.databinding.ItemBillLineBinding
import com.dadaschatpos.util.CurrencyFormatter

class BillItemAdapter : ListAdapter<OrderItemEntity, BillItemAdapter.BillLineViewHolder>(DiffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BillLineViewHolder {
        val binding = ItemBillLineBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BillLineViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BillLineViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class BillLineViewHolder(private val binding: ItemBillLineBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: OrderItemEntity) = with(binding) {
            itemNameText.text = item.itemName
            qtyText.text = item.qty.toString()
            priceText.text = CurrencyFormatter.format(item.price)
            amountText.text = CurrencyFormatter.format(item.amount)
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<OrderItemEntity>() {
        override fun areItemsTheSame(oldItem: OrderItemEntity, newItem: OrderItemEntity): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: OrderItemEntity, newItem: OrderItemEntity): Boolean = oldItem == newItem
    }
}
