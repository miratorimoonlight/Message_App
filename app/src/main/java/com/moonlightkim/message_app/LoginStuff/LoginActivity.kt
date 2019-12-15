package com.moonlightkim.message_app.LoginStuff

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.moonlightkim.message_app.Messages.LatestMessagesActivity
import com.moonlightkim.message_app.R
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar?.hide()

        auth = FirebaseAuth.getInstance()

        login_button.setOnClickListener{
            Log.d("LoginActivity", "login clicked")
            loginUser()
        }

        create_new_account_text_view.setOnClickListener {
            //to close current activity. onDestroy is called. Like click back button. and RETURN to Register Screen
            finish()
        }
    }

    //for log in user via Firebase
    private fun loginUser(){
        val email = email_edit_text_login.text.toString()
        val password = password_edit_text_login.text.toString()

        if(email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Email/Password cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }
        //login in user
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener{
                if (!it.isSuccessful) {
                    Toast.makeText(this, "Wrong Email or Password", Toast.LENGTH_SHORT).show()
                    return@addOnCompleteListener
                }
                Log.d("LoginActivity", "Success login")

                //start LatestMessagesActivity
                val intent = Intent(this, LatestMessagesActivity::class.java)
                //to clear all login plus register screen
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK + Intent.FLAG_ACTIVITY_CLEAR_TASK
                Log.d("Destroy adfasdf", "${intent.flags}")
                startActivity(intent)
            }
    }

    override fun onDestroy() {
        Log.d("Destroy Activity", "LoginActivity destroyed")
        super.onDestroy()
    }
}