package com.example.project_uas.ui

import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.project_uas.R
import com.example.project_uas.adapter.CardViewCartAdapter
import com.example.project_uas.data.URL
import com.example.project_uas.model.Cart
import com.example.project_uas.model.Product
import com.example.project_uas.model.User
import com.example.project_uas.service.CartAPI
import com.example.project_uas.service.OrderAPI
import com.example.project_uas.service.UserAPI
import com.example.project_uas.util.Retro
import com.google.android.material.snackbar.Snackbar
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.fragment_cart.*
import kotlinx.android.synthetic.main.fragment_cart.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.HttpRetryException
import java.net.HttpURLConnection
import java.text.NumberFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class CartFragment : Fragment() {

    var arrListProduct = arrayListOf<Cart>()
    val localeID = Locale("in", "ID")
    val formatRupiah: NumberFormat = NumberFormat.getCurrencyInstance(localeID)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_cart, container, false)

        root.rvCart.layoutManager = LinearLayoutManager(activity)

        return root
    }

    fun implementToRecyclerView() {
        val cardViewCartAdapter =
            CardViewCartAdapter(arrListProduct, arguments!!.getInt("idUser"))
        rvCart.adapter = cardViewCartAdapter
    }

    fun implementSubTotal() {
        text_sub_total.setText(formatRupiah.format(subTotal))
    }

    internal fun getCartItems() {

        arrListProduct.clear()

        val retro = Retro().getRetroClientInstance(URL).create(CartAPI::class.java)

        retro.getCartItems(arguments!!.getInt("idUser")).enqueue(object : Callback<JsonObject> {
            override fun onFailure(call: Call<JsonObject>?, t: Throwable) {
                Log.e("Failed", "Pesan kesalahan: " + t.message.toString())
            }

            override fun onResponse(call: Call<JsonObject>?, response: Response<JsonObject>) {
                if (response.code() == 200) {
                    val result = response.body().get("result").asString

                    if (result == "OK") {
                        val datas = response.body().getAsJsonArray("data")

                        for (data in datas) {
                            val cart = Cart()
                            cart.idCart = data.asJsonObject.get("idCart").asInt

                            val productC = Product()
                            productC.idProduct = data.asJsonObject.get("idProduct").asInt
                            productC.nama = data.asJsonObject.get("nama").asString
                            productC.foto = data.asJsonObject.get("foto").asString

                            cart.product = productC

                            cart.subTotal = data.asJsonObject.get("sub_total").asInt

                            cart.quantity = data.asJsonObject.get("quantity").asInt

                            subTotal = data.asJsonObject.get("total_sub_total").asInt

                            arrListProduct.add(cart)
                        }

                        implementToRecyclerView()
                        implementSubTotal()
                    } else {
                        val snackBar = Snackbar.make(
                            cartView, "Keranjang masih kosong",
                            Snackbar.LENGTH_SHORT
                        ).setAction("Action", null)

                        snackBar.show()
                    }
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()

        getCartItems()

        btn_check_out.setOnClickListener {

            // ceck jumlah saldo apakah mencukupi
            checkTransaction()

        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        CardViewCartAdapter.context = this.context
    }

    internal fun checkTransaction()
    {
        val retro = Retro().getRetroClientInstance(URL).create(UserAPI::class.java)

        retro.checkTransaction(arguments!!.getInt("idUser")).enqueue(object : Callback<JsonObject> {
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Log.e("Failed", "Pesan kesalahan: " + t.message.toString())
            }

            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.code() == 200) {

                    val result = response.body().get("result").asString

                    if (result == "ERROR") {

                        val message = response.body().get("message").asString

                        val snackBar = Snackbar.make(
                            cartView, message,
                            Snackbar.LENGTH_SHORT
                        ).setAction("Action", null)

                        snackBar.show()

                    } else {

                        val message = response.body().get("message").asString

                        if (message == "TRUE")
                        {
                          insertOrder()
                        } else if (text_sub_total.text == "Rp0"){

                            val snackBar = Snackbar.make(
                                cartView, "Keranjang anda masih kosong, isi terlebih dahulu",
                                Snackbar.LENGTH_SHORT
                            ).setAction("Action", null)

                            snackBar.show()
                        } else {

                            val snackBar = Snackbar.make(
                                cartView, "Saldo anda tidak mencukupi",
                                Snackbar.LENGTH_SHORT
                            ).setAction("Action", null)

                            snackBar.show()

                        }
                    }
                }
            }

        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    internal  fun insertOrder()
    {
        val curDateTime = LocalDate.now()
        val formatDate = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val curDate = formatDate.format(curDateTime)

//        val curDate = "2020-11-30"
        val arrIdCart = arrayListOf<Int>()

        for(item in arrListProduct) {
            arrIdCart.add(item.idCart!!)
        }

        val retro = Retro().getRetroClientInstance(URL).create(OrderAPI::class.java)

        retro.insertCartItems(arguments!!.getInt("idUser"), curDate, arrIdCart).enqueue(object : Callback<JsonObject>{
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Log.e("Failed", "Pesan kesalahan: " + t.message.toString())
            }

            override fun onResponse(call: Call<JsonObject>?, response: Response<JsonObject>?) {
                if (response!!.code() == 200)
                {
                    val result = response.body().get("result").asString

                    if (result == "OK") {

                        // DIalog view result checkout sukses
                        val mDialogView = LayoutInflater.from(this@CartFragment.context).inflate(R.layout.layout_order_sukses, null)
                        //AlertDialogBuilder
                        val mBuilder = AlertDialog.Builder(this@CartFragment.context)
                            .setView(mDialogView)
                        //show dialog
                        mBuilder.show()

                        val idOrder = response.body().get("idOrder").asString
                        val sisaSaldo = response.body().get("sisa_saldo").asString

                        // PASS DATA idOrder Ke Fragment order history
                        ProfileFragment.SISA_SALDO = sisaSaldo

                        arrListProduct.clear()
                        rvCart.adapter!!.notifyDataSetChanged()

                        text_sub_total.setText("Rp0")

                    } else
                    {
                        Toast.makeText(this@CartFragment.context, response.body().get("message").asString, Toast.LENGTH_SHORT).show()
                    }
                } else if (response.code() == 500) {
                        Toast.makeText(this@CartFragment.context, "try " + response.code().toString(), Toast.LENGTH_LONG).show()

                }
            }

        })
    }

    companion object {
        var subTotal = 0
        fun newInstance(user: User) = CartFragment().apply {
            val fragment = CartFragment()
            val args = Bundle()
            args.putInt("idUser", user.idUser!!)
            fragment.arguments = args
            return fragment
        }
    }
}

