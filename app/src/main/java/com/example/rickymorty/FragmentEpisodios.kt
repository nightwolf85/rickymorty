package com.example.rickymorty

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FragmentEpisodios : Fragment() {

    private lateinit var adapter: EpisodiosAdapter
    private lateinit var recyclerView: RecyclerView
    private var menuSuperior: Menu? = null

    // Variables de datos
    private var listaCompleta: List<Episodio> = ArrayList()
    private var soloVistos: Boolean = false

    // Variables de Firebase
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

        adapter = EpisodiosAdapter(
            ArrayList(),

            onEpisodioClick = { episodio ->
                val bundle = Bundle().apply {
                    putString("clave_nombre", episodio.name)
                    putString("clave_codigo", episodio.episode)
                    putString("clave_fecha", episodio.airDate)
                    putBoolean("clave_visto", episodio.visto)
                    putStringArrayList("clave_personajes", ArrayList(episodio.characters))
                }
                findNavController().navigate(R.id.action_episodios_to_detalles, bundle)
            },

            onModoSeleccionChange = { estaSeleccionando ->
                val itemGuardar = menuSuperior?.findItem(R.id.guardarSeleccion)
                itemGuardar?.isVisible = estaSeleccionando
            }
        )
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
                Toast.makeText(context, "Fallo de conexión API", Toast.LENGTH_SHORT).show()
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
            val listaFiltrada = listaCompleta.filter { it.visto }
            adapter.actualizarDatos(listaFiltrada)
        } else {
            adapter.actualizarDatos(listaCompleta)
        }
    }

    // --- MENÚ SUPERIOR (TOOLBAR) ---
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_toolbar, menu)
        menuSuperior = menu
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.filtrar -> {
                soloVistos = !soloVistos
                aplicarFiltro()

                if (soloVistos) Toast.makeText(context, "Filtro: Solo Vistos", Toast.LENGTH_SHORT).show()
                else Toast.makeText(context, "Filtro: Todos", Toast.LENGTH_SHORT).show()

                true
            }
            R.id.guardarSeleccion -> {
                guardarSeleccionMasiva()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun guardarSeleccionMasiva() {
        val seleccionados = adapter.obtenerSeleccionados()
        if (seleccionados.isEmpty()) return

        val userId = auth.currentUser?.uid ?: return

        val batch = db.batch()

        for (episodio in seleccionados) {
            val docRef = db.collection("usuarios").document(userId)
                .collection("vistos").document(episodio.episode)

            val datos = hashMapOf(
                "name" to episodio.name,
                "episode" to episodio.episode,
                "air_date" to episodio.airDate,
                "characters" to listOf("Batch Update"),
                "viewed" to true
            )
            batch.set(docRef, datos)

            episodio.visto = true
        }

        batch.commit().addOnSuccessListener {
            Toast.makeText(context, "Guardados ${seleccionados.size} episodios", Toast.LENGTH_SHORT).show()
            adapter.cancelarSeleccion()
            aplicarFiltro()
        }.addOnFailureListener {
            Toast.makeText(context, "Error al guardar", Toast.LENGTH_SHORT).show()
        }
    }
}