package com.example.rickymorty

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class EpisodiosAdapter(
    //Se definen las variables a usar en esta clase.
    private var listaEpisodios: List<Episodio>,
    private val onEpisodioClick: (Episodio) -> Unit,
    private val onModoSeleccionChange: (Boolean) -> Unit
) : RecyclerView.Adapter<EpisodiosAdapter.MyViewHolder>() {

    var esModoSeleccion = false

    //Se indica lo que mostrará el RecyclerView
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombre: TextView = itemView.findViewById(R.id.nombreEpisodio)
        val codigo: TextView = itemView.findViewById(R.id.codigoEpisodio)
        val fecha: TextView = itemView.findViewById(R.id.fechaEpisodio)
        val checkbox: CheckBox = itemView.findViewById(R.id.cbSeleccion)
    }

    //Se indica que debe usar el layout de item_episodio
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_episodio, parent, false)
        return MyViewHolder(vista)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        //Se usa una variable para tomar el episodio actual
        val episodioActual = listaEpisodios[position]

        //Se definen el nombre, código y fecha a mostrar del episodio actual.
        holder.nombre.text = episodioActual.name
        holder.codigo.text = episodioActual.episode
        holder.fecha.text = episodioActual.airDate

        //Si se ha marado como visto, se cambia el color de fondo.
        if (episodioActual.visto) {
            holder.itemView.setBackgroundColor(Color.parseColor("#3300FF9C"))
        } else {
            holder.itemView.setBackgroundColor(Color.TRANSPARENT)
        }

        //Si se deja pulsado varios segundos se activa este modo para hacer la selección múltiple de episodios.
        if (esModoSeleccion) {
            holder.checkbox.visibility = View.VISIBLE
            holder.checkbox.isChecked = episodioActual.seleccionado
        } else {
            holder.checkbox.visibility = View.GONE
            episodioActual.seleccionado = false
        }

        //Si se hace un click normal en función de si está activado o no el modo selección, se permite marcar o desmarcar el checkbox (modo activado)
        // o se entra a la pantalla de los datos del episodio (modo desactivado)

        holder.itemView.setOnClickListener {
            if (esModoSeleccion) {
                episodioActual.seleccionado = !episodioActual.seleccionado
                notifyItemChanged(position)
            } else {
                onEpisodioClick(episodioActual)
            }
        }

        //En este caso al hacer click durante varios segundos, se activa el modo selección múltiple, se marca el primero que se pulsa y muestra los checkbox
        //y también muestra el botón de guardar.
        holder.itemView.setOnLongClickListener {
            if (!esModoSeleccion) {
                esModoSeleccion = true
                episodioActual.seleccionado = true
                onModoSeleccionChange(true)
                notifyDataSetChanged()
                true
            } else {
                false
            }
        }
    }

    //Cuenta los episodios que hay en la lista
    override fun getItemCount(): Int = listaEpisodios.size

    //Actualiza la lista de episodios
    fun actualizarDatos(nuevosEpisodios: List<Episodio>) {
        listaEpisodios = nuevosEpisodios
        notifyDataSetChanged()
    }

    //Obtiene la lista de los que se han seleccionado.
    fun obtenerSeleccionados(): List<Episodio> {
        return listaEpisodios.filter { it.seleccionado }
    }

    //Sale del modo selección múltiple.
    fun cancelarSeleccion() {
        esModoSeleccion = false
        listaEpisodios.forEach { it.seleccionado = false }
        notifyDataSetChanged()
        onModoSeleccionChange(false)
    }
}