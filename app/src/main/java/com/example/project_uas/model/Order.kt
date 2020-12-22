package com.example.project_uas.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Order {
    @SerializedName("idOrder")
    @Expose
    var idOrder:Int? = null

    @SerializedName("tanggal")
    @Expose
    var tanggal:String? = null

    @SerializedName("totalQuantity")
    @Expose
    var totalQuantity:Int? = null

    @SerializedName("totalJenisItem")
    @Expose
    var totalJenisItem:Int? = null

    @SerializedName("grandTotal")
    @Expose
    var grandTotal:Int? = null
}