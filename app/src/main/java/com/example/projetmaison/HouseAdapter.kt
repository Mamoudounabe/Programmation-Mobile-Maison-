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
        val txtOwner = holder.view.findViewById<TextView>(R.id.txtOwner)

        txtId.text = "Maison ${house.houseId}"
        // txtOwner.text = if (house.owner) "Propriétaire" else "Invité"
        // txt.text = "Maison ${house.houseId} - Owner: ${house.owner}"

        /* holder.view.setOnClickListener {

            onClick(house)
        }*/


        txtOwner.text = if (house.owner)
            "👑 Propriétaire"
        else
            "👤 Invité"


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

    override fun onBindViewHolder(holder: HouseViewHolder, position: Int) {
        val house = houses[position]

        val txtId = holder.view.findViewById<TextView>(R.id.txtHouseId)
        val txtOwner = holder.view.findViewById<TextView>(R.id.txtOwner)
        val imgRole = holder.view.findViewById<ImageView>(R.id.imgRole)
        val btnShare = holder.view.findViewById<Button>(R.id.btnShare)

        txtId.text = "Maison ${house.houseId}"

        // reset (IMPORTANT RecyclerView)
        btnShare.setOnClickListener(null)

        if (house.owner) {
            txtOwner.text = "Propriétaire"
            imgRole.setImageResource(R.drawable.ic_owner)
            btnShare.visibility = View.VISIBLE

            btnShare.setOnClickListener {
                onClick(house)
            }

        } else {
            txtOwner.text = "Invité"
            imgRole.setImageResource(R.drawable.ic_guest)
            btnShare.visibility = View.GONE
        }

        holder.view.setOnClickListener {
            if (house.owner) {
                onClick(house)
            } else {
                Toast.makeText(holder.view.context, "Accès limité", Toast.LENGTH_SHORT).show()
            }
        }
    }






}
