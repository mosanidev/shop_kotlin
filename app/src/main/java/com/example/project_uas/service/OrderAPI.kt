package com.example.project_uas.service

import com.google.gson.JsonObject
import retrofit2.http.*

interface OrderAPI {
    @FormUrlEncoded
    @POST("nmp160418117/insert_order_history.php")
    fun insertCartItems(@Field("idUser") idUser: Int,
                        @Field("tanggal") tanggal: String,
                        @Field("arrIdCart[]") arrIdCart: ArrayList<Int>
    ) : retrofit2.Call<JsonObject>

    @FormUrlEncoded
    @POST("nmp160418117/get_order_info.php")
    fun getOrders(@Field("idUser") idUser: Int) : retrofit2.Call<JsonObject>
}