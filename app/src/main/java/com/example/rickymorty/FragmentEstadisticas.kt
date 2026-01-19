package com.example.rickymorty

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

//Este fragment muestra las estadísticas de los episodios vistos frente al total de episodios
class FragmentEstadisticas : Fragment() {

    //Variables de firebase
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_estadisticas, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        calcularEstadisticas(view)
    }

    //Aquí se calculan los episodios vistos frente al total de episodios.
    private fun calcularEstadisticas(view: View) {
        RetrofitClient.instance.obtenerEpisodios().enqueue(object : Callback<RespuestaApi> {
            override fun onResponse(call: Call<RespuestaApi>, response: Response<RespuestaApi>) {
                if (response.isSuccessful) {
                    val totalEpisodios = response.body()?.info?.count ?: 0
                    contarVistosEnFirebase(view, totalEpisodios)
                }
            }

            override fun onFailure(call: Call<RespuestaApi>, t: Throwable) {
                Toast.makeText(context, "Error conectando API", Toast.LENGTH_SHORT).show()
            }
        })
    }

    //Se cuentan los almacenados en firebase como vistos
    private fun contarVistosEnFirebase(view: View, total: Int) {
        val userId = auth.currentUser?.uid
        if (userId == null) return

        db.collection("usuarios").document(userId).collection("vistos")
            .get()
            .addOnSuccessListener { documents ->
                val vistos = documents.size()
                actualizarUI(view, vistos, total)
            }
            .addOnFailureListener {
                Toast.makeText(context, "Error cargando datos usuario", Toast.LENGTH_SHORT).show()
            }
    }

    //En esta función se actualiza la barra que se muestra en el fragment para indicar cuantos se han visto respecto al total.
    private fun actualizarUI(view: View, vistos: Int, total: Int) {
        val tvContador = view.findViewById<TextView>(R.id.tvContador)
        val tvPorcentaje = view.findViewById<TextView>(R.id.tvPorcentaje)

        val viewVistos = view.findViewById<View>(R.id.viewVistos)
        val viewRestantes = view.findViewById<View>(R.id.viewRestantes)

        tvContador.text = "Has visto $vistos de $total episodios"

        val porcentaje = if (total > 0) (vistos * 100) / total else 0
        tvPorcentaje.text = "$porcentaje%"

        val paramsVistos = viewVistos.layoutParams as LinearLayout.LayoutParams
        paramsVistos.weight = porcentaje.toFloat()
        viewVistos.layoutParams = paramsVistos

        val paramsRestantes = viewRestantes.layoutParams as LinearLayout.LayoutParams
        paramsRestantes.weight = (100 - porcentaje).toFloat()
        viewRestantes.layoutParams = paramsRestantes
    }
}