package com.example.stocktracker.ui

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.stocktracker.R
import com.example.stocktracker.ui.main.StockFragment
import io.paperdb.Paper

class MainActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        Paper.init(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById<Toolbar>(R.id.toolbar_main)
        setSupportActionBar(toolbar)

        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
        if(currentFragment==null){
            val fragment = StockFragment()
            supportFragmentManager.beginTransaction().add(R.id.fragment_container, fragment).commit()
        }
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportActionBar?.setDisplayHomeAsUpEnabled(supportFragmentManager.backStackEntryCount > 1);
            supportFragmentManager.popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                supportFragmentManager?.popBackStack()
                supportActionBar?.setDisplayHomeAsUpEnabled(false)
                supportActionBar?.title = "Stock"
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}