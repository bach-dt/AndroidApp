package com.example.secondapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class Interface : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_interface)

        val intent:Intent = getIntent()
        val mail:String = intent.getStringExtra(Login.EMAIL_NAME).toString()

        val name_mail = findViewById<TextView>(R.id.name_mail)

        val FStore : FirebaseFirestore = FirebaseFirestore.getInstance()
        val account_ = FStore.collection("RFID")
            .whereEqualTo("email", transformEmail(mail))
            .get().addOnSuccessListener { documents ->
                for (document in documents){
                    Log.e("id ", (document.data["name"]).toString())
                    name_mail.text = (document.data["name"]).toString()
                }
            }

        val QR_btn = findViewById<ImageButton>(R.id.QR_btn)
        QR_btn.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, QRorHis::class.java)
            intent.putExtra(Login.EMAIL_NAME, mail)
            startActivity(intent)
        })

        val logout = findViewById<ImageButton>(R.id.logout)
        logout.setOnClickListener(View.OnClickListener {
            val intent:Intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        })
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