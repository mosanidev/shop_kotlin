package com.example.project_uas.service

import com.google.gson.JsonObject
import retrofit2.http.*

interface UserAPI {
    @FormUrlEncoded
    @POST("nmp160418117/sign_up.php")
    fun createUser(@Field("nama") nama: String,
                   @Field("email") email: String,
                   @Field("password") password: String
    ) : retrofit2.Call<JsonObject>

    @FormUrlEncoded
    @POST("nmp160418117/login.php")
    fun getUser(@Field("email") email: String,
                @Field("password") password: String
    ) : retrofit2.Call<JsonObject>

    @FormUrlEncoded
    @POST("nmp160418117/ganti_nama.php")
    fun gantiNama(@Field("idUser") idUser: Int,
                  @Field("nama") nama: String
    ) : retrofit2.Call<JsonObject>

    @FormUrlEncoded
    @POST("nmp160418117/ganti_password.php")
    fun gantiPassword(@Field("idUser") idUser: Int,
                      @Field("password") password: String
    ) : retrofit2.Call<JsonObject>

    @FormUrlEncoded
    @POST("nmp160418117/top_up_saldo.php")
    fun topUpSaldo(@Field("idUser") idUser: Int,
                   @Field("saldoTambahan") saldoTambahan: String
    ) : retrofit2.Call<JsonObject>

    @FormUrlEncoded
    @POST("nmp160418117/check_transaction.php")
    fun checkTransaction(@Field("idUser") idUser: Int
    ) : retrofit2.Call<JsonObject>
}