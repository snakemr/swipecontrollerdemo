package com.example.recyclerswipesdemo

import android.annotation.SuppressLint
import android.graphics.Canvas
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.core.view.children
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.absoluteValue

open class SwipeControllerR : ItemTouchHelper.Callback() {
    var buttonWidth = 200
    //var buttonLeftText = "Left"
    var buttonRightText = "Right"

    private var swipeBack = false
    //private var buttonLState = View.GONE
    protected var buttonRState = View.GONE
    private var recyclerView: RecyclerView? = null
    private var viewHolder: ViewHolder? = null

    interface ViewHolder {
        //val buttonLeft: Button
        val buttonRight: Button
        val text: TextView
    }

    private lateinit var padding: Padding
    class Padding(val left: Int, val top: Int, val right: Int, val bottom: Int)

    open fun reset() {
        swipeBack = false
        //buttonLState = View.GONE
        buttonRState = View.GONE
        viewHolder?.let {
            //it.buttonLeft.text = ""
            it.buttonRight.text = ""
            //it.buttonLeft.layoutParams = it.buttonLeft.layoutParams.apply { width = 0 }
            it.buttonRight.layoutParams = it.buttonRight.layoutParams.apply { width = 0 }
            if (::padding.isInitialized)
                it.text.setPadding(padding.left, padding.top, padding.right, padding.bottom)
        }
        recyclerView?.let {
            it.setOnTouchListener { v, event -> false }
            setItemsClickable(it, true)
        }
    }

    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder)
            = makeMovementFlags(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT)

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder)
            = false

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}

    override fun convertToAbsoluteDirection(flags: Int, layoutDirection: Int): Int {
        if (swipeBack) {
            swipeBack = anyButtonVisible()
            //buttonRState == View.VISIBLE || buttonLState == View.VISIBLE
            return 0
        }
        return super.convertToAbsoluteDirection(flags, layoutDirection)
    }

    open fun anyButtonVisible() = (buttonRState == View.VISIBLE)

    override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                             dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            if (viewHolder is ViewHolder) {
                this.recyclerView = recyclerView
                this.viewHolder = viewHolder
                if (! ::padding.isInitialized)
                    padding = Padding(viewHolder.text.paddingLeft, viewHolder.text.paddingTop, viewHolder.text.paddingRight, viewHolder.text.paddingBottom)

                val newX = if (buttonRState== View.VISIBLE || buttonLState== View.VISIBLE) buttonWidth
                else Math.min(dX.toInt().absoluteValue, buttonWidth)

                viewHolder.buttonRight.layoutParams =
                    viewHolder.buttonRight.layoutParams.apply { width = if (dX < 0 || buttonRState== View.VISIBLE) newX else 0 }
                viewHolder.buttonLeft.layoutParams =
                    viewHolder.buttonLeft.layoutParams.apply { width = if (dX > 0 || buttonLState== View.VISIBLE) newX else 0 }

                viewHolder.text.setPadding(
                    if (dX < 0 || buttonRState== View.VISIBLE) -newX else padding.left, padding.top,
                    if (dX > 0 || buttonLState== View.VISIBLE) -newX else padding.right, padding.bottom)

                super.onChildDraw(c, recyclerView, viewHolder, 0f, dY, actionState, isCurrentlyActive)
            }

            if (buttonLState == View.GONE && buttonRState == View.GONE && viewHolder is ViewHolder)
                setTouchListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setTouchListener(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                                 dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
        recyclerView.setOnTouchListener { v, event ->
            swipeBack =
                event.action == MotionEvent.ACTION_CANCEL || event.action == MotionEvent.ACTION_UP

            if (swipeBack) {
                if (dX < -buttonWidth) {
                    buttonRState = View.VISIBLE
                    if (viewHolder is ViewHolder) viewHolder.buttonRight.text = buttonRightText
                } else if (dX > buttonWidth) {
                    buttonLState = View.VISIBLE
                    if (viewHolder is ViewHolder) viewHolder.buttonLeft.text = buttonLeftText
                }

                if (buttonLState == View.VISIBLE || buttonRState == View.VISIBLE) {
                    setTouchDownListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    setItemsClickable(recyclerView, false)
                }
            }
            false
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setTouchDownListener(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                                     dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
        recyclerView.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                setTouchUpListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
            false
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setTouchUpListener(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                                   dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
        recyclerView.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                recyclerView.setOnTouchListener { v2, event2 -> false }
                setItemsClickable(recyclerView, true)
                swipeBack = false
                buttonLState = View.GONE
                buttonRState = View.GONE
                if (viewHolder is ViewHolder) {
                    viewHolder.buttonRight.text = ""
                    viewHolder.buttonLeft.text = ""
                }
                onChildDraw(c, recyclerView, viewHolder,0f, dY, actionState, isCurrentlyActive)
            }
            false
        }
    }

    private fun setItemsClickable(recyclerView: RecyclerView, isClickable: Boolean) {
        recyclerView.children.forEach {
            it.isClickable = isClickable
        }
    }
}


class SwipeControlerLR : SwipeControllerR() {
    var buttonLeftText = "Left"
    private var buttonLState = View.GONE
    private var viewHolder: ViewHolder? = null

    interface ViewHolder: SwipeControllerR.ViewHolder {
        val buttonLeft: Button
    }

    override fun reset() {
        buttonLState = View.GONE
        viewHolder?.let {
            it.buttonLeft.text = ""
            it.buttonLeft.layoutParams = it.buttonLeft.layoutParams.apply { width = 0 }
        }
        super.reset()
    }

    override fun anyButtonVisible() = (buttonRState == View.VISIBLE || buttonLState == View.VISIBLE)


}