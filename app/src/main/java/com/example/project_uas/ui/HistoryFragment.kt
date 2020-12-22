package com.example.project_uas.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.project_uas.R
import com.example.project_uas.adapter.CardViewOrderAdapter
import com.example.project_uas.adapter.CardViewProductAdapter
import com.example.project_uas.data.URL
import com.example.project_uas.model.Kategori
import com.example.project_uas.model.Order
import com.example.project_uas.model.Product
import com.example.project_uas.model.User
import com.example.project_uas.service.OrderAPI
import com.example.project_uas.service.ProductAPI
import com.example.project_uas.util.Retro
import com.google.android.material.snackbar.Snackbar
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.fragment_cart.*
import kotlinx.android.synthetic.main.fragment_history.*
import kotlinx.android.synthetic.main.fragment_history.view.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HistoryFragment : Fragment() {

    var arrListOrder = arrayListOf<Order>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_history, container, false)

        root.rvOrders.layoutManager = LinearLayoutManager(activity)

        return root
    }

    internal fun getOrders() {
        arrListOrder.clear()

        val retro = Retro().getRetroClientInstance(URL).create(OrderAPI::class.java)

        retro.getOrders(arguments!!.getInt("idUser")).enqueue(object : Callback<JsonObject> {
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Log.e("Failed", "Pesan kesalahan: " + t.message.toString())
            }

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.code() == 200)
                {
                    val result = response.body().get("result").asString

                    if (result == "OK")
                    {
                        val arrOrder = response.body().getAsJsonArray("data")

                        for (item in arrOrder)
                        {
                            val order = Order()

                            order.idOrder = item.asJsonObject.get("idOrder").asInt
                            order.tanggal = item.asJsonObject.get("tanggal").asString
                            order.totalQuantity = item.asJsonObject.get("total_quantity").asInt
                            order.totalJenisItem = item.asJsonObject.get("total_jenis_item").asInt
                            order.grandTotal = item.asJsonObject.get("grand_total").asInt

                            arrListOrder.add(order)
                        }

                        implementToRecyclerView()
                    }
                    else
                    {
                        val message = response.body().get("message").asString

                        val snackBar = Snackbar.make(
                            historyView, message,
                            Snackbar.LENGTH_SHORT
                        ).setAction("Action", null)

                        snackBar.show()
                    }
                }
            }
        })
    }

    fun implementToRecyclerView()
    {
        val cardViewOrderAdapter = CardViewOrderAdapter(arrListOrder)
        rvOrders.adapter = cardViewOrderAdapter
    }

    override fun onResume() {
        super.onResume()

        getOrders()
    }

    companion object {
        fun newInstance(user: User) = HistoryFragment().apply {
            val fragment = HistoryFragment()
            val args = Bundle()
            args.putInt("idUser", user.idUser!!)
            fragment.arguments = args
            return fragment
        }
    }

}