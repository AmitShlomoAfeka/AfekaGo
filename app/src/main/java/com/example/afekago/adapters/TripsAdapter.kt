package com.example.afekago.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.afekago.NavigationActivity
import com.example.afekago.R
import com.example.afekago.models.Trip

class TripsAdapter(private val trips: List<Trip>, private val passengerId: String) :
    RecyclerView.Adapter<TripsAdapter.TripViewHolder>() {

    class TripViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dayTextView: TextView = itemView.findViewById(R.id.tvDay)
        val timeTextView: TextView = itemView.findViewById(R.id.tvTime)
        val typeTextView: TextView = itemView.findViewById(R.id.tvType)
        val btnRequest: Button = itemView.findViewById(R.id.btnRequestRide)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_trip, parent, false)
        return TripViewHolder(view)
    }

    override fun onBindViewHolder(holder: TripViewHolder, position: Int) {
        val trip = trips[position]
        holder.dayTextView.text = trip.day
        holder.timeTextView.text = trip.time
        holder.typeTextView.text = trip.type

        holder.btnRequest.setOnClickListener {
            if (trip.driverId.isNotEmpty()) {
                val intent = Intent(holder.itemView.context, NavigationActivity::class.java)
                intent.putExtra("driverId", trip.driverId)
                intent.putExtra("passengerId", passengerId)
                holder.itemView.context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int = trips.size
}
