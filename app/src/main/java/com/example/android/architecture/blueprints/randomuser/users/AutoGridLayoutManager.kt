package com.example.android.architecture.blueprints.randomuser.users

import android.content.Context
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler

/**
 * RecyclerView GridLayoutManager, auto-detect span count
 * extracted from https://gist.github.com/omidraha/af3aa017d4ec06342bdc03c49d4b83b1#file-gridautofitlayoutmanager-java
 */
class GridAutoFitLayoutManager : GridLayoutManager {
    private var columnWidth = 0
    private var columnWidthChanged = true

    constructor(context: Context?, columnWidth: Int): super(context, 1) {
        setColumnWidth(columnWidth)
    }

    fun setColumnWidth(newColumnWidth: Int) {
        if (newColumnWidth > 0 && newColumnWidth != columnWidth) {
            columnWidth = newColumnWidth
            columnWidthChanged = true
        }
    }

    override fun onLayoutChildren(recycler: Recycler?, state: RecyclerView.State?) {
        if (columnWidthChanged && columnWidth > 0) {
            val totalSpace: Int
            totalSpace = if (orientation == LinearLayoutManager.VERTICAL) {
                width - paddingRight - paddingLeft
            } else {
                height - paddingTop - paddingBottom
            }
            val spanCount = Math.max(1, totalSpace / columnWidth)
            setSpanCount(spanCount)
            columnWidthChanged = false
        }
        super.onLayoutChildren(recycler, state)
    }
}