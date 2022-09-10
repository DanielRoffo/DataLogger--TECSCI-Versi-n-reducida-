package com.example.dataloggerextended.ui.view

import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import com.example.dataloggerextended.R
import com.example.dataloggerextended.databinding.ActivityShopBinding

class ShopActivity : AppCompatActivity() {

    lateinit var webView: WebView
    private lateinit var binding: ActivityShopBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop)

        // Cambiar el color del SupportActionBar
        supportActionBar!!.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.light_black)))
        supportActionBar!!.title = "TECSCI - Online Shop"

        //Agrego un boton de regreso al MainActivity y cambio el titulo del action bar
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        binding = ActivityShopBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setWebView()

    }

    private fun setWebView(){

        webView = binding.webView
        webView.loadUrl("https://tecsci.com.ar/shop/")
        webView.webViewClient = WebViewClient()
        webView.settings.javaScriptEnabled = true
    }

    override fun onBackPressed() {
        if (webView.canGoBack()){
            webView.goBack()
        }else{
            super.onBackPressed()
        }
    }
}