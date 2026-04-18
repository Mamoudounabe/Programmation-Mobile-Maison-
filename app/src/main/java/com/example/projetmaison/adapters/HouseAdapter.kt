package com.example.projetmaison.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.projetmaison.R
import com.example.projetmaison.models.HouseData

// Adaptateur pour afficher une liste de maisons dans une RecyclerView
class HouseAdapter(private val houses: List<HouseData>,
                   private val onClick: (HouseData)-> Unit) :
    RecyclerView.Adapter<HouseAdapter.HouseViewHolder>() {

    // ViewHolder pour chaque item de maison
    class HouseViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    // Création des items (ViewHolder)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HouseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_house, parent, false)
        return HouseViewHolder(view)
    }

    // Nombre d’éléments dans la liste
    override fun getItemCount(): Int {
        return houses.size
    }

    // Lie les données d'une maison à un ViewHolder
    override fun onBindViewHolder(holder: HouseViewHolder, position: Int) {
        val house = houses[position]

        val txtId = holder.view.findViewById<TextView>(R.id.txtHouseId) // Affiche l'ID de la maison
        val txtDevices = holder.view.findViewById<TextView>(R.id.txtDevices) // Affiche le nombre d'appareils
        val imgRole = holder.view.findViewById<ImageView>(R.id.imgRole) // Icône du rôle (propriétaire ou invité)
        val btnRefresh = holder.view.findViewById<Button>(R.id.btnRefresh) // Bouton de rafraîchissement
        val btnManage = holder.view.findViewById<Button>(R.id.btnManage) // Bouton de gestion

        txtId.text = "Maison ${house.houseId}"
        txtDevices.text = "${house.deviceCount} périphériques connectés."
        imgRole.setImageResource(if (house.owner) R.drawable.ic_owner else R.drawable.ic_guest)

        btnRefresh.setOnClickListener {
            // Action de rafraîchissement (à adapter selon ton besoin)
            Toast.makeText(holder.view.context, "Rafraîchissement...", Toast.LENGTH_SHORT).show()
            // Tu peux rappeler une API ou autre ici
        }

        btnManage.setOnClickListener {
            if (house.owner) {
                onClick(house) // Ouvre HouseAccesActivity
            } else {
                Toast.makeText(holder.view.context, "Accès limité", Toast.LENGTH_SHORT).show()
            }
        }
    }





}