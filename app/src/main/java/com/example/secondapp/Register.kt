package com.example.secondapp

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.SignInMethodQueryResult
import com.google.firebase.database.FirebaseDatabase
import java.util.jar.Attributes
import java.util.regex.Matcher
import java.util.regex.Pattern


class Register : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val FullName = findViewById<EditText>(R.id.Name_)
        val Email = findViewById<EditText>(R.id.Email_)
        val Password = findViewById<EditText>(R.id.Password_)
        val Confirm = findViewById<EditText>(R.id.confirm_)
        val ProcessBar = findViewById<ProgressBar>(R.id.progressBar)
        val regis_btn = findViewById<Button>(R.id.register_)
        val checkBox = findViewById<CheckBox>(R.id.checkBox)
        class account(val email:String, val name:String, val password:String)

        var mAuth: FirebaseAuth

        ProcessBar.visibility = View.INVISIBLE

        checkBox.setOnClickListener{
            if(checkBox.text.equals("Hiện mật khẩu")){
                Password.transformationMethod = HideReturnsTransformationMethod.getInstance()
                Confirm.transformationMethod = HideReturnsTransformationMethod.getInstance()
                checkBox.text = "Ẩn mật khẩu"
            } else{
                Password.transformationMethod = PasswordTransformationMethod.getInstance()
                Confirm.transformationMethod = PasswordTransformationMethod.getInstance()
                checkBox.text = "Hiện mật khẩu"
            }
        }

        regis_btn.setOnClickListener {

            var checknum:Int = 0
            val email:String = Email.text.toString()
            val name:String = FullName.text.toString()
            val pw:String = Password.text.toString()
            val confirm_pw = Confirm.text.toString()

            val expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$"
            val pattern: Pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
            val matcher: Matcher = pattern.matcher(email)

            mAuth = FirebaseAuth.getInstance()

            if (TextUtils.isEmpty(email)){
                Email.setError("Không được bỏ trống!")
                checknum += 1
            } else if (!matcher.matches()){
                Email.setError("Email không hợp lệ!")
                checknum += 1
            }
            if (TextUtils.isEmpty(name)){
                FullName.setError("Không được bỏ trống!")
                checknum += 1
            }
            if (TextUtils.isEmpty(pw)){
                Password.setError("Không được bỏ trống!")
                checknum += 1
            }
            if (TextUtils.isEmpty(confirm_pw)){
                Password.setError("Không được bỏ trống!")
                checknum += 1
            }
            if (pw.length < 8 && pw.length > 0){
                Password.setError("Mật khẩu tối thiểu 8 ký tự!")
                checknum += 1
            }
            if (!confirm_pw.equals(pw)){
                Confirm.setError("Mật khẩu không khớp!")
                checknum += 1
            }

            if (checknum == 0){
                ProcessBar.visibility = View.VISIBLE
                
                mAuth.createUserWithEmailAndPassword(email, pw)
                    .addOnSuccessListener {}

                val acc = account(email, name, pw)

                val database = FirebaseDatabase.getInstance().getReference("Account")
                database.child(transformEmail(email)).setValue(acc).addOnSuccessListener {
                    Email.text.clear()
                    FullName.text.clear()
                    Password.text.clear()
                }

                val mAuth_:FirebaseDatabase = FirebaseDatabase.getInstance()
                mAuth_.getReference(transformEmail(email)).child("Last State").setValue("Returned")

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }

        val exit_btn = findViewById<Button>(R.id.exit)
        exit_btn.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        })
    }

    fun transformEmail(email:String): String {
        var reform:String = ""
        for (i in 0..(email.split(".", "@").toTypedArray().size-2)){
            reform = reform.plus(email.split(".", "@").toTypedArray()[i]).plus("_")
        }
        reform = reform.plus(email.split(".", "@").toTypedArray()[email.split(".", "@").toTypedArray().size-1])
        return reform
    }
}