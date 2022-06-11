package com.example.secondapp

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import java.util.jar.Manifest
import kotlin.collections.HashMap

private const val CAMERA_REQUEST_CODE = 1888

class ScanDevice : AppCompatActivity() {

    companion object {
        val AC_REMOTE_ID:String = "com.example.application.example.AC_REMOTE_"
        val HDMI_WIRE_ID:String = "com.example.application.example.HDMI_WIRE_"
        val LASER_PEN_ID:String = "com.example.application.example.LASER_PEN_"
        val MCR_PHONE_ID:String = "com.example.application.example.MCR_PHONE_"
        val DEVICE_ID:String = "com.example.application.example.DEVICE_ID"
    }

    private lateinit var codeScanner: CodeScanner
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_tab)

        val intent_:Intent = intent
        val mail:String = intent_.getStringExtra(Login.EMAIL_NAME).toString()

        setupPermissions()
        startScanning()

        val exit2 = findViewById<Button>(R.id.exit2)
        exit2.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, SelectEquipment::class.java)
            intent.putExtra(Login.EMAIL_NAME, mail)
            startActivity(intent)
        })


    }

    private fun setupPermissions() {
        val permission = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.CAMERA
        )

        if (permission != PackageManager.PERMISSION_GRANTED) {
            makeRequest()
        }
    }

    private fun startScanning() {
        val scannerView: CodeScannerView = findViewById(R.id.scanner_view)
        codeScanner = CodeScanner(this, scannerView)
        codeScanner.camera = CodeScanner.CAMERA_BACK
        codeScanner.formats = CodeScanner.ALL_FORMATS

        codeScanner.autoFocusMode = AutoFocusMode.SAFE
        codeScanner.scanMode = ScanMode.CONTINUOUS
        codeScanner.isAutoFocusEnabled = true
        codeScanner.isFlashEnabled = false

        codeScanner.decodeCallback = DecodeCallback {
            runOnUiThread {
                Toast.makeText(this, it.text, Toast.LENGTH_SHORT).show()
                if (it.text.length == 4) {

                    val intent_ = Intent(this, SelectEquipment::class.java)
                    val intent = intent
                    val mail  = intent.getStringExtra(Login.EMAIL_NAME)
                    val ac_remote = intent.getStringExtra(SelectEquipment.AC_REMOTE)
                    val hdmi_wire = intent.getStringExtra(SelectEquipment.HDMI_WIRE)
                    val laser_pen = intent.getStringExtra(SelectEquipment.LASER_PEN)
                    val mcr_phone = intent.getStringExtra(SelectEquipment.MCR_PHONE)

                    val deviceID = it.text.toString()

                    intent_.putExtra(Login.EMAIL_NAME, mail)
                    intent_.putExtra(DEVICE_ID, deviceID)
                    intent_.putExtra(AC_REMOTE_ID, ac_remote)
                    intent_.putExtra(HDMI_WIRE_ID, hdmi_wire)
                    intent_.putExtra(LASER_PEN_ID, laser_pen)
                    intent_.putExtra(MCR_PHONE_ID, mcr_phone)
                    startActivity(intent_)

                } else {
                    Toast.makeText(this, "Quét mã không thành công!", Toast.LENGTH_SHORT).show()
                }
            }
        }
        codeScanner.errorCallback = ErrorCallback {
            runOnUiThread {
                Toast.makeText(this, "Camera error: ${it.message}", Toast.LENGTH_SHORT).show()

            }
        }

        scannerView.setOnClickListener {
            codeScanner.startPreview()
        }
    }

    private fun makeRequest() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.CAMERA),
            CAMERA_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CAMERA_REQUEST_CODE -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(
                        this,
                        "You need the camera permission to able to use this app",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    //success
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (::codeScanner.isInitialized) {
            codeScanner.releaseResources()
        }
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }
}



