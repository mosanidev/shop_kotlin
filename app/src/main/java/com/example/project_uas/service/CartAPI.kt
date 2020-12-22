package com.example.project_uas.service

import com.google.gson.JsonObject
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface CartAPI {
    @FormUrlEncoded
    @POST("nmp160418117/insert_to_cart.php")
    fun insertCartItems(@Field("idUser") idUser: Int,
                        @Field("idProduct") idProduct: Int,
                        @Field("subTotal") subTotal: Int
    ) : retrofit2.Call<JsonObject>

    @FormUrlEncoded
    @POST("nmp160418117/get_all_items_cart.php")
    fun getCartItems(@Field("idUser") idUser: Int
    ) : retrofit2.Call<JsonObject>

    @FormUrlEncoded
    @POST("nmp160418117/add_quantity.php")
    fun addQuantity(@Field("idCart") idCart: Int,
                    @Field("idUser") idUser: Int
    ) : retrofit2.Call<JsonObject>

    @FormUrlEncoded
    @POST("nmp160418117/substract_quantity.php")
    fun substractQuantity(@Field("idCart") idCart: Int,
                          @Field("idUser") idUser: Int
    ) : retrofit2.Call<JsonObject>
}