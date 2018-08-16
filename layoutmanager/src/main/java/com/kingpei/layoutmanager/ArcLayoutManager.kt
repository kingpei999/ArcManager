package com.kingpei.viewmodel.layoutManager

import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.util.SparseArray
import android.util.SparseIntArray
import android.view.View

class ArcLayoutManager(arcCalculator : ArcCalculator) : RecyclerView.LayoutManager() {
    private var totalWidth : Int = 0
    private var allItemRect : SparseArray<Rect> = SparseArray()
    private var horizontalOffset:Int = 0
    private var arcCalculator = arcCalculator
    private var widths : SparseIntArray = SparseIntArray()

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
       return RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT)
    }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        if(itemCount <= 0 || state.isPreLayout){
            return
        }
        detachAndScrapAttachedViews(recycler)
        calculateChildrenSite(recycler)
        recycleAndFillView(recycler, state)
    }

    private fun recycleAndFillView(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        if(itemCount <= 0 || state.isPreLayout){
            return
        }

        var displayRect = Rect(horizontalOffset, 0, horizontalOffset + getHorizontalSpace(), getVerticalSpace())

        var childRect = Rect()
        for (i in 0..(childCount -1)){
            var child: View? = getChildAt(i) ?: continue
            childRect.left = getDecoratedLeft(child)
            childRect.top = getDecoratedTop(child)
            childRect.right = getDecoratedRight(child)
            childRect.bottom = getDecoratedBottom(child)

            if(!Rect.intersects(displayRect, childRect)){
                removeAndRecycleView(child, recycler)
            }
        }

        for (i in 0..(itemCount -1)){
            var rect = allItemRect.get(i)

            var tempWidth = widths.get(i)
            if(totalWidth - tempWidth > arcCalculator.halfWidth * 2){
                if(rect.left - horizontalOffset < -tempWidth){
                    rect.left = rect.left + totalWidth
                    rect.right = rect.right + totalWidth
                }else if(rect.right - horizontalOffset > totalWidth){
                    rect.left = rect.left - totalWidth
                    rect.right = rect.right - totalWidth
                }
            }

            if(Rect.intersects(displayRect, rect)){
                var itemView = recycler.getViewForPosition(i)?:continue
                measureChildWithMargins(itemView, 0, arcCalculator.getUsedHeight())
                addView(itemView)

                var width = getDecoratedMeasuredWidth(itemView)
                widths.put(i, width)

                var left = rect.left - horizontalOffset
                var right = rect.right - horizontalOffset
                var top = arcCalculator.getTopForItem(left, right)
                var bottom = rect.bottom - rect.top + top
                layoutDecoratedWithMargins(itemView, left, top, right, bottom)
            }
        }
    }

    private fun getHorizontalSpace(): Int {
        return width - paddingLeft - paddingRight
    }

    override fun canScrollHorizontally(): Boolean {
        return true
    }

    private fun calculateChildrenSite(recycler: RecyclerView.Recycler) {
        totalWidth = 0

        for (i in 0..(itemCount-1)){
            var view = recycler.getViewForPosition(i)?:continue

            measureChildWithMargins(view, 0, arcCalculator.getUsedHeight())
            calculateItemDecorationsForChild(view, Rect())

            addView(view)

            var width = getDecoratedMeasuredWidth(view)
            var height = getDecoratedMeasuredHeight(view)

            var tempRect = allItemRect.get(i)
            if(tempRect == null) tempRect = Rect()

            var left = totalWidth
            var right = totalWidth + width
            var top = arcCalculator.getTopForItem(left, right)
            var bottom = top + height

            tempRect.set(left, top, right, bottom)

            totalWidth += width

            allItemRect.put(i, tempRect)
        }
    }

    override fun scrollHorizontallyBy(dx: Int, recycler: RecyclerView.Recycler, state: RecyclerView.State): Int {
        detachAndScrapAttachedViews(recycler)

        offsetChildrenHorizontal(-dx)
        recycleAndFillView(recycler, state)
        horizontalOffset += dx

        return dx
    }

    public fun getVerticalSpace():Int{
        return height - paddingTop - paddingBottom
    }
}
