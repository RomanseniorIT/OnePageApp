package com.example.onepageapp

import android.content.Context
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class PreCashingLayoutManager(
    context: Context,
    columnCount: Int,
    layoutOrientation: Int,
    isReversed: Boolean,
    var extraLayoutSpace: Int
) : GridLayoutManager(context, columnCount, layoutOrientation, isReversed) {

    private val defaultExtraLayoutSpace = 600

    override fun getExtraLayoutSpace(state: RecyclerView.State): Int {
        return if (extraLayoutSpace > 0) {
            extraLayoutSpace
        } else defaultExtraLayoutSpace
    }

}