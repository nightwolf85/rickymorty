package com.example.rickymorty

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.rickymorty.Personaje

class FragmentDetalles : Fragment() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_detalles, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val nombre = arguments?.getString("clave_nombre")
        val codigo = arguments?.getString("clave_codigo")
        val fecha = arguments?.getString("clave_fecha")

        val userId = auth.currentUser?.uid


        val nombreEpisodio = view.findViewById<TextView>(R.id.tituloDetalles)
        val codigoEpisodio = view.findViewById<TextView>(R.id.codigoEpisodio)
        val fechaEpisodio = view.findViewById<TextView>(R.id.fechaEpisodio)
        val switchVisto = view.findViewById<Switch>(R.id.switchVisto)
        nombreEpisodio.text = nombre
        codigoEpisodio.text = codigo
        fechaEpisodio.text = fecha

        if (userId != null) {
            db.collection("usuarios").document(userId)
                .collection("vistos").document(codigo.toString())
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        switchVisto.isChecked = true
                    } else {
                        switchVisto.isChecked = false
                    }
                }
        }
        switchVisto.setOnCheckedChangeListener { _, isChecked ->
            if (userId != null) {
                if (isChecked) {
                    val datos = hashMapOf(
                        "name" to (nombre ?: "Sin nombre"),
                        "episode" to (codigo ?: "S00E00"),
                        "air_date" to (fecha ?: "Sin fecha"),
                        "characters" to listOf("Personaje 1", "Personaje 2"),
                        "viewed" to true
                    )

                    db.collection("usuarios").document(userId)
                        .collection("vistos").document(codigo.toString())
                        .set(datos)
                        .addOnSuccessListener {
                            Toast.makeText(context, "Guardado como visto", Toast.LENGTH_SHORT)
                                .show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, "Error al guardar", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    db.collection("usuarios").document(userId)
                        .collection("vistos").document(codigo.toString())
                        .delete()
                        .addOnSuccessListener {
                            Toast.makeText(context, "Marcado como no visto", Toast.LENGTH_SHORT)
                                .show()
                        }
                }
            } else {
                Toast.makeText(context, "Error: Usuario no identificado", Toast.LENGTH_SHORT).show()
            }
        }
        val urlsPersonajes = arguments?.getStringArrayList("clave_personajes")

        if (urlsPersonajes != null && urlsPersonajes.isNotEmpty()) {
            val ids = urlsPersonajes.map { it.substringAfterLast("/") }.joinToString(",")
            cargarPersonajes(ids, view)
        }
    }
    private fun cargarPersonajes(ids: String, view: View) {
        val rvPersonajes = view.findViewById<RecyclerView>(R.id.rvPersonajes)
        rvPersonajes.layoutManager = GridLayoutManager(context, 3)

        RetrofitClient.instance.obtenerPersonajes(ids).enqueue(object : Callback<List<Personaje>> {
            override fun onResponse(call: Call<List<Personaje>>, response: Response<List<Personaje>>) {
                if (response.isSuccessful) {
                    val personajes = response.body() ?: emptyList()
                    rvPersonajes.adapter = PersonajesAdapter(personajes)
                }
            }
            override fun onFailure(call: Call<List<Personaje>>, t: Throwable) {
            }
        })
    }
}