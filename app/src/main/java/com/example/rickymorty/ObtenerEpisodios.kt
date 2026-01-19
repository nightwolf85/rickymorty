package com.example.rickymorty

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

//Aquí nos conectamos mediante la API y obtenemos el listado de episodios y los id de los personajes que usaremos posteriormente.

//En la interfaz se indica que estamos buscando
interface RickAndMortyService {
    @GET("episode") // Endpoint
    fun obtenerEpisodios(): Call<RespuestaApi>
    @GET("character/{ids}")
    fun obtenerPersonajes(@Path("ids") ids: String): Call<List<Personaje>>
}

//Mediante este objeto se define la URL de la API y usando retrofit obtenemos la información que buscamos.
object RetrofitClient {
    private const val BASE_URL = "https://rickandmortyapi.com/api/"

    val instance: RickAndMortyService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(RickAndMortyService::class.java)
    }
}