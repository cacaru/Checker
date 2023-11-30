package com.example.checker

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment


class MainActivity : AppCompatActivity() {

    lateinit var navController: NavController

    lateinit var sf : SharedPreferences;
    lateinit var editor : SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        sf = getSharedPreferences("pref", Activity.MODE_PRIVATE)
        editor = sf.edit()
        val theme_val = sf.getInt("theme", 0)
        when(theme_val) {
            0 -> setTheme(R.style.Base_Theme_Checker_Default)
            1 -> setTheme(R.style.Base_Theme_Checker_Pastel)
            2 -> setTheme(R.style.Base_Theme_Checker_Sky)
            3 -> setTheme(R.style.Base_Theme_Checker_Green)
            4 -> setTheme(R.style.Base_Theme_Checker_Black)
            5 -> setTheme(R.style.Base_Theme_Checker_RedBlack)
            6 -> setTheme(R.style.Base_Theme_Checker_GreenBlack)
            7 -> setTheme(R.style.Base_Theme_Checker_BlueBlack)
            8 -> setTheme(R.style.Base_Theme_Checker_PurpleBlack)
            9 -> setTheme(R.style.Base_Theme_Checker_Wood)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment;
        navController = navHostFragment.navController;

    }
}