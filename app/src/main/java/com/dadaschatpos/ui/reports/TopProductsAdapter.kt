package com.dadaschatpos.ui.reports

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dadaschatpos.data.model.TopProduct
import com.dadaschatpos.databinding.ItemTopProductBinding
import com.dadaschatpos.util.CurrencyFormatter

class TopProductsAdapter : ListAdapter<TopProduct, TopProductsAdapter.TopProductViewHolder>(DiffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopProductViewHolder {
        val binding = ItemTopProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TopProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TopProductViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class TopProductViewHolder(private val binding: ItemTopProductBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(product: TopProduct) = with(binding) {
            productNameText.text = product.name
            productQtyText.text = "Qty ${product.qty}"
            productTotalText.text = CurrencyFormatter.format(product.total)
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<TopProduct>() {
        override fun areItemsTheSame(oldItem: TopProduct, newItem: TopProduct): Boolean = oldItem.name == newItem.name
        override fun areContentsTheSame(oldItem: TopProduct, newItem: TopProduct): Boolean = oldItem == newItem
    }
}
