package com.example.recyclerswipesdemo

import android.annotation.SuppressLint
import android.graphics.Canvas
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.item.view.*
import kotlin.math.absoluteValue

class Template(
    var name: String
)

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val controller1 = SwipeControllerR().apply {
            buttonRightText = "Delete"
        }
        val adapter1 = TemplateAdapterR(
            (1..32).map {
                Template("List 1 - Item "+it.toString())
            }.toMutableList()
            ,controller1
        )
        list1.adapter = adapter1
        list1.layoutManager = LinearLayoutManager(this)
        ItemTouchHelper(controller1).attachToRecyclerView(list1)


        val controller2 = SwipeControllerLR().apply {
            buttonLeftText = "Edit"
            buttonRightText = "Delete"
        }
        val adapter2 = TemplateAdapterLR(
            (1..32).map {
                Template("List 2 - Item "+it.toString())
            }.toMutableList()
            ,controller2
        )
        list2.adapter = adapter2
        list2.layoutManager = LinearLayoutManager(this)
        ItemTouchHelper(controller2).attachToRecyclerView(list2)

        val controller3 = SwipeControllerRR().apply {
            buttonLeftText = "Edit"
            buttonRightText = "Delete"
        }
        val adapter3 = TemplateAdapterRR(
            (1..32).map {
                Template("List 3 - Item "+it.toString())
            }.toMutableList()
            ,controller3
        )
        list3.adapter = adapter3
        list3.layoutManager = LinearLayoutManager(this)
        ItemTouchHelper(controller3).attachToRecyclerView(list3)
    }
}


///////////////////////////////// No classification: ///////////////////////////////////////////////
class TemplateAdapter(var items: MutableList<Template>, val controller: SwipeController)
    : RecyclerView.Adapter<TemplateAdapter.ViewHolder>(){
    override fun getItemCount() = items.size
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item, parent, false))
    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(items[position], position)
    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view), SwipeController.ViewHolder {
        override val buttonLeft = view.buttonLeft
        override val buttonRight = view.buttonRight
        override val text = view.text
        fun bind(item: Template, position: Int) = with(itemView) {
            text.text = item.name
            view.setOnClickListener {
                Toast.makeText(context, item.name, Toast.LENGTH_SHORT).show()
            }
            buttonLeft.setOnClickListener {
                Toast.makeText(context, "Edit "+item.name, Toast.LENGTH_SHORT).show()
                item.name = item.name + " edited"
                notifyItemChanged(position)
                controller.reset()
            }
            buttonRight.setOnClickListener {
                Toast.makeText(context, item.name + " deleted", Toast.LENGTH_SHORT).show()
                items.removeAt(position)
                notifyItemRemoved(position)
                notifyItemRangeChanged(position, items.size-position)
                controller.reset()
            }
        }
    }
}


///////////////////////////////// No classification: ///////////////////////////////////////////////
class SwipeController : ItemTouchHelper.Callback() {
    var buttonWidth = 200
    var buttonLeftText = "Left"
    var buttonRightText = "Right"

    private var swipeBack = false
    private var buttonLState = View.GONE
    private var buttonRState = View.GONE
    private var recyclerView: RecyclerView? = null
    private var viewHolder: ViewHolder? = null

    interface ViewHolder {
        val buttonLeft: Button
        val buttonRight: Button
        val text: TextView
    }

    private lateinit var padding: Padding
    class Padding(val left: Int, val top: Int, val right: Int, val bottom: Int)

    fun reset() {
        swipeBack = false
        buttonLState = View.GONE
        buttonRState = View.GONE
        viewHolder?.let {
            it.buttonLeft.text = ""
            it.buttonRight.text = ""
            it.buttonLeft.layoutParams = it.buttonLeft.layoutParams.apply { width = 0 }
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
            swipeBack = buttonRState == View.VISIBLE || buttonLState == View.VISIBLE
            return 0
        }
        return super.convertToAbsoluteDirection(flags, layoutDirection)
    }

    override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                             dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            if (viewHolder is ViewHolder) {
                this.recyclerView = recyclerView
                this.viewHolder = viewHolder
                if (! ::padding.isInitialized)
                    padding = Padding(viewHolder.text.paddingLeft, viewHolder.text.paddingTop, viewHolder.text.paddingRight, viewHolder.text.paddingBottom)

                val newX = if (buttonRState==View.VISIBLE || buttonLState==View.VISIBLE) buttonWidth
                           else Math.min(dX.toInt().absoluteValue, buttonWidth)

                viewHolder.buttonRight.layoutParams =
                    viewHolder.buttonRight.layoutParams.apply { width = if (dX < 0 || buttonRState==View.VISIBLE) newX else 0 }
                viewHolder.buttonLeft.layoutParams =
                    viewHolder.buttonLeft.layoutParams.apply { width = if (dX > 0 || buttonLState==View.VISIBLE) newX else 0 }

                viewHolder.text.setPadding(
                    if (dX < 0 || buttonRState==View.VISIBLE) -newX else padding.left, padding.top,
                    if (dX > 0 || buttonLState==View.VISIBLE) -newX else padding.right, padding.bottom)

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