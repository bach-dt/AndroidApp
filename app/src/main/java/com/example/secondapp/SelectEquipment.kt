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
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class SelectEquipment : AppCompatActivity() {

    companion object {
        val BITS_TAKED:String = "com.example.application.example.BITS_TAKED"
        val LAST_STATE:String = "com.example.application.example.LAST_STATE"
        val LAST_CHECK:String = "com.example.application.example.LAST_CHECK"
        val BORROWTIME:String = "com.example.application.example.BORROWTIME"
        val RETURNTIME:String = "com.example.application.example.RETURNTIME"
        val AC_REMOTE:String = "com.example.application.example.AC_REMOTE"
        val HDMI_WIRE:String = "com.example.application.example.HDMI_WIRE"
        val LASER_PEN:String = "com.example.application.example.LASER_PEN"
        val MCR_PHONE:String = "com.example.application.example.MCR_PHONE"
        val BORROWNOTE:String = "com.example.application.example.BORROWNOTE"
        val RETURNNOTE:String = "com.example.application.example.RETURNNOTE"
        val _SUBJECT_:String = "com.example.application.example._SUBJECT_"
        val TEACHROOM:String = "com.example.application.example.TEACHROOM"
        val PERIOD_TM:String = "com.example.application.example.PERIOD_TM"

    }

    @SuppressLint("NewApi", "SetTextI18n", "WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_equipment)

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

        val intent_:Intent = getIntent()
        val mail:String = intent_.getStringExtra(Login.EMAIL_NAME).toString()

        val ac_remote = findViewById<ImageButton>(R.id.ac_romote)
        val hdmi = findViewById<ImageButton>(R.id.hdmi)
        val laser_pen = findViewById<ImageButton>(R.id.laser_pen)
        val micro = findViewById<ImageButton>(R.id.micro)

        val ac_remote_cb = findViewById<CheckBox>(R.id.ac_romote_cb)
        val hdmi_cb = findViewById<CheckBox>(R.id.hdmi_cb)
        val laser_pen_cb = findViewById<CheckBox>(R.id.laser_pen_cb)
        val micro_cb = findViewById<CheckBox>(R.id.micro_cb)

        val exit__ = findViewById<ImageButton>(R.id.exit__)
        val pre_text = findViewById<TextView>(R.id.pre_text)
        val notify = findViewById<TextView>(R.id.notify)
        val br_btn = findViewById<Button>(R.id.br_btn)

        val borrow_time = findViewById<TextView>(R.id.borrow_time)

        pre_text.visibility = View.INVISIBLE
        notify.visibility = View.INVISIBLE

        borrow_time.visibility = View.INVISIBLE

        exit__.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, QRorHis::class.java)
            intent.putExtra(Login.EMAIL_NAME, mail)
            startActivity(intent)
        })

        Toast.makeText(applicationContext, transformEmail(mail), Toast.LENGTH_SHORT).show()

        val FStore : FirebaseFirestore = FirebaseFirestore.getInstance()
        FStore.collection("History")
            .document(transformEmail(mail))
            .collection("EquipmentState")
            .document("Last").get().addOnSuccessListener {
                if (it.exists()) {
                    val lastState:String = it["LastState"].toString()
                        if (lastState == "Borrowed") {
                            pre_text.visibility = View.VISIBLE
                            notify.visibility = View.VISIBLE

                            borrow_time.visibility = View.VISIBLE

                            FStore.collection("History")
                                .document(transformEmail(mail))
                                .collection("EquipmentState")
                                .document("Last")
                                .get().addOnSuccessListener {
                                if (it.exists()) {
                                    val bits:String = it["Bits_AHLM"].toString()
                                    pre_text.text = "Bạn đã mượn"
                                    notify.text = (if (bits[0] == '1'){"01 điều khiển điều hòa\n"}else{""}) +
                                            (if (bits[1] == '1'){"01 dây cáp HDMI\n"}else{""}) +
                                            (if (bits[2] == '1'){"01 bút Laser\n"}else{""}) +
                                            if (bits[3] == '1'){"01 míc di động"}else{""}

                                    ac_remote_cb.isChecked = bits[0] == '1'
                                    hdmi_cb.isChecked = bits[1] == '1'
                                    laser_pen_cb.isChecked = bits[2] == '1'
                                    micro_cb.isChecked = bits[3] == '1'

                                    ac_remote_cb.isEnabled = false
                                    hdmi_cb.isEnabled = false
                                    laser_pen_cb.isEnabled = false
                                    micro_cb.isEnabled = false

                                    val time = it["LastCheck"].toString().split("-")
                                    val br_time = "${time[2]}/${time[1]}/${time[0]} lúc ${time[3]}h:${time[4]}"

                                    borrow_time.text = "Bạn đã mượn vào ngày $br_time"


                                }
                            }
                            br_btn.text = "Hoàn trả thiết bị"
                        } else {

                            br_btn.text = "Mượn thiết bị"
                        }
                    Log.e("", lastState)
                }
            }

        ac_remote.setOnClickListener {
            ac_remote_cb.isChecked = !ac_remote_cb.isChecked
        }

        hdmi.setOnClickListener {
            hdmi_cb.isChecked = !hdmi_cb.isChecked
        }

        laser_pen.setOnClickListener {
            laser_pen_cb.isChecked = !laser_pen_cb.isChecked
        }
        micro.setOnClickListener {
            micro_cb.isChecked = !micro_cb.isChecked
        }

        br_btn.setOnClickListener {
            if (br_btn.text == "Hoàn trả thiết bị") {
                Return_act()
            } else {
                Borrow_act()
            }
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

        Toast.makeText(applicationContext, "Xác nhận hoàn trả thiết bị!", Toast.LENGTH_SHORT).show()

        val intent = Intent(this, MainTab::class.java)
        intent.putExtra(RETURNTIME, time)
        intent.putExtra(LAST_STATE, "Returned")
        intent.putExtra(Login.EMAIL_NAME, mail)
        startActivity(intent)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    fun Borrow_act() {

        val intent_:Intent = intent
        val mail:String = intent_.getStringExtra(Login.EMAIL_NAME).toString()

        val ac_remote_cb = findViewById<CheckBox>(R.id.ac_romote_cb)
        val hdmi_cb = findViewById<CheckBox>(R.id.hdmi_cb)
        val laser_pen_cb = findViewById<CheckBox>(R.id.laser_pen_cb)
        val micro_cb = findViewById<CheckBox>(R.id.micro_cb)

        val pre_text = findViewById<TextView>(R.id.pre_text)

        pre_text.text=""

        val ac_text:String
        val hdmi_text:String
        val micro_text:String
        val lsr_text:String

        val ac_bit:Int
        val hdmi_bit:Int
        val micro_bit:Int
        val lsr_bit:Int

        if (ac_remote_cb.isChecked){
            ac_text= "Borrowed"
            ac_bit = 1
        }else{
            ac_text = "_"
            ac_bit = 0
        }

        if (hdmi_cb.isChecked){
            hdmi_text = "Borrowed"
            hdmi_bit = 1
        }else{
            hdmi_text = "_"
            hdmi_bit = 0
        }

        if (micro_cb.isChecked){
            micro_text = "Borrowed"
            micro_bit = 1
        }else{
            micro_text = "_"
            micro_bit = 0
        }

        if (laser_pen_cb.isChecked){
            lsr_text = "Borrowed"
            lsr_bit = 1
        }else{
            lsr_text = "_"
            lsr_bit = 0
        }
        val bitsTaked = "${ac_bit}${hdmi_bit}${lsr_bit}${micro_bit}"
        if (bitsTaked == "0000") {
            Toast.makeText(applicationContext, "Bạn chưa chọn thiết bị nào!", Toast.LENGTH_SHORT)
                .show()
            ////////////////////////////
        }else {
//            val now = LocalDateTime.now()
//            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm")
//            val time: String =now.format(formatter)
            val c:Calendar = Calendar.getInstance()
            val time = "${c.get(Calendar.YEAR)}-${timeForm(c.get(Calendar.MONTH)+1)}" +
                    "-${timeForm(c.get(Calendar.DATE))}" +
                    "-${timeForm(c.get(Calendar.HOUR_OF_DAY))}" +
                    "-${timeForm(c.get(Calendar.MINUTE))}"
            Toast.makeText(
                applicationContext, "Xác nhận mượn thiết bị! $time", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, MainTab::class.java)

            intent.putExtra(BORROWTIME, time)
            intent.putExtra(RETURNTIME, "_")

            var period_ = ""
            var teachroom = ""
            var subject_ = ""
            intent.putExtra(_SUBJECT_, "")
            intent.putExtra(TEACHROOM, "")
            intent.putExtra(PERIOD_TM, "")
            intent.putExtra(BORROWTIME, time)

            intent.putExtra(BITS_TAKED, bitsTaked)
            intent.putExtra(LAST_STATE, "Borrowed")
            intent.putExtra(AC_REMOTE, ac_text)
            intent.putExtra(HDMI_WIRE, hdmi_text)
            intent.putExtra(LASER_PEN, lsr_text)
            intent.putExtra(MCR_PHONE, micro_text)
            intent.putExtra(Login.EMAIL_NAME, mail)
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
