package com.example.stocktracker.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.stocktracker.R
import com.example.stocktracker.ui.main.StockFragment
import io.paperdb.Paper

class MainActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        Paper.init(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
        if(currentFragment==null){
            val fragment = StockFragment()
            supportFragmentManager.beginTransaction().add(R.id.fragment_container, fragment).commit()
        }
    }

}