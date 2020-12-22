package com.example.project_uas.ui

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.project_uas.Login
import com.example.project_uas.R
import com.example.project_uas.adapter.CardViewCartAdapter
import com.example.project_uas.data.URL
import com.example.project_uas.model.User
import com.example.project_uas.service.UserAPI
import com.example.project_uas.util.Retro
import com.google.android.material.snackbar.Snackbar
import com.google.gson.JsonObject
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_cart.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.layout_ganti_nama.view.*
import kotlinx.android.synthetic.main.layout_ganti_nama.view.dialogCancelNamaBtn
import kotlinx.android.synthetic.main.layout_ganti_nama.view.dialogSimpanNamaBtn
import kotlinx.android.synthetic.main.layout_ganti_password.view.*
import kotlinx.android.synthetic.main.layout_top_up_saldo.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat
import java.util.*

class ProfileFragment : Fragment() {

    val localeID = Locale("in", "ID")
    val formatRupiah: NumberFormat = NumberFormat.getCurrencyInstance(localeID)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_profile, container, false)

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val foto = arguments!!.getString("foto")!!.toString()

        if (foto == "") {

            Picasso.get()
                .load(R.drawable.photo_user)
                .placeholder(R.drawable.photo_user)
                .error(R.drawable.photo_user)
                .into(user_image_profile)

        } else {

            Picasso.get()
                .load(arguments!!.getString("foto")!!.toString())
                .placeholder(R.drawable.photo_user)
                .error(R.drawable.photo_user)
                .into(user_image_profile)

        }

        text_nama_user.text = arguments!!.getString("nama").toString()
        text_email_user.text = arguments!!.getString("email").toString()

        val saldo = arguments!!.getInt("saldo")

        text_saldo_user.setText(formatRupiah.format(saldo))

        buttonGantiNama.setOnClickListener {
            openDialogGantiNama()
        }

        buttonGantiPassword.setOnClickListener {
            openDialogGantiPassword()
        }

        buttonTopUpSaldo.setOnClickListener {
            openDialogTopUpSaldo()
        }

        buttonSignOut.setOnClickListener {
            val intent = Intent(this@ProfileFragment.context, Login::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()

        if (SISA_SALDO != "") {
            text_saldo_user.setText(formatRupiah.format(SISA_SALDO.toInt()))
        }
    }

    fun openDialogGantiNama() {
        val mDialogView = LayoutInflater.from(this@ProfileFragment.context).inflate(R.layout.layout_ganti_nama, null)
        //AlertDialogBuilder
        val mBuilder = AlertDialog.Builder(this@ProfileFragment.context)
            .setView(mDialogView)
            .setTitle("Ganti Nama")
        //show dialog
        val  mAlertDialog = mBuilder.show()
        //login button click of custom layout
        mDialogView.dialogSimpanNamaBtn.setOnClickListener {
            //dismiss dialog
            mAlertDialog.dismiss()

            if (mDialogView.edit_nama_user_baru.text.toString() == "") {

                val snackBar = Snackbar.make(
                    profile_view, "Harap isi semua informasi terlebih dahulu",
                    Snackbar.LENGTH_SHORT
                ).setAction("Action", null)

                snackBar.show()

            } else {

                //get text from EditTexts of custom layout
                val nama = mDialogView.edit_nama_user_baru.text.toString()

                gantiNama(nama)

                //set the input text in TextView
                text_nama_user.setText(nama)

            }
        }
        //cancel button click of custom layout
        mDialogView.dialogCancelNamaBtn.setOnClickListener {
            //dismiss dialog
            mAlertDialog.dismiss()
        }
    }

    fun openDialogGantiPassword() {
        val passwordLama = arguments!!.getString("password").toString()

        val mDialogView = LayoutInflater.from(this@ProfileFragment.context).inflate(R.layout.layout_ganti_password, null)
        //AlertDialogBuilder
        val mBuilder = AlertDialog.Builder(this@ProfileFragment.context)
            .setView(mDialogView)
            .setTitle("Ganti Password")
        //show dialog
        val  mAlertDialog = mBuilder.show()
        //login button click of custom layout
        mDialogView.dialogSimpanPassBtn.setOnClickListener {
            //dismiss dialog
            mAlertDialog.dismiss()

            if (mDialogView.edit_password_lama.text.toString() == "" || mDialogView.edit_password_baru.text.toString() == "" || mDialogView.edit_konfirmasi_password.text.toString() == "") {

                val snackBar = Snackbar.make(
                    profile_view, "Harap isi semua informasi terlebih dahulu",
                    Snackbar.LENGTH_SHORT
                ).setAction("Action", null)

                snackBar.show()

            } else if (mDialogView.edit_password_lama.text.toString() != passwordLama) {

                val snackBar = Snackbar.make(
                    profile_view, "Password lama anda tidak sesuai",
                    Snackbar.LENGTH_SHORT
                ).setAction("Action", null)

                snackBar.show()

            } else if (mDialogView.edit_password_baru.text.toString() != mDialogView.edit_konfirmasi_password.text.toString()) {

                val snackBar = Snackbar.make(
                    profile_view, "Harap ketik ulang password dengan benar",
                    Snackbar.LENGTH_SHORT
                ).setAction("Action", null)

                snackBar.show()

            } else {

                //get text from EditTexts of custom layout
                val password = mDialogView.edit_password_baru.text.toString()

                gantiPassword(password)
            }
        }
        //cancel button click of custom layout
        mDialogView.dialogCancelPassBtn.setOnClickListener {
            //dismiss dialog
            mAlertDialog.dismiss()
        }
    }

    fun openDialogTopUpSaldo() {
        val mDialogView = LayoutInflater.from(this@ProfileFragment.context).inflate(R.layout.layout_top_up_saldo, null)
        //AlertDialogBuilder
        val mBuilder = AlertDialog.Builder(this@ProfileFragment.context)
            .setView(mDialogView)
            .setTitle("Top Up Saldo")
        //show dialog
        val  mAlertDialog = mBuilder.show()
        //login button click of custom layout
        mDialogView.dialogSimpanNamaBtn.setOnClickListener {
            //dismiss dialog
            mAlertDialog.dismiss()

            if (mDialogView.edit_top_up_saldo.text.toString() == "") {

                val snackBar = Snackbar.make(
                    profile_view, "Harap isi semua informasi terlebih dahulu",
                    Snackbar.LENGTH_SHORT
                ).setAction("Action", null)

                snackBar.show()

            } else {

                //get text from EditTexts of custom layout
                val saldoTambahan = mDialogView.edit_top_up_saldo.text.toString()

                topUpSaldo(saldoTambahan)

                Toast.makeText(this.context, "Data berhasil disimpan", Toast.LENGTH_SHORT).show()

            }
        }
        //cancel button click of custom layout
        mDialogView.dialogCancelNamaBtn.setOnClickListener {
            //dismiss dialog
            mAlertDialog.dismiss()
        }
    }

    internal fun gantiNama(nama: String)
    {
        val idUser = arguments!!.getInt("idUser")

        val retro = Retro().getRetroClientInstance(URL).create(UserAPI::class.java)

        retro.gantiNama(idUser, nama).enqueue(object: Callback<JsonObject> {
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Log.e("Failed", "Pesan kesalahan: " + t.message.toString())
            }

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful)
                {
                    val result = response.body().get("result").asString
                    val message = response.body().get("message").asString

                    if (result == "OK")
                    {
                        Toast.makeText(this@ProfileFragment.context, message, Toast.LENGTH_SHORT).show()
                    }
                    else
                    {
                        Toast.makeText(this@ProfileFragment.context, message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    internal fun gantiPassword(password: String)
    {
        val idUser = arguments!!.getInt("idUser")

        val retro = Retro().getRetroClientInstance(URL).create(UserAPI::class.java)

        retro.gantiPassword(idUser, password).enqueue(object: Callback<JsonObject> {
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Log.e("Failed", "Pesan kesalahan: " + t.message.toString())
            }

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful)
                {
                    val result = response.body().get("result").asString
                    val message = response.body().get("message").asString

                    if (result == "OK")
                    {
                        Toast.makeText(this@ProfileFragment.context, message, Toast.LENGTH_SHORT).show()
                    }
                    else
                    {
                        Toast.makeText(this@ProfileFragment.context, message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    internal fun topUpSaldo(saldoTambahan: String)
    {
        val idUser = arguments!!.getInt("idUser")

        val retro = Retro().getRetroClientInstance(URL).create(UserAPI::class.java)

        retro.topUpSaldo(idUser, saldoTambahan).enqueue(object: Callback<JsonObject> {
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Log.e("Failed", "Pesan kesalahan: " + t.message.toString())
            }

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful)
                {
                    val result = response.body().get("result").asString
                    val message = response.body().get("message").asString

                    if (result == "OK")
                    {
                        implementToTextSaldo(message)
                    }
                    else
                    {
                        Toast.makeText(this@ProfileFragment.context, message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    fun implementToTextSaldo(saldo: String)
    {
        val rpSaldo = formatRupiah.format(saldo.toInt())

        text_saldo_user.setText(rpSaldo)
    }

    companion object {
        var SISA_SALDO = ""
        fun newInstance(user: User) = ProfileFragment().apply {
            val fragment = ProfileFragment()
            val args = Bundle()
            args.putInt("idUser", user.idUser!!)
            args.putString("nama", user.nama)
            args.putString("email", user.email)
            args.putString("foto", user.foto)
            args.putString("password", user.password)
            args.putInt("saldo", user.saldo!!)
            fragment.arguments = args
            return fragment
        }
    }
}

