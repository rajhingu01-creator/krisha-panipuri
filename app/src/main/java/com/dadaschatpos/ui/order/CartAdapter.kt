package com.dadaschatpos.ui.order

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dadaschatpos.data.model.CartItem
import com.dadaschatpos.databinding.ItemCartBinding
import com.dadaschatpos.util.CurrencyFormatter

class CartAdapter(
    private val onIncrease: (Long) -> Unit,
    private val onDecrease: (Long) -> Unit,
    private val onRemove: (Long) -> Unit
) : ListAdapter<CartItem, CartAdapter.CartViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding, onIncrease, onDecrease, onRemove)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class CartViewHolder(
        private val binding: ItemCartBinding,
        private val onIncrease: (Long) -> Unit,
        private val onDecrease: (Long) -> Unit,
        private val onRemove: (Long) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(cartItem: CartItem) = with(binding) {
            cartItemNameText.text = cartItem.item.name
            quantityText.text = cartItem.quantity.toString()
            cartItemAmountText.text = "${CurrencyFormatter.format(cartItem.item.price)} x ${cartItem.quantity} = ${CurrencyFormatter.format(cartItem.amount)}"
            increaseButton.setOnClickListener { onIncrease(cartItem.item.id) }
            decreaseButton.setOnClickListener { onDecrease(cartItem.item.id) }
            removeButton.setOnClickListener { onRemove(cartItem.item.id) }
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<CartItem>() {
        override fun areItemsTheSame(oldItem: CartItem, newItem: CartItem): Boolean = oldItem.item.id == newItem.item.id
        override fun areContentsTheSame(oldItem: CartItem, newItem: CartItem): Boolean = oldItem == newItem
    }
}
