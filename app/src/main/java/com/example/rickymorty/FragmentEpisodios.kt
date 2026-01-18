package com.example.rickymorty

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rickymorty.Episodio
import com.example.rickymorty.RespuestaApi
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FragmentEpisodios : Fragment() {

    private lateinit var adapter: EpisodiosAdapter
    private lateinit var recyclerView: RecyclerView

    private var listaCompleta: List<Episodio> = ArrayList() // Guardamos TODOS aquí
    private var soloVistos: Boolean = false // Para saber si el filtro está activo

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_episodios, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerViewEpisodios)
        recyclerView.layoutManager = LinearLayoutManager(context)

        adapter = EpisodiosAdapter(ArrayList()) { episodio ->
            val bundle = Bundle().apply {
                putString("clave_nombre", episodio.name)
                putString("clave_codigo", episodio.episode)
                putString("clave_fecha", episodio.airDate)
                putBoolean("clave_visto", episodio.visto)
                putStringArrayList("clave_personajes", ArrayList(episodio.characters))
            }
            findNavController().navigate(R.id.action_episodios_to_detalles, bundle)
        }
        recyclerView.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        cargarDatos()
    }

    private fun cargarDatos() {
        RetrofitClient.instance.obtenerEpisodios().enqueue(object : Callback<RespuestaApi> {
            override fun onResponse(call: Call<RespuestaApi>, response: Response<RespuestaApi>) {
                if (response.isSuccessful) {
                    val episodiosApi = response.body()?.results ?: emptyList()

                    cruzarConFirebase(episodiosApi)
                }
            }

            override fun onFailure(call: Call<RespuestaApi>, t: Throwable) {
                Toast.makeText(context, "Error API: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun cruzarConFirebase(episodiosApi: List<Episodio>) {
        val userId = auth.currentUser?.uid

        if (userId != null) {
            db.collection("usuarios").document(userId).collection("vistos")
                .get()
                .addOnSuccessListener { documents ->
                    val listaCodigosVistos = documents.map { it.id }

                    for (episodio in episodiosApi) {
                        if (listaCodigosVistos.contains(episodio.episode)) {
                            episodio.visto = true
                        } else {
                            episodio.visto = false
                        }
                    }

                    listaCompleta = episodiosApi

                    aplicarFiltro()
                }
                .addOnFailureListener {
                    listaCompleta = episodiosApi
                    aplicarFiltro()
                }
        }
    }

    private fun aplicarFiltro() {
        if (soloVistos) {
            val listaFiltrada = listaCompleta.filter { it.visto == true }
            adapter.actualizarDatos(listaFiltrada)
        } else {
            adapter.actualizarDatos(listaCompleta)
        }
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {

        menu.add(Menu.NONE, 1, Menu.NONE, "Filtro Vistos")
            .setIcon(android.R.drawable.ic_menu_view)
            .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == 1) {
            soloVistos = !soloVistos

            if (soloVistos) {
                Toast.makeText(context, "Mostrando solo VISTOS", Toast.LENGTH_SHORT).show()
                item.setIcon(android.R.drawable.checkbox_on_background)
            } else {
                Toast.makeText(context, "Mostrando TODOS", Toast.LENGTH_SHORT).show()
                item.setIcon(android.R.drawable.ic_menu_view)
            }

            aplicarFiltro()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}