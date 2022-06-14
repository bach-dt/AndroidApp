package com.example.secondapp

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
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

class SelectEquipment : AppCompatActivity() {

    companion object {

        val AC_REMOTE:String = "com.example.application.example.AC_REMOTE"
        val HDMI_WIRE:String = "com.example.application.example.HDMI_WIRE"
        val LASER_PEN:String = "com.example.application.example.LASER_PEN"
        val MCR_PHONE:String = "com.example.application.example.MCR_PHONE"

        val BORROW_TM:String = "com.example.application.example.BORROWTIME"
        val RETURN_TM:String = "com.example.application.example.RETURNTIME"
        val TEACHROOM:String = "com.example.application.example.TEACHROOM"
        val PERIOD_TM:String = "com.example.application.example.PERIOD_TM"
        val _SUBJECT_:String = "com.example.application.example._SUBJECT_"

        val BITS_TAKED:String = "com.example.application.example.BITS_TAKED"
        val LAST_STATE:String = "com.example.application.example.LAST_STATE"
        val LAST_CHECK:String = "com.example.application.example.LAST_CHECK"
        val LAST_ROOM:String = "com.example.application.example.LAST_ROOM"



        var period: Array<Array<String>> = arrayOf(
            arrayOf("00-00", "06-45"),
            arrayOf("06-45", "07-30"),
            arrayOf("07-30", "08-15"),
            arrayOf("08-25", "09-10"),
            arrayOf("09-20", "10-15"),
            arrayOf("10-15", "11-00"),
            arrayOf("11-00", "11-45"),
            arrayOf("12-30", "13-15"),
            arrayOf("13-15", "14-00"),
            arrayOf("14-10", "14-55"),
            arrayOf("15-05", "15-50"),
            arrayOf("16-00", "16-45"),
            arrayOf("16-45", "17-30"),
            arrayOf("17-45", "18-30"),
            arrayOf("18-30", "19-15")
        )
    }

    @SuppressLint("NewApi", "SetTextI18n", "WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_equipment)

        val intent_:Intent = intent
        val mail:String = intent_.getStringExtra(Login.EMAIL_NAME).toString()
        var remoteID = intent_.getStringExtra(ScanDevice.AC_REMOTE_ID).toString()
        var hdmiID = intent_.getStringExtra(ScanDevice.HDMI_WIRE_ID).toString()
        var laserID = intent_.getStringExtra(ScanDevice.LASER_PEN_ID).toString()
        var microID = intent_.getStringExtra(ScanDevice.MCR_PHONE_ID).toString()

        val ac_remote_cb = findViewById<CheckBox>(R.id.ac_romote_cb)
        val hdmi_cb = findViewById<CheckBox>(R.id.hdmi_cb)
        val laser_pen_cb = findViewById<CheckBox>(R.id.laser_pen_cb)
        val micro_cb = findViewById<CheckBox>(R.id.micro_cb)

        val remote_room = findViewById<TextView>(R.id.remote_room)
        val hdmi_room = findViewById<TextView>(R.id.hdmi_room)
        val laser_room = findViewById<TextView>(R.id.laser_room)
        val micro_room = findViewById<TextView>(R.id.micro_room)

        val exit__ = findViewById<ImageButton>(R.id.exit__)
        val br_btn = findViewById<Button>(R.id.br_btn)

        val ac_cb = findViewById<CheckBox>(R.id.ac)
        val hdm_cb = findViewById<CheckBox>(R.id.hdmi_)
        val laser_cb = findViewById<CheckBox>(R.id.laser)
        val mic_cb = findViewById<CheckBox>(R.id.mic)

//        ac_remote_cb.isEnabled = false
//        hdmi_cb.isEnabled = false
//        laser_pen_cb.isEnabled = false
//        micro_cb.isEnabled = false

        val remote_frame = findViewById<ImageButton>(R.id.ac_romote)
        val hdmi_frame = findViewById<ImageButton>(R.id.hdmi)
        val laser_frame = findViewById<ImageButton>(R.id.laser_pen)
        val micro_frame = findViewById<ImageButton>(R.id.micro)

        val broken = findViewById<ImageButton>(R.id.broken)
        broken.setOnClickListener {
            val intent = Intent(this, Broken::class.java)
            intent.putExtra(Login.EMAIL_NAME, mail)
            startActivity(intent)
        }


        val scan_device = findViewById<Button>(R.id.scan_device)

        exit__.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, QRorHis::class.java)
            intent.putExtra(Login.EMAIL_NAME, mail)
            startActivity(intent)
        })

        val FStore : FirebaseFirestore = FirebaseFirestore.getInstance()

        val rightNow = Calendar.getInstance()
        val date = rightNow.get(Calendar.DAY_OF_WEEK).toString()

        val Subject = findViewById<TextView>(R.id.subject)
        val Period = findViewById<TextView>(R.id.period)
        val Room = findViewById<TextView>(R.id.room)

        val time_ :String
        val c:Calendar = Calendar.getInstance()
        if (c.get(Calendar.MINUTE) < 30){
            time_ = "${timeForm(c.get(Calendar.HOUR_OF_DAY))}-${timeForm(c.get(Calendar.MINUTE) + 30)}"}
        else {
            time_ = "${timeForm(c.get(Calendar.HOUR_OF_DAY) + 1)}-${timeForm(c.get(Calendar.MINUTE) + 30 - 60)}"
        }
        val numtoStr = mapOf("1" to "Sunday", "2" to "Monday", "3" to "Tuesday",
            "4" to "Wednesday", "5" to "Thurday", "6" to "Friday", "7" to "Saturday")

        FStore.collection("RFID")
            .whereEqualTo("email", transformEmail(mail)).get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    Log.e("", document.toString())
                    FStore.collection("RFID")
                        .document(document.id)
                        .collection("Teaching")
                        .document("schedule")
                        .collection(numtoStr[date].toString())
                        .get().addOnSuccessListener { documents ->
                            for (document in documents) {
                                val str:Int = document["period"].toString().split("-")[0].toInt()
                                val end:Int = document["period"].toString().split("-")[1].toInt()
                                val Start = period[str][0]
                                val End = period[end][1]
                                Log.e("start", str.toString())
                                Log.e("Start", Start)
                                Log.e("time", time_)
                                if (Start < time_) {
                                    if (End > time_){
                                        Room.setText(document["room"].toString())
                                        Subject.setText(document["subject"].toString())
                                        Period.setText(document["period"].toString())
                                        Log.e("subject", Subject.text.toString())
                                    }
                                }
                            }
                        }
                }
            }

        FStore.collection("History")
            .document(transformEmail(mail))
            .collection("EquipmentState")
            .document("Last").get().addOnSuccessListener {
                if (it.exists()) {
                    val lastState:String = it["LastState"].toString()
                        if (lastState == "Borrowed") {

                            remote_frame.setBackgroundColor(Color.argb(100, 245, 245, 250))
                            hdmi_frame.setBackgroundColor(Color.argb(100, 245, 245, 250))
                            laser_frame.setBackgroundColor(Color.argb(100, 245, 245, 250))
                            micro_frame.setBackgroundColor(Color.argb(100, 245, 245, 250))

                            ac_remote_cb.isEnabled = false
                            hdmi_cb.isEnabled = false
                            laser_pen_cb.isEnabled = false
                            micro_cb.isEnabled = false

                            FStore.collection("History")
                                .document(transformEmail(mail))
                                .collection("EquipmentState")
                                .document("Last")
                                .get().addOnSuccessListener {
                                if (it.exists()) {
                                    val bits:String = it["Bits_AHLM"].toString()

                                    if (bits[0] == '1'){
                                        ac_cb.isChecked = true
                                        remote_frame.setBackgroundColor(Color.WHITE)
                                        remote_room.text = it["Last_Room"].toString()
                                    }
                                    if (bits[1] == '1'){
                                        hdm_cb.isChecked = true
                                        hdmi_frame.setBackgroundColor(Color.WHITE)
                                        hdmi_room.text = it["Last_Room"].toString()
                                    }
                                    if (bits[2] == '1'){
                                        laser_cb.isChecked = true
                                        laser_frame.setBackgroundColor(Color.WHITE)
                                        laser_room.text = it["Last_Room"].toString()
                                    }
                                    if (bits[3] == '1'){
                                        mic_cb.isChecked = true
                                        micro_frame.setBackgroundColor(Color.WHITE)
                                        micro_room.text = it["Last_Room"].toString()
                                    }

                                    val his = findViewById<TextView>(R.id.borrow_tm)
                                    val a = it["LastCheck"].toString().split("-")
                                    his.text = "${a[0]}/${a[1]}/${a[2]} ${a[3]}h:${a[4]}"

                                    if (remoteID == it["Last_Room"] && remote_room.length() == 3){
                                        ac_remote_cb.isChecked = true
                                    }
                                    if (hdmiID == it["Last_Room"] && hdmi_room.length() == 3){
                                        hdmi_cb.isChecked = true
                                    }
                                    if (laserID == it["Last_Room"] && laser_room.length() == 3){
                                        laser_pen_cb.isChecked = true
                                    }
                                    if (microID == it["Last_Room"] && micro_room.length() == 3){
                                        micro_cb.isChecked = true
                                    }

                                }
                            }


                            br_btn.text = "Hoàn trả thiết bị"
                        } else {

                            ac_remote_cb.isEnabled = false
                            hdmi_cb.isEnabled = false
                            laser_pen_cb.isEnabled = false
                            micro_cb.isEnabled = false

                            val his = findViewById<TextView>(R.id.borrow_tm)
                            val c:Calendar = Calendar.getInstance()
                            val time = "${c.get(Calendar.YEAR)}/${timeForm(c.get(Calendar.MONTH)+1)}" +
                                    "/${timeForm(c.get(Calendar.DATE))}" +
                                    " ${timeForm(c.get(Calendar.HOUR_OF_DAY))}h:" +
                                    timeForm(c.get(Calendar.MINUTE))
                            his.text = time
//                            if (deviceID[3] == '1'){
//                                remoteID = deviceID.take(3)
//                            }
//                            if (deviceID[3] == '2'){
//                                hdmiID = deviceID.take(3)
//                            }
//                            if (deviceID[3] == '3'){
//                                laserID = deviceID.take(3)
//                            }
//                            if (deviceID[3] == '4'){
//                                microID = deviceID.take(3)
//                            }
                            br_btn.text = "Mượn thiết bị"
                            if (remoteID.length == 3){
                                ac_remote_cb.isChecked = true
                                ac_remote_cb.isEnabled = true
                                ac_cb.isChecked = true
                                remote_room.setText(remoteID)
                            }
                            if (hdmiID.length == 3){
                                hdmi_cb.isChecked = true
                                hdmi_cb.isEnabled = true
                                hdm_cb.isChecked = true
                                hdmi_room.setText(hdmiID)
                            }
                            if (laserID.length == 3){
                                laser_pen_cb.isChecked = true
                                laser_pen_cb.isEnabled = true
                                laser_cb.isChecked = true
                                laser_room.setText(laserID)
                            }
                            if (microID.length == 3){
                                micro_cb.isChecked = true
                                micro_cb.isEnabled = true
                                mic_cb.isChecked = true
                                micro_room.setText(microID)
                            }

                            ac_remote_cb.setOnClickListener {
                                if (!ac_remote_cb.isChecked){
                                    remote_room.text = ""
                                    ac_remote_cb.isEnabled = false
                                    ac_cb.isChecked = false
                                    remoteID = ""
                                }
                            }

                            hdmi_cb.setOnClickListener {
                                if (!hdmi_cb.isChecked){
                                    hdmi_room.text = ""
                                    hdmi_cb.isEnabled = false
                                    hdm_cb.isChecked = false
                                    hdmiID = ""
                                }
                            }

                            laser_pen_cb.setOnClickListener {
                                if (!laser_pen_cb.isChecked){
                                    laser_room.text = ""
                                    laser_pen_cb.isEnabled = false
                                    laser_cb.isChecked = false
                                    laserID = ""
                                }
                            }

                            micro_cb.setOnClickListener {
                                if (!micro_cb.isChecked){
                                    micro_room.text = ""
                                    micro_cb.isEnabled = false
                                    mic_cb.isChecked = false
                                    microID = ""
                                }
                            }
                        }
                }
            }

        br_btn.setOnClickListener {
            var num = 0
            if (br_btn.text == "Hoàn trả thiết bị") {
                if (remote_room.length() == 3){
                    if (ac_remote_cb.isChecked == false){
                        Toast.makeText(applicationContext, "Bạn chưa trả đúng thiết bị", Toast.LENGTH_SHORT).show()
                        num = 1
                    }
                }
                if (hdmi_room.length() == 3){
                    if (hdmi_cb.isChecked == false){
                        Toast.makeText(applicationContext, "Bạn chưa trả đúng thiết bị", Toast.LENGTH_SHORT).show()
                        num = 1
                    }
                }
                if (laser_room.length() == 3){
                    if (laser_pen_cb.isChecked == false){
                        Toast.makeText(applicationContext, "Bạn chưa trả đúng thiết bị", Toast.LENGTH_SHORT).show()
                        num = 1
                    }
                }
                if (micro_room.length() == 3){
                    if (micro_cb.isChecked == false){
                        Toast.makeText(applicationContext, "Bạn chưa trả đúng thiết bị", Toast.LENGTH_SHORT).show()
                        num = 1
                    }
                }
                if (num == 0){
                    Return_act()
                }
            } else {
                Borrow_act()
            }
        }

        scan_device.setOnClickListener {
            val intent = Intent(this, ScanDevice::class.java)
            intent.putExtra(Login.EMAIL_NAME, mail)
            intent.putExtra(AC_REMOTE, remoteID)
            intent.putExtra(HDMI_WIRE, hdmiID)
            intent.putExtra(LASER_PEN, laserID)
            intent.putExtra(MCR_PHONE, microID)
            startActivity(intent)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    fun Return_act() {
        val intent_:Intent = intent
        val mail:String = intent_.getStringExtra(Login.EMAIL_NAME).toString()
//        val now = LocalDateTime.now()
//        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm")
//        val time: String = now.format(formatter)

        val c:Calendar = Calendar.getInstance()
        val time = "${c.get(Calendar.YEAR)}-${timeForm(c.get(Calendar.MONTH)+1)}" +
                "-${timeForm(c.get(Calendar.DATE))}" +
                "-${timeForm(c.get(Calendar.HOUR_OF_DAY))}" +
                "-${timeForm(c.get(Calendar.MINUTE))}"

        val intent = Intent(this, MainTab::class.java)
        intent.putExtra(Login.EMAIL_NAME, mail)
        intent.putExtra(RETURN_TM, time)
        startActivity(intent)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    fun Borrow_act() {

        val intent_:Intent = intent
        val mail:String = intent_.getStringExtra(Login.EMAIL_NAME).toString()
        val deviceID = intent_.getStringExtra(ScanDevice.DEVICE_ID).toString()


        val ac_remote_cb = findViewById<CheckBox>(R.id.ac_romote_cb)
        val hdmi_cb = findViewById<CheckBox>(R.id.hdmi_cb)
        val laser_pen_cb = findViewById<CheckBox>(R.id.laser_pen_cb)
        val micro_cb = findViewById<CheckBox>(R.id.micro_cb)

        val remote_room = findViewById<TextView>(R.id.remote_room)
        val hdmi_room = findViewById<TextView>(R.id.hdmi_room)
        val laser_room = findViewById<TextView>(R.id.laser_room)
        val micro_room = findViewById<TextView>(R.id.micro_room)

        val ac_text:String
        val hdmi_text:String
        val micro_text:String
        val lsr_text:String

        val ac_bit:Int
        val hdmi_bit:Int
        val micro_bit:Int
        val lsr_bit:Int

        if (ac_remote_cb.isChecked){
            ac_text= remote_room.text.toString()
            ac_bit = 1
        }else{
            ac_text = "_"
            ac_bit = 0
        }

        if (hdmi_cb.isChecked){
            hdmi_text = hdmi_room.text.toString()
            hdmi_bit = 1
        }else{
            hdmi_text = "_"
            hdmi_bit = 0
        }

        if (micro_cb.isChecked){
            micro_text = micro_room.text.toString()
            micro_bit = 1
        }else{
            micro_text = "_"
            micro_bit = 0
        }

        if (laser_pen_cb.isChecked){
            lsr_text = laser_room.text.toString()
            lsr_bit = 1
        }else{
            lsr_text = "_"
            lsr_bit = 0
        }

        val bitsTaked = "${ac_bit}${hdmi_bit}${lsr_bit}${micro_bit}"
        if (bitsTaked == "0000") {
            Toast.makeText(applicationContext, "Bạn chưa chọn thiết bị nào!", Toast.LENGTH_SHORT).show()
        }else {
            val c:Calendar = Calendar.getInstance()
            val time = "${c.get(Calendar.YEAR)}-${timeForm(c.get(Calendar.MONTH)+1)}" +
                    "-${timeForm(c.get(Calendar.DATE))}" +
                    "-${timeForm(c.get(Calendar.HOUR_OF_DAY))}" +
                    "-${timeForm(c.get(Calendar.MINUTE))}"

            val FStore : FirebaseFirestore = FirebaseFirestore.getInstance()

            val Subject = findViewById<TextView>(R.id.subject)
            val Period = findViewById<TextView>(R.id.period)
            val Room = findViewById<TextView>(R.id.room)
            if (deviceID.length > 3) {
                Room.setText(deviceID.take(3))
            }
            val BrTime = time
            val RtTime = "_"

            Log.e("sbj", Subject.text.toString())
            val intent = Intent(this, MainTab::class.java)
            intent.putExtra(Login.EMAIL_NAME, mail)
            intent.putExtra(AC_REMOTE, ac_text)
            intent.putExtra(HDMI_WIRE, hdmi_text)
            intent.putExtra(LASER_PEN, lsr_text)
            intent.putExtra(MCR_PHONE, micro_text)
            intent.putExtra(PERIOD_TM, Period.text)
            intent.putExtra(_SUBJECT_, Subject.text)
            intent.putExtra(TEACHROOM, Room.text)
            intent.putExtra(BORROW_TM, BrTime)
            intent.putExtra(RETURN_TM, RtTime)
            intent.putExtra(LAST_STATE, "Borrowed")
            intent.putExtra(LAST_CHECK, BrTime)
            intent.putExtra(BITS_TAKED, bitsTaked)
            intent.putExtra(LAST_ROOM, Room.text)
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
