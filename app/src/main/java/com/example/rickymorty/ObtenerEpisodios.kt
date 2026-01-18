package com.example.rickymorty

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface RickAndMortyService {
    @GET("episode") // Endpoint
    fun obtenerEpisodios(): Call<RespuestaApi>
    @GET("character/{ids}")
    fun obtenerPersonajes(@Path("ids") ids: String): Call<List<Personaje>>
}

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