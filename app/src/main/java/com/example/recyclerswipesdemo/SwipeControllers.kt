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
    var buttonRightText = "Right"

    private var swipeBack = false
    protected var buttonRState = View.GONE
    private var recyclerView: RecyclerView? = null
    private var viewHolder: ViewHolder? = null

    interface ViewHolder {
        val buttonRight: Button
        val text: TextView
    }

    protected lateinit var padding: Padding
    class Padding(val left: Int, val top: Int, val right: Int, val bottom: Int)

    fun reset() {
        swipeBack = false
        resetButtons()
        viewHolder?.let {
            if (::padding.isInitialized)
                it.text.setPadding(padding.left, padding.top, padding.right, padding.bottom)
        }
        recyclerView?.let {
            it.setOnTouchListener { v, event -> false }
            setItemsClickable(it, true)
        }
    }

    protected open fun resetButtons() {
        buttonRState = View.GONE
        viewHolder?.let {
            it.buttonRight.text = ""
            it.buttonRight.layoutParams = it.buttonRight.layoutParams.apply { width = 0 }
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
            return 0
        }
        return super.convertToAbsoluteDirection(flags, layoutDirection)
    }

    open fun anyButtonVisible() = (buttonRState == View.VISIBLE)

    protected open fun setButtonsLayout(viewHolder: RecyclerView.ViewHolder, dX: Float, newX: Int) {
        if (viewHolder is ViewHolder)
            viewHolder.buttonRight.layoutParams =
                viewHolder.buttonRight.layoutParams.apply { width = if (dX < 0 || buttonRState== View.VISIBLE) newX else 0 }
    }

    protected open fun setPadding(viewHolder: RecyclerView.ViewHolder, dX: Float, newX: Int) {
        if (viewHolder is ViewHolder)
            viewHolder.text.setPadding(
                if (dX < 0 || buttonRState== View.VISIBLE) -newX else padding.left, padding.top, padding.right, padding.bottom)
    }

    protected open fun showButtonsIfNeed(viewHolder: RecyclerView.ViewHolder, dX: Float) {
        if (dX < -buttonWidth) {
            buttonRState = View.VISIBLE
            if (viewHolder is ViewHolder) viewHolder.buttonRight.text = buttonRightText
        }
    }

    protected open fun saveViewHolder(viewHolder: RecyclerView.ViewHolder) {
        if (viewHolder is ViewHolder) this.viewHolder = viewHolder
    }

    protected open fun doShift(viewHolder: RecyclerView.ViewHolder, dX: Float) {
        val newX = if (anyButtonVisible()) buttonWidth
                   else Math.min(dX.toInt().absoluteValue, buttonWidth)
        setButtonsLayout(viewHolder, dX, newX)
        setPadding(viewHolder, dX, newX)
    }

    override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                             dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            if (viewHolder is ViewHolder) {
                this.recyclerView = recyclerView
                saveViewHolder(viewHolder)

                if (! ::padding.isInitialized)
                    padding = Padding(viewHolder.text.paddingLeft, viewHolder.text.paddingTop, viewHolder.text.paddingRight, viewHolder.text.paddingBottom)

                doShift(viewHolder, dX)

                super.onChildDraw(c, recyclerView, viewHolder, 0f, dY, actionState, isCurrentlyActive)
            }

            if (!anyButtonVisible() && viewHolder is ViewHolder)
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
                showButtonsIfNeed(viewHolder, dX)

                if (anyButtonVisible()) {
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
                resetButtons()
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


open class SwipeControllerLR : SwipeControllerR() {
    var buttonLeftText = "Left"
    protected var buttonLState = View.GONE
    private var viewHolder: ViewHolder? = null

    interface ViewHolder: SwipeControllerR.ViewHolder {
        val buttonLeft: Button
    }

    override fun resetButtons() {
        buttonLState = View.GONE
        viewHolder?.let {
            it.buttonLeft.text = ""
            it.buttonLeft.layoutParams = it.buttonLeft.layoutParams.apply { width = 0 }
        }
        super.resetButtons()
    }

    override open fun saveViewHolder(viewHolder: RecyclerView.ViewHolder) {
        super.saveViewHolder(viewHolder)
        if (viewHolder is ViewHolder) this.viewHolder = viewHolder
    }

    override fun anyButtonVisible() = (buttonRState == View.VISIBLE || buttonLState == View.VISIBLE)

    override open fun setButtonsLayout(viewHolder: RecyclerView.ViewHolder, dX: Float, newX: Int) {
        super.setButtonsLayout(viewHolder, dX, newX)
        if (viewHolder is ViewHolder)
            viewHolder.buttonLeft.layoutParams =
                viewHolder.buttonLeft.layoutParams.apply { width = if (dX > 0 || buttonLState== View.VISIBLE) newX else 0 }
    }

    override open fun setPadding(viewHolder: RecyclerView.ViewHolder, dX: Float, newX: Int) {
        if (viewHolder is ViewHolder)
            viewHolder.text.setPadding(
                if (dX < 0 || buttonRState== View.VISIBLE) -newX else padding.left, padding.top,
                if (dX > 0 || buttonLState== View.VISIBLE) -newX else padding.right, padding.bottom)
    }

    override open fun showButtonsIfNeed(viewHolder: RecyclerView.ViewHolder, dX: Float) {
        if (dX < -buttonWidth) {
            buttonRState = View.VISIBLE
            if (viewHolder is ViewHolder) viewHolder.buttonRight.text = buttonRightText
        } else if (dX > buttonWidth) {
            buttonLState = View.VISIBLE
            if (viewHolder is ViewHolder) viewHolder.buttonLeft.text = buttonLeftText
        }
    }
}

class SwipeControllerRR : SwipeControllerLR() {

}