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

//Este fragment se usa para mostrar los detalles de cada episodio, nombre, código, fecha de emisión y lista de personaje.
//Además, un check para marcarlo como visto.
//También se envía la información a la base de datos de Firebase para almacenar los marcados como vistos.
class FragmentDetalles : Fragment() {

    //Se definen las variables para obtener la base de datos y los datos de autenticación
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    //Se indica que debe cargar el layout de fragment_detalles
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_detalles, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Se obtienen el nombre, código, fecha y lista de personajes del episodio y el ID de usuario.
        val nombre = arguments?.getString("clave_nombre")
        val codigo = arguments?.getString("clave_codigo")
        val fecha = arguments?.getString("clave_fecha")
        val listaPersonajes = arguments?.getStringArrayList("clave_personajes") ?: emptyList<String>()
        val userId = auth.currentUser?.uid

        //Se definen variables para indicar donde irá cada dato dentro del layout y se rellenan con los datos obtenidos anteriormente.
        val nombreEpisodio = view.findViewById<TextView>(R.id.tituloDetalles)
        val codigoEpisodio = view.findViewById<TextView>(R.id.codigoEpisodio)
        val fechaEpisodio = view.findViewById<TextView>(R.id.fechaEpisodio)
        val switchVisto = view.findViewById<Switch>(R.id.switchVisto)
        nombreEpisodio.text = nombre
        codigoEpisodio.text = codigo
        fechaEpisodio.text = fecha

        //Si el usuario no está vacío se indica en la tabla de la base de datos donde se guardarán o quitarán los datos.
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
        //Si se marca el check de visto, se almacena la información del episodio marcado como visto en la tabla definida en el paso anterior.
        switchVisto.setOnCheckedChangeListener { _, isChecked ->
            if (userId != null) {
                if (isChecked) {
                    val datos = hashMapOf(
                        "name" to (nombre ?: nombre),
                        "episode" to (codigo ?: codigo),
                        "air_date" to (fecha ?: fecha),
                        "characters" to listaPersonajes,
                        "viewed" to true
                    )

                    //Se muestra el aviso de marcado como visto o de error al guardar, si se produce.
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
                    //Se muestra mensaje de marcado como no visto.
                    db.collection("usuarios").document(userId)
                        .collection("vistos").document(codigo.toString())
                        .delete()
                        .addOnSuccessListener {
                            Toast.makeText(context, "Marcado como no visto", Toast.LENGTH_SHORT)
                                .show()
                        }
                }
            } else {
                //Se muestra error de usuario no identificado.
                Toast.makeText(context, "Error: Usuario no identificado", Toast.LENGTH_SHORT).show()
            }
        }
        //Variable que obtiene la lista de url de cada personaje del episodio seleccionado.
        val urlsPersonajes = arguments?.getStringArrayList("clave_personajes")

        //Si la lista no está vacía, se define la variable ids para extraer el ID de cada personaje según el formato de la URL y se llama a cargarPersonajes
        if (urlsPersonajes != null && urlsPersonajes.isNotEmpty()) {
            val ids = urlsPersonajes.map { it.substringAfterLast("/") }.joinToString(",")
            cargarPersonajes(ids, view)
        }
    }
    //Esta función lo que hace es mostrar cada personaje en el recyclerview de la ventana de detalles del episodio, mostrando nombre e imagen.
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