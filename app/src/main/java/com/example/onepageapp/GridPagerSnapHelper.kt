package com.example.onepageapp

import android.util.DisplayMetrics
import android.view.View
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SmoothScroller.ScrollVectorProvider
import androidx.recyclerview.widget.SnapHelper
import kotlin.math.abs


class GridPagerSnapHelper(private val rowCount: Int, private val columnCount: Int) : SnapHelper() {

    private var recyclerView: RecyclerView? = null
    private var verticalHelper: OrientationHelper? = null
    private var horizontalHelper: OrientationHelper? = null

    override fun calculateDistanceToFinalSnap(
        layoutManager: RecyclerView.LayoutManager,
        targetView: View
    ): IntArray? {
        val out = IntArray(2)
        if (layoutManager.canScrollHorizontally() && getHorizontalHelper(layoutManager) != null) {
            out[0] = distanceToStart(layoutManager, targetView, getHorizontalHelper(layoutManager)!!)
        } else {
            out[0]
        }

        if (layoutManager.canScrollVertically() && getVerticalHelper(layoutManager) != null) {
            out[1] = distanceToStart(layoutManager, targetView, getVerticalHelper(layoutManager)!!)
        } else {
            out[1]
        }

        return out
    }

    override fun findSnapView(layoutManager: RecyclerView.LayoutManager?): View? {
        return when {
            layoutManager?.canScrollVertically() == true && getVerticalHelper(layoutManager) != null -> {
                findStartSnapView(layoutManager, getVerticalHelper(layoutManager)!!)
            }
            layoutManager?.canScrollHorizontally() == true && getHorizontalHelper(layoutManager) != null -> {
                findStartSnapView(layoutManager, getHorizontalHelper(layoutManager)!!)
            }
            else -> null
        }
    }

    override fun findTargetSnapPosition(
        layoutManager: RecyclerView.LayoutManager?,
        velocityX: Int,
        velocityY: Int
    ): Int {
        val itemCount = layoutManager!!.itemCount
        return if (itemCount == 0) {
            -1
        } else {
            var mStartMostChildView: View? = null
            when {
                layoutManager.canScrollVertically() && getVerticalHelper(layoutManager) != null -> {
                    mStartMostChildView =
                        this.findStartView(layoutManager, getVerticalHelper(layoutManager)!!)
                }
                layoutManager.canScrollHorizontally() && getHorizontalHelper(layoutManager) != null -> {
                    mStartMostChildView =
                        this.findStartView(layoutManager, getHorizontalHelper(layoutManager)!!)
                }
            }

            if (mStartMostChildView == null) {
                -1
            } else {
                val centerPosition = layoutManager.getPosition(mStartMostChildView)
                if (centerPosition == -1) {
                    -1
                } else {

                    val pagerIndex: Int = centerPosition / (rowCount * columnCount)

                    val forwardDirection = if (layoutManager.canScrollHorizontally()) {
                        velocityX > 0
                    } else {
                        velocityY > 0
                    }

                    var reverseLayout = false
                    if (layoutManager is ScrollVectorProvider) {
                        val vectorProvider = layoutManager as ScrollVectorProvider
                        val vectorForEnd =
                            vectorProvider.computeScrollVectorForPosition(itemCount - 1)
                        if (vectorForEnd != null) {
                            reverseLayout = vectorForEnd.x < 0.0f || vectorForEnd.y < 0.0f
                        }
                    }
                    val targetPosition = if (reverseLayout) {
                        if (forwardDirection) (pagerIndex - 1) * (rowCount * columnCount) else pagerIndex * (rowCount * columnCount)
                    } else {
                        if (forwardDirection) (pagerIndex + 1) * (rowCount * columnCount) else pagerIndex * (rowCount * columnCount)
                    }
                    targetPosition
                }
            }
        }

    }

    override fun attachToRecyclerView(recyclerView: RecyclerView?) {
        super.attachToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    override fun createScroller(layoutManager: RecyclerView.LayoutManager?): RecyclerView.SmoothScroller? {
        return if (layoutManager !is ScrollVectorProvider) {
            null
        } else {
            object : LinearSmoothScroller(recyclerView?.context) {
                override fun onTargetFound(
                    targetView: View,
                    state: RecyclerView.State,
                    action: Action
                ) {
                    val snapDistances = calculateDistanceToFinalSnap(
                        recyclerView!!.layoutManager!!,
                        targetView
                    )
                    val dx = snapDistances!![0]
                    val dy = snapDistances[1]
                    val time = calculateTimeForDeceleration(
                        abs(dx).coerceAtLeast(abs(dy))
                    )
                    if (time > 0) {
                        action.update(dx, dy, time, mDecelerateInterpolator)
                    }
                }

                override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
                    return 100.0f / displayMetrics.densityDpi.toFloat()
                }

                override fun calculateTimeForScrolling(dx: Int): Int {
                    return 100.coerceAtMost(super.calculateTimeForScrolling(dx))
                }
            }
        }
    }

    private fun distanceToStart(
        layoutManager: RecyclerView.LayoutManager,
        targetView: View,
        helper: OrientationHelper
    ): Int {
        val childStart = helper.getDecoratedStart(targetView)
        val containerStart = if (layoutManager.clipToPadding) helper.startAfterPadding else 0
        return childStart - containerStart
    }

    private fun findStartSnapView(
        layoutManager: RecyclerView.LayoutManager,
        helper: OrientationHelper
    ): View? {
        val childCount = layoutManager.childCount
        return if (childCount == 0) {
            null
        } else {
            var closestChild: View? = null
            val start = if (layoutManager.clipToPadding) {
                helper.startAfterPadding
            } else {
                0
            }
            var absClosest = 2147483647
            for (i in 0 until childCount) {
                val child = layoutManager.getChildAt(i)
                val childStart = helper.getDecoratedStart(child)
                val absDistance = Math.abs(childStart - start)
                if (absDistance < absClosest) {
                    absClosest = absDistance
                    closestChild = child
                }
            }
            closestChild
        }
    }

    private fun findStartView(
        layoutManager: RecyclerView.LayoutManager,
        helper: OrientationHelper
    ): View? {
        val childCount = layoutManager.childCount
        if (childCount == 0) {
            return null
        } else {
            var closestChild: View? = null
            var startTest = 2147483647

            for (i in 0 until childCount) {
                val child = layoutManager.getChildAt(i);
                val childStart = helper.getDecoratedStart(child);
                if (childStart < startTest) {
                    startTest = childStart;
                    closestChild = child;
                }
            }

            return closestChild;
        }

    }

    private fun getVerticalHelper(layoutManager: RecyclerView.LayoutManager): OrientationHelper? {
        if (this.verticalHelper == null || this.verticalHelper?.layoutManager != layoutManager) {
            this.verticalHelper = OrientationHelper.createVerticalHelper(layoutManager)
        }
        return this.verticalHelper
    }

    private fun getHorizontalHelper(layoutManager: RecyclerView.LayoutManager): OrientationHelper? {
        if (this.horizontalHelper == null || this.horizontalHelper?.layoutManager != layoutManager) {
            this.horizontalHelper = OrientationHelper.createHorizontalHelper(layoutManager)
        }
        return this.horizontalHelper
    }
}