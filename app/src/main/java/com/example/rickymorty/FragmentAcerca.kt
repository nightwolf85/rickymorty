package com.example.rickymorty

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment

class FragmentAcerca : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val builder = AlertDialog.Builder(requireActivity())

        builder.setTitle("Acerca de")
            .setMessage("Desarrollado por: Fernando Ruiz Hernández\nVersión: 1.0.0")
            .setIcon(android.R.drawable.ic_menu_info_details)
            .setPositiveButton("Aceptar") { dialog, _ ->
                dialog.dismiss()
            }

        return builder.create()
    }
}