package com.example.secondapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import org.w3c.dom.Text
import java.lang.Exception
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class Broken : AppCompatActivity() {

    companion object {

        val AC_REMOTE__:String = "com.example.application.example.AC_REMOTE__"
        val HDMI_WIRE__:String = "com.example.application.example.HDMI_WIRE__"
        val LASER_PEN__:String = "com.example.application.example.LASER_PEN__"
        val MCR_PHONE__:String = "com.example.application.example.MCR_PHONE__"

    }

    @SuppressLint("NewApi", "SetTextI18n", "WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_broken)

        val intent_:Intent = intent
        val mail:String = intent_.getStringExtra(Login.EMAIL_NAME).toString()
        var remoteID = intent_.getStringExtra(BrokenQR.AC_REMOTE_ID_).toString()
        var hdmiID = intent_.getStringExtra(BrokenQR.HDMI_WIRE_ID_).toString()
        var laserID = intent_.getStringExtra(BrokenQR.LASER_PEN_ID_).toString()
        var microID = intent_.getStringExtra(BrokenQR.MCR_PHONE_ID_).toString()
        val deviceID = intent_.getStringExtra(BrokenQR.DEVICE_ID_).toString()

        val ac_remote_cb = findViewById<CheckBox>(R.id.ac_romote_cb)
        val hdmi_cb = findViewById<CheckBox>(R.id.hdmi_cb)
        val laser_pen_cb = findViewById<CheckBox>(R.id.laser_pen_cb)
        val micro_cb = findViewById<CheckBox>(R.id.micro_cb)

        val remote_room = findViewById<TextView>(R.id.remote_room)
        val hdmi_room = findViewById<TextView>(R.id.hdmi_room)
        val laser_room = findViewById<TextView>(R.id.laser_room)
        val micro_room = findViewById<TextView>(R.id.micro_room)

        val exit__ = findViewById<ImageButton>(R.id.exit__)

//        ac_remote_cb.isEnabled = false
//        hdmi_cb.isEnabled = false
//        laser_pen_cb.isEnabled = false
//        micro_cb.isEnabled = false

        if (deviceID[3] == '1'){
            remoteID = deviceID.take(3)
        }
        if (deviceID[3] == '2'){
            hdmiID = deviceID.take(3)
        }
        if (deviceID[3] == '3'){
            laserID = deviceID.take(3)
        }
        if (deviceID[3] == '4'){
            microID = deviceID.take(3)
        }
        if (remoteID.length == 3){
            ac_remote_cb.isChecked = true
            remote_room.setText(remoteID)
        }
        if (hdmiID.length == 3){
            hdmi_cb.isChecked = true
            hdmi_room.setText(hdmiID)
        }
        if (laserID.length == 3){
            laser_pen_cb.isChecked = true
            laser_room.setText(laserID)
        }
        if (microID.length == 3){
            micro_cb.isChecked = true
            micro_room.setText(microID)
        }

        val broken = findViewById<Button>(R.id.broken)
        broken.setOnClickListener {
            val FStore : FirebaseFirestore = FirebaseFirestore.getInstance()

            val c:Calendar = Calendar.getInstance()
            val time = "${c.get(Calendar.YEAR)}-${timeForm(c.get(Calendar.MONTH)+1)}" +
                    "-${timeForm(c.get(Calendar.DATE))}" +
                    "-${timeForm(c.get(Calendar.HOUR_OF_DAY))}-" +
                    timeForm(c.get(Calendar.MINUTE))
            val ac_bit:Int
            val hdmi_bit:Int
            val micro_bit:Int
            val lsr_bit:Int

            ac_bit = if (ac_remote_cb.isChecked){
                1
            }else{
                0
            }

            hdmi_bit = if (hdmi_cb.isChecked){
                1
            }else{
                0
            }

            micro_bit = if (micro_cb.isChecked){
                1
            }else{
                0
            }

            lsr_bit = if (laser_pen_cb.isChecked){
                1
            }else{
                0
            }

            val bitsTaked = "${ac_bit}${hdmi_bit}${lsr_bit}${micro_bit}"
            if (bitsTaked == "0000") {
                Toast.makeText(applicationContext, "Bạn chưa chọn thiết bị nào!", Toast.LENGTH_SHORT).show()
            }else {
                if (ac_remote_cb.isChecked) {
                    val items = HashMap<String, String>()
                    items["detected"] = time
                    items["fix_time"] = "_"
                    items["state"] = "Thiết bị đang hỏng"
                    FStore.collection("Broken").document("ac_remote")
                        .collection("broken").document(remote_room.text.toString())
                        .set(items)
                }
                if (hdmi_cb.isChecked) {
                    val items = HashMap<String, String>()
                    items["detected"] = time
                    items["fix_time"] = "_"
                    items["state"] = "Thiết bị đang hỏng"
                    FStore.collection("Broken").document("hdmi_wire")
                        .collection("broken").document(hdmi_room.text.toString())
                        .set(items)
                }
                if (laser_pen_cb.isChecked) {
                    val items = HashMap<String, String>()
                    items["detected"] = time
                    items["fix_time"] = "_"
                    items["state"] = "Thiết bị đang hỏng"
                    FStore.collection("Broken").document("laser_pen")
                        .collection("broken").document(laser_room.text.toString())
                        .set(items)
                }
                if (micro_cb.isChecked) {
                    val items = HashMap<String, String>()
                    items["detected"] = time
                    items["fix_time"] = "_"
                    items["state"] = "Thiết bị đang hỏng"
                    FStore.collection("Broken").document("mcr_phone")
                        .collection("broken").document(micro_room.text.toString())
                        .set(items)
                }
                val intent = Intent(this, SelectEquipment::class.java)
                intent.putExtra(Login.EMAIL_NAME, mail)
                startActivity(intent)
            }
        }


        val scan_device = findViewById<Button>(R.id.scan_device)

        exit__.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, SelectEquipment::class.java)
            intent.putExtra(Login.EMAIL_NAME, mail)
            startActivity(intent)
        })

        scan_device.setOnClickListener {
            val intent = Intent(this, BrokenQR::class.java)
            intent.putExtra(Login.EMAIL_NAME, mail)
            intent.putExtra(AC_REMOTE__, remoteID)
            intent.putExtra(HDMI_WIRE__, hdmiID)
            intent.putExtra(LASER_PEN__, laserID)
            intent.putExtra(MCR_PHONE__, microID)
            startActivity(intent)
        }
    }


    private fun transformEmail(email:String): String {
        var reform:String = ""
        for (i in 0..(email.split(".", "@").toTypedArray().size-2)){
            reform = reform.plus(email.split(".", "@").toTypedArray()[i]).plus("_")
        }
        reform = reform.plus(email.split(".", "@").toTypedArray()[email.split(".", "@").toTypedArray().size-1])
        return reform
    }

    private fun timeForm(time:Int): String {
        var form = ""
        form = if (time < 10) {
            "0${time}"
        }else{
            "$time"
        }
        return form
    }

}
