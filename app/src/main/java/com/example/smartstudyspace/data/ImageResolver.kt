package com.example.smartstudyspace.data

import android.content.Context
import com.example.smartstudyspace.R

object ImageResolver {
    fun resolveDrawable(context: Context, imageUrl: String): Int {
        return when (imageUrl.lowercase()) {
            "bg_library" -> R.drawable.bg_library
            "img_1" -> R.drawable.img_1
            "img_2" -> R.drawable.img_2
            "img_3" -> R.drawable.img_3
            else -> {
                val resId = context.resources.getIdentifier(
                    imageUrl, "drawable", context.packageName
                )
                if (resId != 0) resId else R.drawable.bg_library
            }
        }
    }
}
