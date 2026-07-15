package com.dadaschatpos.util

import android.net.Uri
import android.widget.ImageView
import com.dadaschatpos.R

object ImageLoader {
    fun load(imageView: ImageView, image: String?) {
        if (image.isNullOrBlank()) {
            imageView.setImageResource(R.drawable.ic_food_panipuri)
            return
        }

        if (image.startsWith("content://") || image.startsWith("file://")) {
            runCatching { imageView.setImageURI(Uri.parse(image)) }
                .onFailure { imageView.setImageResource(R.drawable.ic_food_panipuri) }
            return
        }

        val resourceId = imageView.context.resources.getIdentifier(
            image,
            "drawable",
            imageView.context.packageName
        )
        imageView.setImageResource(if (resourceId != 0) resourceId else R.drawable.ic_food_panipuri)
    }
}
