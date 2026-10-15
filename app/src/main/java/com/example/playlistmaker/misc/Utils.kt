package com.example.playlistmaker.misc
import android.content.Context
import android.util.TypedValue
import com.google.gson.Gson

object Utils {
    val gson = Gson()

    fun dpToPx(ctx: Context, dp: Float): Int {
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                ctx.resources.displayMetrics
            ).toInt()
    }
}