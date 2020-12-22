package com.example.project_uas.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Kategori {
    @SerializedName("idKategori")
    @Expose
    var idKategori:Int? = null

    @SerializedName("nama")
    @Expose
    var nama:String? = null
}