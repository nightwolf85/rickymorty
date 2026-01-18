package com.example.rickymorty

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class EpisodiosAdapter(
    private var listaEpisodios: List<Episodio>,
    private val onEpisodioClick: (Episodio) -> Unit
) : RecyclerView.Adapter<EpisodiosAdapter.MyViewHolder>() {

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombre: TextView = itemView.findViewById(R.id.nombreEpisodio)
        val codigo: TextView = itemView.findViewById(R.id.codigoEpisodio)
        val fecha: TextView = itemView.findViewById(R.id.fechaEpisodio)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_episodio, parent, false)
        return MyViewHolder(vista)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val episodioActual = listaEpisodios[position]

        holder.nombre.text = episodioActual.name
        holder.codigo.text = episodioActual.episode
        holder.fecha.text = episodioActual.airDate

        if (episodioActual.visto) {
            holder.itemView.setBackgroundColor(Color.parseColor("#E8F5E9"))
        } else {
            holder.itemView.setBackgroundColor(Color.WHITE)
        }

        holder.itemView.setOnClickListener {
            onEpisodioClick(episodioActual)
        }
    }

    override fun getItemCount(): Int = listaEpisodios.size

    fun actualizarDatos(nuevosEpisodios: List<Episodio>) {
        listaEpisodios = nuevosEpisodios
        notifyDataSetChanged()
    }
}