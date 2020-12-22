package com.example.project_uas.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.project_uas.R
import com.example.project_uas.model.Order
import java.text.NumberFormat
import java.util.*

class CardViewOrderAdapter(private val listOrder: ArrayList<Order>) :  RecyclerView.Adapter<CardViewOrderAdapter.CardViewViewHolder>() {

    inner class CardViewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var tvIdOrder: TextView = itemView.findViewById(R.id.tv_order_id)
        var tvTanggalOrder: TextView = itemView.findViewById(R.id.tv_order_date)
        var tvTotalQty: TextView = itemView.findViewById(R.id.tv_order_qty)
        var tvTotalJenis: TextView = itemView.findViewById(R.id.tv_order_total_jenis)
        var tvGrandTotal: TextView = itemView.findViewById(R.id.tv_order_grand_total)
        var tvTextTotalQty: TextView = itemView.findViewById(R.id.text_total_qty)
        var tvTextTotalJenisItem: TextView = itemView.findViewById(R.id.text_total_jenis_item)
        var tvTextGrandTotal: TextView = itemView.findViewById(R.id.text_grand_total)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_cardview_order, parent, false)
        return CardViewViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listOrder.size
    }

    override fun getItemId(position: Int): Long {
        return listOrder[position].idOrder!!.toLong()
    }

    override fun onBindViewHolder(holder: CardViewViewHolder, position: Int) {
        val order = listOrder[position]

        val strIdOrder = "#NMP" + order.idOrder.toString()
        holder.tvIdOrder.setText(strIdOrder)

        holder.tvTanggalOrder.setText(order.tanggal)

        holder.tvTotalQty.setText(order.totalQuantity.toString())

        holder.tvTotalJenis.setText(order.totalJenisItem.toString())

        holder.tvTextTotalQty.setText("Total Quantity: ")
        holder.tvTextTotalJenisItem.setText("Total Jenis Item: ")
        holder.tvTextGrandTotal.setText("Grand Total: ")

        val localeID = Locale("in", "ID")
        val formatRupiah: NumberFormat = NumberFormat.getCurrencyInstance(localeID)
        holder.tvGrandTotal.setText(formatRupiah.format(order.grandTotal))
    }
}