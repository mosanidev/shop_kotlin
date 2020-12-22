package com.example.project_uas.adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.project_uas.Detail
import com.example.project_uas.Home
import com.example.project_uas.R
import com.example.project_uas.data.URL
import com.example.project_uas.model.Cart
import com.example.project_uas.model.Product
import com.example.project_uas.service.CartAPI
import com.example.project_uas.service.ProductAPI
import com.example.project_uas.ui.CartFragment
import com.example.project_uas.ui.HomeFragment
import com.example.project_uas.util.Retro
import com.google.android.material.snackbar.Snackbar
import com.google.gson.JsonObject
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.fragment_cart.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

class CardViewProductAdapter(private val listProduct: ArrayList<Product>, private val idUser:Int) : RecyclerView.Adapter<CardViewProductAdapter.CardViewViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CardViewProductAdapter.CardViewViewHolder {

        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_cardview_product, parent, false)
        return CardViewViewHolder(view)

    }

    override fun getItemCount(): Int {
        return listProduct.size
    }

    override fun onBindViewHolder(
        holder: CardViewProductAdapter.CardViewViewHolder,
        position: Int
    ) {

        val product = listProduct[position]

        holder.tvName.text = product.nama
        holder.tvDeskripsi.text = product.deskripsi

        val localeID = Locale("in", "ID")
        val formatRupiah: NumberFormat = NumberFormat.getCurrencyInstance(localeID)
        holder.tvPrice.setText(formatRupiah.format(product.harga))

        Picasso.get()
            .load(product.foto)
            .placeholder(R.drawable.photo_barang)
            .error(R.drawable.photo_barang)
            .fit()
            .into(holder.imgUser)

        holder.btnDetail.setOnClickListener {

            val intent = Intent(holder.itemView.context, Detail::class.java)

            intent.putExtra(Detail.USER_ID, idUser)
            intent.putExtra(Detail.PRODUCT_ID, product.idProduct)
            intent.putExtra(Detail.PRODUCT_NAME, product.nama)
            intent.putExtra(Detail.PRODUCT_DESCRIPTION, product.deskripsi)
            intent.putExtra(Detail.PRODUCT_PRICE, product.harga)
            intent.putExtra(Detail.PRODUCT_CATEGORY, product.kategori!!.nama)
            intent.putExtra(Detail.PRODUCT_PHOTO, product.foto)

            holder.itemView.context.startActivity(intent)
        }

        holder.btnAddToCart.setOnClickListener {

            val retro = Retro().getRetroClientInstance(URL).create(CartAPI::class.java)

            retro.insertCartItems(idUser, product.idProduct!!, product.harga!!).enqueue(object : Callback<JsonObject> {
                override fun onFailure(call: Call<JsonObject>?, t: Throwable?) {
                    Log.e("Failed", "Pesan kesalahan: " + t!!.message.toString())
                }

                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if (response.isSuccessful) {
                        val result = response.body().get("result").asString
                        val message = response.body().get("message").asString

                        if (result == "ERROR") {
                            Toast.makeText(holder.itemView.context, message, Toast.LENGTH_SHORT).show()
                        } else {

                            val snackBar = Snackbar.make(
                                holder.itemView, "Data berhasil ditambahkan",
                                Snackbar.LENGTH_SHORT
                            ).setAction("Action", null)

                            snackBar.show()

                        }
                    }
                }
            })
        }
    }

    inner class CardViewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var tvName: TextView = itemView.findViewById(R.id.tv_item_name)
        var tvDeskripsi: TextView = itemView.findViewById(R.id.tv_item_description)
        var tvPrice: TextView = itemView.findViewById(R.id.tv_item_price)
        var imgUser: ImageView = itemView.findViewById(R.id.img_item_photo)
        var btnDetail: Button = itemView.findViewById(R.id.btn_detail_barang)
        var btnAddToCart: Button = itemView.findViewById(R.id.btn_add_to_cart)
    }
}