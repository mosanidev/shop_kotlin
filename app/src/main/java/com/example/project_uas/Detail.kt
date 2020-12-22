package com.example.project_uas

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.core.view.GravityCompat
import com.example.project_uas.data.URL
import com.example.project_uas.service.CartAPI
import com.example.project_uas.util.Retro
import com.google.android.material.snackbar.Snackbar
import com.google.gson.JsonObject
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.activity_detail.toolbar
import kotlinx.android.synthetic.main.activity_home.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat
import java.util.*

class Detail : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)
        supportActionBar?.setTitle(R.string.app_name)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val idUser = intent.getIntExtra(USER_ID, 0)
        val idProduct = intent.getIntExtra(PRODUCT_ID, 0)
        val nama = intent.getStringExtra(PRODUCT_NAME)
        val desc = intent.getStringExtra(PRODUCT_DESCRIPTION)
        val price = intent.getIntExtra(PRODUCT_PRICE, 0)
        val category = intent.getStringExtra(PRODUCT_CATEGORY)
        val photo = intent.getStringExtra(PRODUCT_PHOTO)

        text_name_detail.text = nama
        text_desc_detail.text = desc
        text_category_detail.text = category

        val localeID = Locale("in", "ID")
        val formatRupiah: NumberFormat = NumberFormat.getCurrencyInstance(localeID)
        text_price_detail.setText(formatRupiah.format(price))

        Picasso.get()
            .load(photo)
            .placeholder(R.drawable.photo_barang)
            .error(R.drawable.photo_barang)
            .resize(400, 460)
            .centerInside()
            .into(imageView)

        btn_add_to_cart.setOnClickListener {
            val retro = Retro().getRetroClientInstance(URL).create(CartAPI::class.java)

            retro.insertCartItems(idUser, idProduct, price).enqueue(object :
                Callback<JsonObject> {
                override fun onFailure(call: Call<JsonObject>?, t: Throwable?) {
                    Log.e("Failed", "Pesan kesalahan: " + t!!.message.toString())
                }

                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if (response.isSuccessful) {
                        val result = response.body().get("result").asString
                        val message = response.body().get("message").asString

                        if (result == "ERROR") {
                            Toast.makeText(this@Detail.baseContext, message, Toast.LENGTH_SHORT).show()
                        } else {

                            val snackBar = Snackbar.make(
                                detailView, "Data berhasil ditambahkan",
                                Snackbar.LENGTH_SHORT
                            ).setAction("Action", null)

                            snackBar.show()

                        }
                    }
                }
            })
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home ->
                finish()
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        const val USER_ID = "extra_user_id"
        const val PRODUCT_ID = "extra_product_id"
        const val PRODUCT_NAME = "extra_name"
        const val PRODUCT_DESCRIPTION = "extra_desc"
        const val PRODUCT_PRICE = "extra_price"
        const val PRODUCT_CATEGORY = "extra_category"
        const val PRODUCT_PHOTO = "extra_photo"
    }
}