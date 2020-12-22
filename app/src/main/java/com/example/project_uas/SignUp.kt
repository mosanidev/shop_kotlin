package com.example.project_uas

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.core.view.GravityCompat
import com.example.project_uas.data.URL
import com.example.project_uas.model.User
import com.example.project_uas.service.UserAPI
import com.example.project_uas.util.Retro
import com.google.android.material.snackbar.Snackbar
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.android.synthetic.main.activity_sign_up.editTextTextEmailAddress
import kotlinx.android.synthetic.main.activity_sign_up.editTextTextPassword
import kotlinx.android.synthetic.main.activity_sign_up.toolbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUp : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        setSupportActionBar(toolbar)
        supportActionBar?.setTitle(R.string.app_name)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        buttonSubmit.setOnClickListener {

            if (editTextTextEmailAddress.text.toString() == "" || editTextTextName.text.toString() == "" || editTextTextPassword.text.toString() == "") {

                val snackBar = Snackbar.make(
                    signUpView, "Harap isi semua informasi terlebih dahulu",
                    Snackbar.LENGTH_SHORT
                ).setAction("Action", null)

                snackBar.show()

            } else if (editTextTextPassword.text.toString() != editTextTextPassword2.text.toString()) {

                val snackBar = Snackbar.make(
                    signUpView, "Harap ketik ulang password dengan benar",
                    Snackbar.LENGTH_SHORT
                ).setAction("Action", null)

                snackBar.show()

            } else {

                signUp()

            }
        }
    }

    internal fun signUp() {
        val user = User()
        user.email = editTextTextEmailAddress.text.toString()
        user.nama = editTextTextName.text.toString()
        user.password = editTextTextPassword.text.toString()

        val retro = Retro().getRetroClientInstance(URL).create(UserAPI::class.java)

        retro.createUser(
            user.nama.toString(),
            user.email.toString(),
            user.password.toString()
        ).enqueue(object : Callback<JsonObject> {
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Log.e("Failed", "Pesan kesalahan: " + t.message.toString())
            }

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {

                if (response.code() == 200) {

                    val result = response.body().get("result").asString

                    if (result == "OK") {

                        // start activity ke Home
                        val intent = Intent(this@SignUp, Home::class.java)
                        val datas = response.body().getAsJsonArray("data")
                        for (data in datas)
                        {
                            val idUser = data.asJsonObject.get("idUser").asInt
                            val nama = data.asJsonObject.get("nama").asString
                            val email = data.asJsonObject.get("email").asString
                            val saldo = data.asJsonObject.get("saldo").asInt
                            val foto = data.asJsonObject.get("foto").asString
                            val password = data.asJsonObject.get("password").asString

                            intent.putExtra(Home.USER_ID, idUser)
                            intent.putExtra(Home.USER_NAMA, nama)
                            intent.putExtra(Home.USER_EMAIL, email)
                            intent.putExtra(Home.USER_SALDO, saldo)
                            intent.putExtra(Home.USER_FOTO, foto)
                            intent.putExtra(Home.USER_PASSWORD, password)
                        }

                        startActivity(intent)

                    } else {

                        val message = response.body().get("message").asString

                        if (result == "ERROR" && message == "Email sudah dipakai") {

                            val snackBar = Snackbar.make(
                                signUpView, "Email sudah dipakai, mohon gunakan email lain",
                                Snackbar.LENGTH_SHORT
                            ).setAction("Action", null)

                            snackBar.show()
                        }

                    }
                } else {

                    Toast.makeText(applicationContext, "Server tidak merespon", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home ->
                finish()
        }
        return super.onOptionsItemSelected(item)
    }
}


