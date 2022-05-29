package com.example.secondapp

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.util.regex.Matcher
import java.util.regex.Pattern

class Login : AppCompatActivity() {
    companion object {
        val EMAIL_NAME:String = "com.example.application.example.EMAIL_NAME"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val Mail = findViewById<EditText>(R.id.Mail)
        val Pass = findViewById<EditText>(R.id.Pass)
        val Bar = findViewById<ProgressBar>(R.id.Bar)


        var mAuth: FirebaseAuth
        Bar.visibility = View.INVISIBLE

        val signin_btn = findViewById<Button>(R.id.Signin)
        signin_btn.setOnClickListener(View.OnClickListener {

            var checknumber:Int = 0
            val mail:String = Mail.text.toString()
            val pass:String = Pass.text.toString()

            if (TextUtils.isEmpty(mail)){
                Mail.setError("Không được bỏ trống!")
                checknumber += 1
            }
            if (TextUtils.isEmpty(pass)){
                Pass.setError("Không được bỏ trống!")
                checknumber += 1
            }

            if (checknumber == 0) {
                Bar.visibility = View.VISIBLE
                var account = FirebaseDatabase.getInstance()
                mAuth = FirebaseAuth.getInstance()
                var mAuth_ = FirebaseDatabase.getInstance()
                mAuth.signInWithEmailAndPassword(mail, pass).addOnCompleteListener(this){ task ->
                    if (task.isSuccessful){
                        val intent = Intent(this, Interface::class.java)
                        intent.putExtra(EMAIL_NAME, mail)
                        startActivity(intent)
                    }
                    else{
                        Toast.makeText(applicationContext, "Tài khoản không tồn tại hoặc sai mật khẩu!", Toast.LENGTH_SHORT).show()
                        Bar.visibility = View.INVISIBLE
                    }
                }
            }
        })

        val register_btn = findViewById<Button>(R.id.Regist)
        register_btn.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
        })

        val exit_ = findViewById<Button>(R.id.exit_)
        exit_.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, MainActivity::class.java)
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