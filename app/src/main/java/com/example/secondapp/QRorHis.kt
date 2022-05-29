package com.example.secondapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qror_his)

        val exitQR = findViewById<Button>(R.id.exit_QR)
        val his = findViewById<TextView>(R.id.his)
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
                        Log.e("var ", document.data["borrow_tm"].toString())
                        his.append(transformNotify(document))
                        his.append("\n\n")
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
    }

    private fun transformEmail(email:String):String {
        var reform:String = ""
        for (i in 0..(email.split(".", "@").toTypedArray().size-2)){
            reform = reform.plus(email.split(".", "@").toTypedArray()[i]).plus("_")
        }
        reform = reform.plus(email.split(".", "@").toTypedArray()[email.split(".", "@").toTypedArray().size-1])
        return reform
    }

    private fun transformNotify(data:QueryDocumentSnapshot):String {
        var reform:String = ""

        var reformBrtime:String = "Thời gian mượn: "
        val time = data.data["borrow_tm"].toString().split("-")
        reformBrtime = reformBrtime.plus("${time[2]}/${time[1]}/${time[0]} lúc ${time[3]}h:${time[4]}\n")

        var reformRttime:String = "Thời gian trả: "
        var time_ = data.data["return_tm"].toString()
        if (time_.length > 1) {
            time_ = time_.split("-").toString()
            reformRttime = reformRttime.plus("${time_[2]}/${time_[1]}/${time_[0]} lúc ${time_[3]}h:${time_[4]}\n")
        }
        else{
            reformRttime = reformRttime.plus("Chưa hoàn trả\n")
        }

        reform = reform.plus(reformBrtime + reformRttime +
                "Thiết bị mượn: \n" +
                "${if (data.data["hdmi_wire"].toString() == "Borrowed"){"               01 Dây nối HDMI\n"}else{""}}" +
                "${if (data.data["ac_remote"].toString() == "Borrowed"){"               01 Điều khiển điều hòa\n"}else{""}}" +
                "${if (data.data["mcr_phone"].toString() == "Borrowed"){"               01 Míc di động\n"}else{""}}" +
                "${if (data.data["laser_pen"].toString() == "Borrowed"){"               01 Bút Laser\n"}else{""}}")

        Log.e("", "${time[2]}/${time[1]}/${time[0]} lúc ${time[3]}h:${time[4]}\n")
        return reform
    }

}