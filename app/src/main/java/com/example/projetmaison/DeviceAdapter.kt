package com.example.projetmaison

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DeviceAdapter(
    private val devices: List<Device>,
    private val onDeviceClick: (Device) -> Unit
) : RecyclerView.Adapter<DeviceAdapter.DeviceVH>() {

    class DeviceVH(v: View) : RecyclerView.ViewHolder(v) {
        val txtDeviceId: TextView = v.findViewById(R.id.txtDeviceId)
        val txtDeviceType: TextView = v.findViewById(R.id.txtDeviceType)
        val txtDeviceState: TextView = v.findViewById(R.id.txtDeviceState)
        val txtDeviceCommands: TextView = v.findViewById(R.id.txtDeviceCommands)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceVH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_device, parent, false)
        return DeviceVH(v)
    }

    override fun onBindViewHolder(holder: DeviceVH, position: Int) {
        val d = devices[position]

        holder.txtDeviceId.text = d.id
        holder.txtDeviceType.text = "Type: ${d.type}"

        holder.txtDeviceState.text = when {
            d.opening != null -> "Opening: ${d.opening}"
            d.power != null -> "Power: ${d.power}"
            else -> "État: n/a"
        }

        holder.txtDeviceCommands.text =
            if (d.availableCommands.isEmpty()) "Commands: (aucune)"
            else "Commands: ${d.availableCommands.joinToString(", ")}"

        holder.itemView.setOnClickListener { onDeviceClick(d) }
    }

    override fun getItemCount(): Int = devices.size
}