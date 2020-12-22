package com.example.project_uas.adapter

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.project_uas.R
import com.example.project_uas.data.URL
import com.example.project_uas.model.Cart
import com.example.project_uas.service.CartAPI
import com.example.project_uas.util.Retro
import com.google.gson.JsonObject
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat
import java.util.*


class CardViewCartAdapter(private val listProduct: ArrayList<Cart>, private val idUser: Int) : RecyclerView.Adapter<CardViewCartAdapter.CardViewViewHolder>()  {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CardViewCartAdapter.CardViewViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_cardview_cart, parent, false)
        return CardViewViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listProduct.size
    }

    inner class CardViewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var tvName: TextView = itemView.findViewById(R.id.tv_item_name)
        var tvPrice: TextView = itemView.findViewById(R.id.tv_item_price)
        var imgCart: ImageView = itemView.findViewById(R.id.img_item_photo)
        var edtQuantity: EditText = itemView.findViewById(R.id.edit_quantity)
        var btnAddQty: ImageButton = itemView.findViewById(R.id.btn_add_quantity)
        var btnMinQty: ImageButton = itemView.findViewById(R.id.btn_min_quantity)
    }

    override fun onBindViewHolder(holder: CardViewViewHolder, position: Int) {
        val product = listProduct[position]

        holder.tvName.text = product.product!!.nama

        val localeID = Locale("in", "ID")
        val formatRupiah: NumberFormat = NumberFormat.getCurrencyInstance(localeID)
        holder.tvPrice.setText(formatRupiah.format(product.subTotal))

        Picasso.get()
            .load(product.product!!.foto)
            .placeholder(R.drawable.photo_barang)
            .error(R.drawable.photo_barang)
            .fit()
            .into(holder.imgCart)

        holder.edtQuantity.setText(product.quantity!!.toString(), TextView.BufferType.EDITABLE)

        holder.btnAddQty.setOnClickListener {

            val retro = Retro().getRetroClientInstance(URL).create(CartAPI::class.java)

            retro.addQuantity(product.idCart!!, idUser).enqueue(object :
                Callback<JsonObject> {
                override fun onFailure(call: Call<JsonObject>?, t: Throwable?) {
                    Log.e("Failed", "Pesan kesalahan: " + t!!.message.toString())
                }

                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if (response.isSuccessful) {
                        val result = response.body().get("result").asString

                        if (result == "ERROR") {
                            Toast.makeText(holder.itemView.context, "Data gagal ditambahkan", Toast.LENGTH_SHORT).show()
                        } else {

                            val quantity = response.body().get("quantity").asString
                            val subTotal = response.body().get("sub_total").asInt
                            val totalSubTotal = response.body().get("total_sub_total").asInt

                            holder.edtQuantity.setText(quantity, TextView.BufferType.EDITABLE)

                            holder.tvPrice.setText(formatRupiah.format(subTotal))

                            val txtTotal =
                                (context as Activity).findViewById<View>(R.id.text_sub_total) as TextView
                            txtTotal.setText(formatRupiah.format(totalSubTotal))
                        }
                    }
                }
            })
        }

        holder.btnMinQty.setOnClickListener {

            if (holder.edtQuantity.text.toString() == "1") {
                Toast.makeText(holder.itemView.context, "Maaf quantity tidak bisa dikurangi lagi", Toast.LENGTH_SHORT).show()
            } else {

                val retro = Retro().getRetroClientInstance(URL).create(CartAPI::class.java)

                retro.substractQuantity(product.idCart!!, idUser).enqueue(object :
                    Callback<JsonObject> {
                    override fun onFailure(call: Call<JsonObject>?, t: Throwable?) {
                        Log.e("Failed", "Pesan kesalahan: " + t!!.message.toString())
                    }

                    override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                        if (response.isSuccessful) {
                            val result = response.body().get("result").asString

                            if (result == "ERROR") {
                                Toast.makeText(holder.itemView.context, "Data gagal dikurangi", Toast.LENGTH_SHORT).show()
                            } else {
                                val quantity = response.body().get("quantity").asString
                                val subTotal = response.body().get("sub_total").asInt
                                val totalSubTotal = response.body().get("total_sub_total").asInt

                                holder.edtQuantity.setText(quantity, TextView.BufferType.EDITABLE)

                                holder.tvPrice.setText(formatRupiah.format(subTotal))

                                val txtTotal =
                                    (context as Activity).findViewById<View>(R.id.text_sub_total) as TextView
                                txtTotal.setText(formatRupiah.format(totalSubTotal))
                            }
                        }
                    }
                })
            }
        }
    }

    override fun getItemId(position: Int): Long {
        return listProduct[position].idCart!!.toLong()
    }

    companion object {
        var context: Context? = null
    }
}