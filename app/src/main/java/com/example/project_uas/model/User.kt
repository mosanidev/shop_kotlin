package com.example.project_uas.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class User {
    @SerializedName("idUser")
    @Expose
    var idUser:Int? = null

    @SerializedName("nama")
    @Expose
    var nama:String? = null

    @SerializedName("email")
    @Expose
    var email:String? = null

    @SerializedName("saldo")
    @Expose
    var saldo:Int? = null

    @SerializedName("password")
    @Expose
    var password:String? = null

    @SerializedName("password")
    @Expose
    var foto:String? = null
}