package com.example.rickymorty

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

//Este adapter gestiona como se mostrarán los personajes en el recyclerview.
class PersonajesAdapter(private val lista: List<Personaje>) :
    RecyclerView.Adapter<PersonajesAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val img: ImageView = view.findViewById(R.id.imgPersonaje)
        val nombre: TextView = view.findViewById(R.id.tvNombrePersonaje)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_personaje, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val p = lista[position]
        holder.nombre.text = p.name
        Picasso.get().load(p.image).into(holder.img)
    }

    override fun getItemCount() = lista.size
}