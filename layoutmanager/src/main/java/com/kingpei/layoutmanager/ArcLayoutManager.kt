package com.kingpei.viewmodel.layoutManager

import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.util.SparseArray
import android.util.SparseIntArray
import android.view.View

class ArcLayoutManager(arcCalculator: ArcCalculator) : RecyclerView.LayoutManager() {
    private var totalWidth: Int = 0
    private var allItemRect: SparseArray<Rect> = SparseArray()
    private var horizontalOffset: Int = 0
    private var arcCalculator = arcCalculator
    private var widths: SparseIntArray = SparseIntArray()
    private var mFirstInit: Boolean = true

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT)
    }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        if (itemCount <= 0 || state.isPreLayout) {
            return
        }
        //先全部废弃当前attached的子项
        detachAndScrapAttachedViews(recycler)
        if (mFirstInit) {
            calculateChildrenSite(recycler)
            mFirstInit = false
        }

        recycleAndFillView(recycler, state)
    }

    private fun recycleAndFillView(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        if (itemCount <= 0 || state.isPreLayout) {
            return
        }

        //展示区域，horizontalOffset是滑动的距离
        var displayRect = Rect(horizontalOffset, 0, horizontalOffset + getHorizontalSpace(), getVerticalSpace())
        Log.d("ArcLayoutManager", "recycleAndFillView displayRect:$displayRect")

        var childRect = Rect()

        //遍历当前的child
        for (i in 0 until childCount) {
            var child: View? = getChildAt(i) ?: continue
            childRect.left = getDecoratedLeft(child)
            childRect.top = getDecoratedTop(child)
            childRect.right = getDecoratedRight(child)
            childRect.bottom = getDecoratedBottom(child)

            Log.d("ArcLayoutManager", "recycleAndFillView childRect:$childRect")

            //不在显示范围的子项，删除并回收
            if (!Rect.intersects(displayRect, childRect)) {
                removeAndRecycleView(child, recycler)
            }
        }

        //遍历所有的item
        for (i in 0 until itemCount) {
            var rect = allItemRect.get(i)
            var tempWidth = widths.get(i)

            //当items的总长度大于RecyclerView的宽度时
            if (totalWidth - arcCalculator.halfWidth * 2 > tempWidth) {
                //已经向左移动出边界(宽度为totalWidth)的子项
                if (rect.left + tempWidth < horizontalOffset) {
                    rect.left = rect.left + totalWidth
                    rect.right = rect.right + totalWidth
                    //已经向右移动出边界(宽度为totalWidth)的子项
                } else if (rect.right - totalWidth > horizontalOffset) {
                    rect.left = rect.left - totalWidth
                    rect.right = rect.right - totalWidth
                }
            }

            if (Rect.intersects(displayRect, rect)) {
                var itemView = recycler.getViewForPosition(i) ?: continue
                measureChildWithMargins(itemView, 0, arcCalculator.getUsedHeight())
                addView(itemView)

                //对于在显示区域中的布局，计算layout的rect，并布局
                var left = rect.left - horizontalOffset
                var right = rect.right - horizontalOffset
                var top = arcCalculator.getTopForItem(left, right)
                var bottom = rect.bottom - rect.top + top
                layoutDecoratedWithMargins(itemView, left, top, right, bottom)

                Log.d("ArcLayoutManager", "recycleAndFillView left:$left -- right:$right -- top:$top -- bottom:$bottom")
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

        for (i in 0 until itemCount) {
            var view = recycler.getViewForPosition(i) ?: continue

            //测量子项，usedHeight是弧形的高度
            measureChildWithMargins(view, 0, arcCalculator.getUsedHeight())

            var width = getDecoratedMeasuredWidth(view)
            widths.put(i, width)
            var height = getDecoratedMeasuredHeight(view)

            var tempRect = allItemRect.get(i)
            if (tempRect == null) tempRect = Rect()

            //计算每个子项rect
            var left = totalWidth
            var right = totalWidth + width
            var top = arcCalculator.getTopForItem(left, right)
            var bottom = top + height

            tempRect.set(left, top, right, bottom)

            Log.d("ArcLayoutManager", "calculateChildrenSite rect:$tempRect")
            allItemRect.put(i, tempRect)

            //总宽度在遍历中增加
            totalWidth += width
        }
    }

    override fun scrollHorizontallyBy(dx: Int, recycler: RecyclerView.Recycler, state: RecyclerView.State): Int {
        detachAndScrapAttachedViews(recycler)

        offsetChildrenHorizontal(-dx)
        recycleAndFillView(recycler, state)
        horizontalOffset += dx
        return dx
    }

    public fun getVerticalSpace(): Int {
        return height - paddingTop - paddingBottom
    }
}
