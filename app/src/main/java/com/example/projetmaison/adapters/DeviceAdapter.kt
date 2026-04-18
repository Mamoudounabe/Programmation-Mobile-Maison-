package com.example.projetmaison.adapters

import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.projetmaison.R
import com.example.projetmaison.models.Device

// Adaptateur pour afficher une liste d'appareils dans une RecyclerView
class DeviceAdapter(
    private val devices: List<Device>,
    private val onDeviceClick: (Device) -> Unit
) : RecyclerView.Adapter<DeviceAdapter.DeviceVH>() {

    // ViewHolder pour chaque item d'appareil
    class DeviceVH(v: View) : RecyclerView.ViewHolder(v) {
        val txtDeviceId: TextView = v.findViewById(R.id.txtDeviceId) // Affiche l'ID de l'appareil
        val txtDeviceType: TextView = v.findViewById(R.id.txtDeviceType) // Affiche le type
        val txtDeviceState: TextView = v.findViewById(R.id.txtDeviceState) // Affiche l'état
        val layoutDeviceCommands: LinearLayout = v.findViewById(R.id.layoutDeviceCommands) // Conteneur pour les boutons de commande
    }

    // Crée un nouveau ViewHolder pour un item
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceVH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_device, parent, false)
        return DeviceVH(v)
    }

    // Lie les données d'un appareil à un ViewHolder
    override fun onBindViewHolder(holder: DeviceVH, position: Int) {
        val d = devices[position]

        holder.txtDeviceId.text = d.id
        holder.txtDeviceType.text = "Type: ${d.type}"

        // Affichage de l'état selon le type d'appareil
        holder.txtDeviceState.text = when {
            d.opening != null -> "Opening: ${d.opening}"
            d.power != null -> "Power: ${d.power}"
            else -> "État: n/a"
        }

        // Générer dynamiquement les boutons de commandes groupés par catégorie, chaque ligne centrée
        holder.layoutDeviceCommands.removeAllViews()
        if (d.availableCommands.isEmpty()) {
            val tv = TextView(holder.layoutDeviceCommands.context)
            tv.text = "Aucune commande"
            holder.layoutDeviceCommands.addView(tv)
        } else {
            // Exemple de regroupement par catégorie (à adapter selon ta logique réelle)
            val categories = listOf(
                listOf("garage", "volet", "lumiere"),
                listOf("Ouvrir étage 1", "Fermer étage 1"),
                listOf("Ouvrir étage 2", "Fermer étage 2"),
                listOf("Mode Jour", "Mode Nuit"),
                listOf("Mode fun")
            )
            val cmds = d.availableCommands.toSet()
            for (cat in categories) {
                val filtered = cat.filter { cmds.contains(it) }
                if (filtered.isNotEmpty()) {
                    val row = LinearLayout(holder.layoutDeviceCommands.context)
                    row.orientation = LinearLayout.HORIZONTAL
                    row.gravity = Gravity.CENTER
                    for (cmd in filtered) {
                        val btn = Button(holder.layoutDeviceCommands.context)
                        btn.text = cmd
                        btn.setTextColor(Color.WHITE)
                        btn.textSize = 14f
                        btn.setBackgroundResource(R.drawable.rounded_red_button)
                        val params = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                        params.setMargins(12, 12, 12, 12)
                        btn.layoutParams = params
                        btn.setOnClickListener { onDeviceClick(d.copy(availableCommands = listOf(cmd))) }
                        row.addView(btn)
                    }
                    holder.layoutDeviceCommands.addView(row)
                }
            }
            // Pour les commandes non catégorisées
            val allCatCmds = categories.flatten().toSet()
            val rest = cmds.filter { !allCatCmds.contains(it) }
            if (rest.isNotEmpty()) {
                val row = LinearLayout(holder.layoutDeviceCommands.context)
                row.orientation = LinearLayout.HORIZONTAL
                row.gravity = Gravity.CENTER
                for (cmd in rest) {
                    val btn = Button(holder.layoutDeviceCommands.context)
                    btn.text = cmd
                    btn.setTextColor(Color.WHITE)
                    btn.textSize = 14f
                    btn.setBackgroundResource(R.drawable.rounded_red_button)
                    val params = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    params.setMargins(12, 12, 12, 12)
                    btn.layoutParams = params
                    btn.setOnClickListener { onDeviceClick(d.copy(availableCommands = listOf(cmd))) }
                    row.addView(btn)
                }
                holder.layoutDeviceCommands.addView(row)
            }
        }
    }

    override fun getItemCount(): Int = devices.size
}