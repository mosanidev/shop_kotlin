package com.example.project_uas

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.project_uas.adapter.ViewPagerAdapter
import com.example.project_uas.model.User
import com.example.project_uas.ui.CartFragment
import com.example.project_uas.ui.HistoryFragment
import com.example.project_uas.ui.HomeFragment
import com.example.project_uas.ui.ProfileFragment
import kotlinx.android.synthetic.main.activity_home.*


class Home : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setSupportActionBar(toolbar)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_dehaze_24)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val toggle = ActionBarDrawerToggle(this, container, R.string.title_open_nav_drawer, R.string.title_close_nav_drawer)

        container.addDrawerListener(toggle)

        toggle.syncState()

        val user = User()
        user.idUser = intent.getIntExtra(USER_ID, 0)
        user.nama = intent.getStringExtra(USER_NAMA)
        user.email = intent.getStringExtra(USER_EMAIL)
        user.password = intent.getStringExtra(USER_PASSWORD)
        user.foto = intent.getStringExtra(USER_FOTO)
        user.saldo = intent.getIntExtra(USER_SALDO, 0)

        val fragments: ArrayList<Fragment> = ArrayList()

        fragments.add(HomeFragment.newInstance(user))
        fragments.add(CartFragment.newInstance(user))
        fragments.add(HistoryFragment.newInstance(user))
        fragments.add(ProfileFragment.newInstance(user))

        view_pager.adapter = ViewPagerAdapter(this, fragments)

        view_pager.registerOnPageChangeCallback(object:ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                val menu = arrayOf(R.id.navigation_home, R.id.navigation_cart, R.id.navigation_history, R.id.navigation_profile)
                nav_view.selectedItemId=menu[position]
            }
        })

        nav_view.setOnNavigationItemSelectedListener {
            if(it.itemId == R.id.navigation_home) {
                view_pager.currentItem = 0
            } else if(it.itemId == R.id.navigation_cart) {
                view_pager.currentItem = 1
            } else if(it.itemId == R.id.navigation_history) {
                view_pager.currentItem = 2
            } else {
                view_pager.currentItem = 3
            }
            true
        }

        nav_drawer.setNavigationItemSelectedListener {
            if(it.itemId == R.id.navigation_home) {
                container.closeDrawer(GravityCompat.START)
                view_pager.currentItem = 0
            } else if(it.itemId == R.id.navigation_cart) {
                container.closeDrawer(GravityCompat.START)
                view_pager.currentItem = 1
            } else if(it.itemId == R.id.navigation_history) {
                container.closeDrawer(GravityCompat.START)
                view_pager.currentItem = 2
            } else if (it.itemId == R.id.navigation_profile){
                container.closeDrawer(GravityCompat.START)
                view_pager.currentItem = 3
            } else {
                val intent = Intent(this@Home, Login::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
            }
            true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home ->
                container.openDrawer(GravityCompat.START)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (container.isDrawerOpen(GravityCompat.START)) {
            container.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    companion object {
        const val USER_ID = "extra_id"
        const val USER_NAMA = "extra_nama"
        const val USER_EMAIL = "extra_email"
        const val USER_SALDO = "extra_saldo"
        const val USER_FOTO = "extra_foto"
        const val USER_PASSWORD = "extra_password"
    }
}

