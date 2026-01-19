package com.example.rickymorty

import com.google.gson.annotations.SerializedName //Se usa para traducir la etiqueta de la fecha que se obtiene mediante la API

//Algunas de las variables se definen en inglés para seguir el mismo patrón que lo que se obtiene desde la API

//Esta clase es para leer la cabecera de la API
data class InfoApi(
    val count: Int, //Cuenta el total de episodios
    val pages: Int
)

//Clase para leer la respuesta de la API
data class RespuestaApi(
    val info: InfoApi, //Se usa la información obtenida de la cabecera
    val results: List<Episodio> // Se define una lista de episodios para almacenarlos
)

//Clase que define los atributos de cada episodio
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