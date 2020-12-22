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
import com.example.project_uas.adapter.CardViewProductAdapter
import com.example.project_uas.data.URL
import com.example.project_uas.model.Kategori
import com.example.project_uas.model.Product
import com.example.project_uas.model.User
import com.example.project_uas.service.ProductAPI
import com.example.project_uas.util.Retro
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class HomeFragment : Fragment() {

    var arrListProduct = arrayListOf<Product>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getProducts()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        root.rvProducts.layoutManager = LinearLayoutManager(activity)

        return root
    }

    internal fun getProducts() {
        arrListProduct.clear()

        val retro = Retro().getRetroClientInstance(URL).create(ProductAPI::class.java)

        retro.getProducts().enqueue(object : Callback<JsonObject> {
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Log.e("Failed", "Pesan kesalahan: " + t.message.toString())
            }

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.code() == 200)
                {
                    val result = response.body().get("result").asString

                    if (result == "OK")
                    {
                        val arrProduct = response.body().getAsJsonArray("data")

                        for (item in arrProduct)
                        {
                            val product = Product()

                            product.idProduct = item.asJsonObject.get("idProduct").asInt
                            product.nama = item.asJsonObject.get("nama_barang").asString
                            product.deskripsi = item.asJsonObject.get("deskripsi").asString
                            product.harga = item.asJsonObject.get("harga").asInt
                            product.foto = item.asJsonObject.get("foto").asString

                            val kategori = Kategori()
                            kategori.idKategori = item.asJsonObject.get("idKategori").asInt
                            kategori.nama = item.asJsonObject.get("nama_kategori").asString

                            product.kategori = kategori

                            arrListProduct.add(product)
                        }

                        implementToRecyclerView()
                    }
                    else
                    {
                        Toast.makeText(activity!!.baseContext, result, Toast.LENGTH_LONG).show()
                    }
                }
            }
        })
    }

    fun implementToRecyclerView()
    {
        val cardViewProductAdapter = CardViewProductAdapter(arrListProduct, arguments!!.getInt("idUser"))
        rvProducts.adapter = cardViewProductAdapter
    }

    override fun onResume() {
        super.onResume()

    }

    companion object {
        fun newInstance(user: User) = HomeFragment().apply {
            val fragment = HomeFragment()
            val args = Bundle()
            args.putInt("idUser", user.idUser!!)
            fragment.arguments = args
            return fragment
        }
    }
}