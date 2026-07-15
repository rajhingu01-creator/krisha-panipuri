package com.dadaschatpos.data.model

data class CartItem(
    val item: ItemEntity,
    val quantity: Int
) {
    val amount: Double
        get() = item.price * quantity
}
