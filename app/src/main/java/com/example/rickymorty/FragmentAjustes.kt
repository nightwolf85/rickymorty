package com.example.rickymorty

import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.firebase.auth.FirebaseAuth
import java.util.Locale

// En este fragment se gestionarán las diferentes opciones de los ajustes.
class FragmentAjustes : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        //Cargamos el xml de los ajustes
        setPreferencesFromResource(R.xml.settings, rootKey)

        //Funcionalidad del botón para cerrar la sesión
        val btnCerrarSesion = findPreference<Preference>("cerrar_sesion")
        btnCerrarSesion?.setOnPreferenceClickListener {
            cerrarSesion()
            true
        }
    }

    //Función que gestiona los cambios de idioma o tema
    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when (key) {
            "tema" -> {
                val temaSeleccionado = sharedPreferences?.getString(key, "claro")
                aplicarTema(temaSeleccionado)
            }
            "idioma" -> {
                val idiomaSeleccionado = sharedPreferences?.getString(key, "es")
                aplicarIdioma(idiomaSeleccionado)
            }
        }
    }

    //Ajusta el tema según lo seleccionado
    private fun aplicarTema(tema: String?) {
        if (tema == "oscuro") {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    //Aplica el idioma seleccionado
    private fun aplicarIdioma(idioma: String?) {
        val codigoIdioma = idioma ?: "es"
        val locale = Locale(codigoIdioma)
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
        activity?.recreate()
    }

    //Función para cerrar la sesión de Firebase
    private fun cerrarSesion() {
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(requireContext(), Login::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        Toast.makeText(requireContext(), "Sesión cerrada", Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
        preferenceManager.sharedPreferences?.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        preferenceManager.sharedPreferences?.unregisterOnSharedPreferenceChangeListener(this)
    }
}