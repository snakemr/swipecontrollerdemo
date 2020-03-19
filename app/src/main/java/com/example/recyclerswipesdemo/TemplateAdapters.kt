package com.example.recyclerswipesdemo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item.view.*

class TemplateAdapterR(var items: MutableList<Template>, val controller: SwipeControllerR)
    : RecyclerView.Adapter<TemplateAdapterR.ViewHolder>(){
    override fun getItemCount() = items.size
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_r, parent, false))
    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(items[position], position)
    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view), SwipeControllerR.ViewHolder {
        override val buttonRight = view.buttonRight
        override val text = view.text
        fun bind(item: Template, position: Int) = with(itemView) {
            text.text = item.name
            view.setOnClickListener {
                Toast.makeText(context, item.name, Toast.LENGTH_SHORT).show()
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

class TemplateAdapterLR(var items: MutableList<Template>, val controller: SwipeControllerLR)
    : RecyclerView.Adapter<TemplateAdapterLR.ViewHolder>(){
    override fun getItemCount() = items.size
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_lr, parent, false))
    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(items[position], position)
    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view), SwipeControllerLR.ViewHolder {
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

class TemplateAdapterRR(var items: MutableList<Template>, val controller: SwipeControllerRR)
    : RecyclerView.Adapter<TemplateAdapterRR.ViewHolder>(){
    override fun getItemCount() = items.size
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_rr, parent, false))
    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(items[position], position)
    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view), SwipeControllerLR.ViewHolder {
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