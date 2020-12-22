package com.example.project_uas.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Product {
    @SerializedName("idProduct")
    @Expose
    var idProduct:Int? = null

    @SerializedName("nama")
    @Expose
    var nama:String? = null

    @SerializedName("deskripsi")
    @Expose
    var deskripsi:String? = null

    @SerializedName("harga")
    @Expose
    var harga:Int? = null

    @SerializedName("foto")
    @Expose
    var foto:String? = null

    @SerializedName("kategori")
    @Expose
    var kategori:Kategori? = null
}