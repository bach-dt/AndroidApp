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

class MainTab : AppCompatActivity() {

    val FStore : FirebaseFirestore = FirebaseFirestore.getInstance()

    private lateinit var codeScanner: CodeScanner
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_tab)

        val intent_:Intent = getIntent()
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
        val  permission = ContextCompat.checkSelfPermission(this,
        android.Manifest.permission.CAMERA)

        if (permission != PackageManager.PERMISSION_GRANTED){
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
                if (it.text == "Hello Noer!") {
                    val intent_ = Intent(this, QRorHis::class.java)

                    val intent: Intent = getIntent()
                    val mail: String = intent.getStringExtra(Login.EMAIL_NAME).toString()

                    val Subject: String =
                        intent.getStringExtra(SelectEquipment._SUBJECT_).toString()
                    val Room: String = intent.getStringExtra(SelectEquipment.LAST_ROOM).toString()
                    val last_check: String =
                        intent.getStringExtra(SelectEquipment.LAST_CHECK).toString()
                    val bits_taked: String =
                        intent.getStringExtra(SelectEquipment.BITS_TAKED).toString()
                    val last_state: String =
                        intent.getStringExtra(SelectEquipment.LAST_STATE).toString()

                    val ac_remote: String =
                        intent.getStringExtra(SelectEquipment.AC_REMOTE).toString()
                    val hdmi_wire: String =
                        intent.getStringExtra(SelectEquipment.HDMI_WIRE).toString()
                    val laser_pen: String =
                        intent.getStringExtra(SelectEquipment.LASER_PEN).toString()
                    val mcr_phone: String =
                        intent.getStringExtra(SelectEquipment.MCR_PHONE).toString()
                    val period_: String =
                        intent.getStringExtra(SelectEquipment.PERIOD_TM).toString()
                    val teachroom: String =
                        intent.getStringExtra(SelectEquipment.TEACHROOM).toString()
                    val borrow_time: String =
                        intent.getStringExtra(SelectEquipment.BORROW_TM).toString()
                    val return_time: String =
                        intent.getStringExtra(SelectEquipment.RETURN_TM).toString()


                    if (last_state == "Borrowed") {

                        val items = HashMap<String, String>()
                        items["ac_remote"] = ac_remote
                        items["hdmi_wire"] = hdmi_wire
                        items["laser_pen"] = laser_pen
                        items["mcr_phone"] = mcr_phone
                        items["borrow_tm"] = borrow_time
                        items["return_tm"] = return_time
                        items["teachroom"] = teachroom
                        items["_subject_"] = Subject
                        items["period_tm"] = period_

                        val items_last = HashMap<String, Any>()
                        items_last["Bits_AHLM"] = bits_taked
                        items_last["LastState"] = last_state
                        items_last["LastCheck"] = last_check
                        items_last["Last_Room"] = Room

                        FStore.collection("History").document(transformEmail(mail))
                            .collection("EquipmentState").document(borrow_time).set(items)
                        FStore.collection("History").document(transformEmail(mail))
                            .collection("EquipmentState").document("Last").set(items_last)
                        Toast.makeText(this, "Quét mã thành công!", Toast.LENGTH_SHORT).show()
                        intent_.putExtra(Login.EMAIL_NAME, mail)
                        Thread.sleep(500)
                        startActivity(intent_)
                    } else {
                        FStore.collection("History").document(transformEmail(mail))
                            .collection("EquipmentState")
                            .document("Last").get().addOnSuccessListener {
                                val items = HashMap<String, Any>()
                                items["return_tm"] = return_time

                                val items_last = HashMap<String, Any>()
                                items_last["LastState"] = "Returned"
                                items_last["LastCheck"] = "_"
                                items_last["Last_Room"] = "_"
                                items_last["Bits_AHLM"] = "_"

                                FStore.collection("History").document(transformEmail(mail))
                                    .collection("EquipmentState")
                                    .document(it["LastCheck"].toString()).update(items)
                                FStore.collection("History").document(transformEmail(mail))
                                    .collection("EquipmentState").document("Last")
                                    .update(items_last)
                            }
                        Toast.makeText(this, "Quét mã thành công!", Toast.LENGTH_SHORT).show()
                        intent_.putExtra(Login.EMAIL_NAME, mail)
                        Thread.sleep(500)
                        startActivity(intent_)
                    }
                }
            }
        }
        codeScanner.errorCallback = ErrorCallback {
            runOnUiThread{
                Toast.makeText(this, "Camera error: ${it.message}", Toast.LENGTH_SHORT).show()

            }
        }

        scannerView.setOnClickListener{
            codeScanner.startPreview()
        }
    }

    private fun makeRequest(){
        ActivityCompat.requestPermissions(this,
            arrayOf(android.Manifest.permission.CAMERA),
            CAMERA_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode){
            CAMERA_REQUEST_CODE -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "You need the camera permission to able to use this app", Toast.LENGTH_SHORT).show()
                } else {
                //success
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (::codeScanner.isInitialized){
            codeScanner.releaseResources()
        }
    }
    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }

    private fun transformEmail(email:String): String {
        var reform:String = ""
        for (i in 0..(email.split(".", "@").toTypedArray().size-2)){
            reform = reform.plus(email.split(".", "@").toTypedArray()[i]).plus("_")
        }
        reform = reform.plus(email.split(".", "@").toTypedArray()[email.split(".", "@").toTypedArray().size-1])
        return reform
    }
}


