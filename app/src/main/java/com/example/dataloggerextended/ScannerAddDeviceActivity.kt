package com.example.dataloggerextended

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import com.example.dataloggerextended.databinding.ActivityDevicesBinding
import com.example.dataloggerextended.databinding.ActivityScannerAddDeviceBinding
import com.example.dataloggerextended.ui.view.AddDeviceActivity
import com.example.dataloggerextended.ui.view.DevicesActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ScannerAddDeviceActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private val dbDevices = Firebase.firestore.collection("devices")
    private val dbDevicesUsers = Firebase.firestore.collection("users")
    private lateinit var codeScanner: CodeScanner
    private lateinit var binding: ActivityScannerAddDeviceBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanner_add_device)

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance()

        // Cambiar el color del SupportActionBar
        supportActionBar!!.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.light_black)))
        supportActionBar!!.title = "Scan new device"

        //Agrego un boton de regreso al MainActivity y cambio el titulo del action bar
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        binding = ActivityScannerAddDeviceBinding.inflate(layoutInflater)
        setContentView(binding.root)


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 123)
        }else{
            startScanning()
        }

    }

    private fun startScanning() {
        val scannerView: CodeScannerView = binding.scannerView
        codeScanner = CodeScanner(this, scannerView)
        codeScanner.camera = CodeScanner.CAMERA_BACK
        codeScanner.formats = CodeScanner.ALL_FORMATS

        codeScanner.autoFocusMode = AutoFocusMode.SAFE
        codeScanner.scanMode = ScanMode.SINGLE
        codeScanner.isAutoFocusEnabled = true
        codeScanner.isFlashEnabled = false

        codeScanner.decodeCallback = DecodeCallback {
            runOnUiThread {

                var list = it.text.split(" ")
                if (list.size > 1) {

                    val devId = list[0]
                    val devCode = list[1]

                    if (devId.isNotEmpty() && devCode.isNotEmpty()) {

                        dbDevicesUsers.document(firebaseAuth.currentUser!!.email!!)
                            .collection("linked_devices").document(devId)
                            .get().addOnSuccessListener {
                                if (it.data.isNullOrEmpty()) {
                                    dbDevices.document(devId).get()
                                        .addOnSuccessListener { document ->
                                            if (document.data == null) {
                                                Toast.makeText(
                                                    this,
                                                    "Couldn't find device",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                val intent =
                                                    Intent(this, DevicesActivity::class.java)
                                                startActivity(intent)
                                            } else {
                                                if (document.data!!.containsValue(devCode)) {
                                                    Toast.makeText(
                                                        this,
                                                        "Set up your new device!",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                    val intent =
                                                        Intent(this, AddDeviceActivity::class.java)
                                                    val extras = Bundle()
                                                    extras.putString("device", devId)
                                                    extras.putString(
                                                        "model",
                                                        document.data!!["model"].toString()
                                                    )
                                                    intent.putExtra("extras", extras)
                                                    startActivity(intent)
                                                } else {
                                                    Toast.makeText(
                                                        this,
                                                        "Couldn't find device",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                    val intent =
                                                        Intent(this, DevicesActivity::class.java)
                                                    startActivity(intent)

                                                }
                                            }
                                        }
                                        .addOnFailureListener {
                                            Toast.makeText(
                                                this,
                                                "Ups, something went wrong. Try again!",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            val intent = Intent(this, DevicesActivity::class.java)
                                            startActivity(intent)
                                        }

                                } else {
                                    Toast.makeText(
                                        this,
                                        "You already have this device!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    val intent = Intent(this, DevicesActivity::class.java)
                                    startActivity(intent)
                                }
                            }.addOnFailureListener { e ->
                                Log.w(
                                    ContentValues.TAG,
                                    "Error Reading document",
                                    e
                                )
                            }
                    } else {
                        Toast.makeText(this, "Couldn't find device", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, DevicesActivity::class.java)
                        startActivity(intent)
                    }

                }else{
                    Toast.makeText(this, "Couldn't find device", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, DevicesActivity::class.java)
                    startActivity(intent)
                }
            }

        }

        codeScanner.errorCallback = ErrorCallback {
            runOnUiThread {
                Toast.makeText(this, "Camera initialization error: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }

        scannerView.setOnClickListener {
            codeScanner.startPreview()
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 123){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Camera permission granted", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (::codeScanner.isInitialized){
            codeScanner?.startPreview()
        }
    }

    override fun onPause() {
        if (::codeScanner.isInitialized){
            codeScanner?.releaseResources()
        }
        super.onPause()
    }
}