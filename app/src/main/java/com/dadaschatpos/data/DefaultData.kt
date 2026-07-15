package com.dadaschatpos.data

import com.dadaschatpos.data.model.ItemEntity

object DefaultData {
    const val MENU_VERSION = 5

    fun defaultItems(): List<ItemEntity> = listOf(
        ItemEntity(name = "Pani Puri / पानी पुरी", price = 25.0, image = "photo_panipuri", category = "Chaat"),
        ItemEntity(name = "Bhel / भेल", price = 50.0, image = "photo_bhel", category = "Chaat"),
        ItemEntity(name = "Bread Pakoda / ब्रेड पकोड़ा", price = 25.0, image = "photo_bread_pakoda", category = "Snack"),
        ItemEntity(name = "Cheese Bread Pakoda / चीज़ ब्रेड पकोड़ा", price = 40.0, image = "photo_bread_pakoda", category = "Cheese"),
        ItemEntity(name = "Sev Puri / सेव पुरी", price = 50.0, image = "photo_sevpuri", category = "Chaat"),
        ItemEntity(name = "Cheese Sev Puri / चीज़ सेव पुरी", price = 70.0, image = "photo_sevpuri", category = "Cheese"),
        ItemEntity(name = "Dahi Puri / दही पुरी", price = 50.0, image = "photo_dahipuri", category = "Chaat"),
        ItemEntity(name = "Cheese Dahi Puri / चीज़ दही पुरी", price = 70.0, image = "photo_dahipuri", category = "Cheese"),
        ItemEntity(name = "Basket Chaat / बास्केट चाट", price = 60.0, image = "photo_basket_chat", category = "Chaat"),
        ItemEntity(name = "Cheese Basket Chaat / चीज़ बास्केट चाट", price = 80.0, image = "photo_cheese_basket_chat", category = "Cheese"),
        ItemEntity(name = "Dahi Kachori Chaat / दही कचोरी चाट", price = 60.0, image = "photo_dahi_kachori", category = "Chaat"),
        ItemEntity(name = "Cheese Dahi Kachori Chaat / चीज़ दही कचोरी चाट", price = 80.0, image = "photo_dahi_kachori", category = "Cheese")
    )

    fun displayImageFor(item: ItemEntity): String {
        val defaultImage = defaultItems().firstOrNull { it.name == item.name }?.image
        return when {
            item.image.isBlank() -> defaultImage.orEmpty()
            item.image.startsWith("content://") || item.image.startsWith("file://") -> item.image
            item.image.startsWith("ic_food_") -> defaultImage ?: item.image
            else -> item.image
        }
    }

    fun removedDefaultItemNames(): List<String> = listOf(
        "પાણીપુરી",
        "ભેળ",
        "ઘૂઘરા",
        "બ્રેડ પકોડા",
        "ચીઝ બ્રેડ પકોડા (અમુલ ચીઝ)",
        "સેવ પુરી",
        "ચીઝ સેવ પુરી (અમુલ ચીઝ)",
        "દહીં પુરી",
        "ચીઝ દહીં પુરી (અમુલ ચીઝ)",
        "બાસ્કેટ ચાટ",
        "ચીઝ બાસ્કેટ ચાટ (અમુલ ચીઝ)",
        "દહીં કચોરી ચાટ",
        "ચીઝ દહીં કચોરી ચાટ (અમુલ ચીઝ)",
        "બ્લાસ્ટર ચાટ",
        "ચીઝ સેવ પુરી",
        "ચીઝ દહીં પુરી",
        "ચીઝ બ્લાસ્ટર ચાટ"
    )
}
