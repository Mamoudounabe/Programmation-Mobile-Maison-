package com.example.projetmaison

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class HouseAdapter(private val houses: List<HouseData>,
    private val onClick: (HouseData)-> Unit) :
    RecyclerView.Adapter<HouseAdapter.HouseViewHolder>() {

    //  ViewHolder
    class HouseViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    //  Création des items
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HouseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_house, parent, false)
        return HouseViewHolder(view)
    }

    //  Nombre d’éléments
    override fun getItemCount(): Int {
        return houses.size
    }

    //  Remplir chaque ligne
   /* override fun onBindViewHolder(holder: HouseViewHolder, position: Int) {
        val house = houses[position]

        val txtId = holder.view.findViewById<TextView>(R.id.txtHouseId)

        txtId.text = "Maison ${house.houseId}"
        // txt.text = "Maison ${house.houseId} - Owner: ${house.owner}"

        /* holder.view.setOnClickListener {

            onClick(house)
        }*/




        //  GESTION DU CLIC
        // désactiver clic ou bouton
        /*    if (!house.owner) {
            holder.view.isEnabled = false
            holder.view.alpha = 0.5f
        } else {
            holder.view.isEnabled = true
            holder.view.alpha = 1f

            holder.view.setOnClickListener {
                onClick(house)
            }
        }*/


        holder.view.setOnClickListener {
            if (!house.owner) {
                Toast.makeText(
                    holder.view.context,
                    "Vous n'êtes pas propriétaire",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                onClick(house)
            }


        }
    }*/

    /*override fun onBindViewHolder(holder: HouseViewHolder, position: Int) {
        val house = houses[position]

        val txtId = holder.view.findViewById<TextView>(R.id.txtHouseId)
        val imgRole = holder.view.findViewById<ImageView>(R.id.imgRole)
        val btnShare = holder.view.findViewById<Button>(R.id.btnShare)

        txtId.text = "Maison ${house.houseId}"

        // reset (IMPORTANT RecyclerView)
        btnShare.setOnClickListener(null)


        holder.view.setOnClickListener {
            if (house.owner) {
                onClick(house)
            } else {
                Toast.makeText(holder.view.context, "Accès limité", Toast.LENGTH_SHORT).show()
            }
        }
    }
*/


    override fun onBindViewHolder(holder: HouseViewHolder, position: Int) {
        val house = houses[position]

        val txtId = holder.view.findViewById<TextView>(R.id.txtHouseId)
        val txtDevices = holder.view.findViewById<TextView>(R.id.txtDevices)
        val imgRole = holder.view.findViewById<ImageView>(R.id.imgRole)
        val btnRefresh = holder.view.findViewById<Button>(R.id.btnRefresh)
        val btnManage = holder.view.findViewById<Button>(R.id.btnManage)

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
