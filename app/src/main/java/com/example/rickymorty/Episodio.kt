package com.example.rickymorty

import com.google.gson.annotations.SerializedName

data class InfoApi(
    val count: Int,
    val pages: Int
)
data class RespuestaApi(
    val info: InfoApi,
    val results: List<Episodio>
)

data class Episodio(
    val id: Int,
    val name: String,
    val episode: String,
    @SerializedName("air_date")
    val airDate: String,
    val characters: List<String>,
    var visto: Boolean = false,
    var seleccionado: Boolean = false
)