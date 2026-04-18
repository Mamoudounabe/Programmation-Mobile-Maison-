package com.example.projetmaison.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.projetmaison.R
import com.example.projetmaison.models.HouseUser

class HouseUserAdapter(
    private val users: List<HouseUser>
) : RecyclerView.Adapter<HouseUserAdapter.UserVH>() {

    class UserVH(v: View) : RecyclerView.ViewHolder(v) {
        val txtUserLogin: TextView = v.findViewById(R.id.txtUserLogin)
        val txtRole: TextView = v.findViewById(R.id.txtRole)
        val imgRole: ImageView = v.findViewById(R.id.imgRole)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserVH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_house_user, parent, false)
        return UserVH(v)
    }

    override fun onBindViewHolder(holder: UserVH, position: Int) {
        val u = users[position]
        val isOwner = u.owner == 1

        // Login en rouge
        holder.txtUserLogin.text = u.userLogin
        holder.txtUserLogin.setTextColor(Color.RED)

        // Role
        holder.txtRole.text = if (isOwner) "Propriétaire" else "Invité"

        // Icône + couleur selon rôle
        val colorRes = if (isOwner) android.R.color.holo_green_dark else android.R.color.holo_blue_dark
        val color = ContextCompat.getColor(holder.itemView.context, colorRes)

        holder.imgRole.setImageResource(
            if (isOwner) R.drawable.ic_person_24 else R.drawable.ic_star_24
        )
        holder.imgRole.imageTintList = ContextCompat.getColorStateList(holder.itemView.context, colorRes)

        holder.txtRole.setTextColor(color)
    }

    override fun getItemCount(): Int = users.size
}