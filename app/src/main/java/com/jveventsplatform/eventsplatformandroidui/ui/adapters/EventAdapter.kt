package com.jveventsplatform.eventsplatformandroidui.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jveventsplatform.eventsplatformandroidui.R
import com.jveventsplatform.eventsplatformandroidui.model.Event

class EventAdapter(private var eventList: List<Event>) :
    RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    // ViewHolder: Defines how a single item looks
    class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val eventTitle: TextView = itemView.findViewById(R.id.eventTitle)
        val eventDate: TextView = itemView.findViewById(R.id.eventDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_event, parent, false)
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = eventList[position]
        holder.eventTitle.text = event.title
        holder.eventDate.text = event.eventDate
    }

    override fun getItemCount(): Int = eventList.size

    // Function to update the list when filtering
    fun updateList(newList: List<Event>) {
        eventList = newList
        notifyDataSetChanged()
    }
}
