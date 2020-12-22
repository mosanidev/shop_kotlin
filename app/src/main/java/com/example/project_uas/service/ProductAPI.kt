package com.example.project_uas.service

import com.google.gson.JsonObject
import retrofit2.http.GET

interface ProductAPI {
    @GET("nmp160418117/get_products.php")
    fun getProducts() : retrofit2.Call<JsonObject>
}