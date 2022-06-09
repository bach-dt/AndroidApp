package com.example.secondapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.api.Distribution
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import java.lang.String.format
import java.lang.String.valueOf
import kotlin.math.log


class QRorHis : AppCompatActivity() {

    val FStore : FirebaseFirestore = FirebaseFirestore.getInstance()

    @SuppressLint("InflateParams")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qror_his)
        val Linear = findViewById<LinearLayout>(R.id.list)
        val exitQR = findViewById<ImageButton>(R.id.exit_QR)
        val intent:Intent = getIntent()
        val mail:String = intent.getStringExtra(Login.EMAIL_NAME).toString()

//        mA.addValueEventListener(object : ValueEventListener {
//            @SuppressLint("SetTextI18n")
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                var count:Int = 0
//                var number_of_data_to_show:Int = 10 //---------------------------------------------//
//                for (snap:DataSnapshot in dataSnapshot.children) {
//                    count += 1
//
//                    val value:String = valueOf(snap.value)
//                    Log.e("our value", value)
//                    if (count >= dataSnapshot.childrenCount + 1 - number_of_data_to_show) {
//                        his.append(transformNotify(value))
//                        his.append("\n\n")
//                    }
//                }
//            }
//            override fun onCancelled(databaseError: DatabaseError) {}
//        })

        var count:Int = 0
        FStore.collection("History").document(transformEmail(mail))
            .collection("EquipmentState").get().addOnSuccessListener { documents ->
                for (document in documents) {
                    count += 1
                    Log.e("var: ", document.data.toString())
                }
                var count_:Int = 0
                val number_of_data_to_show:Int = 10 //--------------------------------------------//
                for (document in documents) {
                    count_ += 1
                    if (count_ >= count - 1 - number_of_data_to_show && count_ < count) {
                        Log.e("var ", document.data["return_tm"].toString())
//                        his.append(transformNotify(document))
//                        his.append("\n\n")
                        val view = layoutInflater.inflate(R.layout.insert_his, null, false)
                        val borrow_tm = view.findViewById<TextView>(R.id.borrow_tm)
                        val return_tm = view.findViewById<TextView>(R.id.return_tm)
                        val ac = view.findViewById<CheckBox>(R.id.ac)
                        val mic = view.findViewById<CheckBox>(R.id.mic)
                        val laser = view.findViewById<CheckBox>(R.id.laser)
                        val hdmi = view.findViewById<CheckBox>(R.id.hdmi)

                        val time = document.data["borrow_tm"].toString().split("-")
                        val reformBrtime = "${time[2]}/${time[1]}/${time[0]} ${time[3]}h:${time[4]}"

                        val time_ = document.data["return_tm"].toString()
                        if (time_.length > 5) {
                            val tm = time_.split("-")
                            val reformRttime = "${tm[2]}/${tm[1]}/${tm[0]} ${tm[3]}h:${tm[4]}"
                            return_tm.text = reformRttime
                        }
                        else{
                            val reformRttime = "chưa hoàn trả"
                            return_tm.text = reformRttime
                        }
                        borrow_tm.text = reformBrtime

                        ac.isChecked = document.data["ac_remote"] == "Borrowed"
                        hdmi.isChecked = document.data["hdmi_wire"] == "Borrowed"
                        laser.isChecked = document.data["laser_pen"] == "Borrowed"
                        mic.isChecked = document.data["mcr_phone"] == "Borrowed"

                        (view.parent as? ViewGroup)?.removeView(view)
                        Linear.addView(view)
                    }
                }
            }

        exitQR.setOnClickListener{
            val intent_:Intent = Intent(this, Interface::class.java)
            intent_.putExtra(Login.EMAIL_NAME, mail)
            startActivity(intent_)
        }

        Toast.makeText(applicationContext, mail, Toast.LENGTH_SHORT).show()

        val QR = findViewById<ImageButton>(R.id.QR)
        QR.setOnClickListener{
            val intent_:Intent = Intent(this, SelectEquipment::class.java)
            intent_.putExtra(Login.EMAIL_NAME, mail)
            startActivity(intent_)
        }
//--------------------------------------------------------------------------------------------------

    }

    private fun transformEmail(email:String):String {
        var reform:String = ""
        for (i in 0..(email.split(".", "@").toTypedArray().size-2)){
            reform = reform.plus(email.split(".", "@").toTypedArray()[i]).plus("_")
        }
        reform = reform.plus(email.split(".", "@").toTypedArray()[email.split(".", "@").toTypedArray().size-1])
        return reform
    }

}