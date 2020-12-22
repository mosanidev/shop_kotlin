package com.example.project_uas.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Cart {
    @SerializedName("idCart")
    @Expose
    var idCart:Int? = null

    @SerializedName("product")
    @Expose
    var product:Product? = null

    @SerializedName("quantity")
    @Expose
    var quantity:Int? = null

    @SerializedName("subTotal")
    @Expose
    var subTotal:Int? = null

    @SerializedName("totalSubTotal")
    @Expose
    var totalSubTotal:Int? = null
}