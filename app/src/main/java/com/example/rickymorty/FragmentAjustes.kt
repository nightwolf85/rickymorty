package com.example.rickymorty

import android.os.Bundle
import android.widget.Toast
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat

class FragmentAjustes : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings, rootKey)

        val botonSalir = findPreference<Preference>("cerrar_sesion")
        botonSalir?.setOnPreferenceClickListener {
            Toast.makeText(context, "Cerrando sesión...", Toast.LENGTH_SHORT).show()
            true
        }
    }
}