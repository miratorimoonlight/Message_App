package com.moonlightkim.message_app.LoginStuff

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.moonlightkim.message_app.Messages.LatestMessagesActivity
import com.moonlightkim.message_app.Model.User
import com.moonlightkim.message_app.R
import kotlinx.android.synthetic.main.activity_register.*
import java.util.*

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        supportActionBar?.hide()

        auth = FirebaseAuth.getInstance()

        register_button.setOnClickListener {
            registerUser()
        }

        alr_have_acc_text_view.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        profile_pic_button.setOnClickListener{
            //pick an image (from new activity) and return the data back to register activity
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            Log.d("RegisterActivity", "Ey ke ah ng $intent")
            //requestCode can be
            startActivityForResult(intent, 1)
        }
    }

    var selectedPhotoUri:Uri? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //process data if available
        if(requestCode == 1 && resultCode == Activity.RESULT_OK && data != null)
        {
            selectedPhotoUri = data.data
            // get the bitmap version of the DATA
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)
            image_view_new_message.setImageBitmap(bitmap)
            profile_pic_button.alpha = 0f
        }
    }

    private fun registerUser(){
        val email = email_edit_text.text.toString()
        val password = password_edit_text.text.toString()
        Log.d("RegisterActivity", "email==== $email")

        if(email.isEmpty() || password.isEmpty() || selectedPhotoUri == null) {
            Toast.makeText(baseContext, "Profile Picture / Email / Password cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        //register user
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if(!task.isSuccessful){
                    Log.d("RegisterActivity", "${task.exception}")
                    Toast.makeText(baseContext, "Authentication failed. Plesae check your email and password or Internet Connection.", Toast.LENGTH_SHORT).show()
                    return@addOnCompleteListener
                }

                Log.d("RegisterActivity", "Successful sign up")
                uploadImageToFireBaseStorage()
            }
    }

    private fun uploadImageToFireBaseStorage(){
        val filename = UUID.randomUUID().toString()

        val firebaseStorage = FirebaseStorage.getInstance()
        val ref = firebaseStorage.getReference("/profilepics/$filename")

        if(selectedPhotoUri != null)
            ref.putFile(selectedPhotoUri!!)
                .addOnSuccessListener {
                    Log.d("RegisterActivity", "successfully uploaded photo ${it.metadata?.path}")

                    ref.downloadUrl
                        .addOnSuccessListener {
                            Log.d("RegisterActivity", "File Location: ${it}")
                            saveUserDataToFireBaseDB(it.toString())
                        }
                }
    }

    private fun saveUserDataToFireBaseDB(profilePicUrl: String){
        //if uid != null, returns it, else returns ""
        val uid = auth.uid ?: ""
        val dbRef = FirebaseDatabase.getInstance().getReference("/users/$uid")

        val user = User(uid, user_name_edit_text.text.toString(), profilePicUrl)

        dbRef.setValue(user)
            .addOnSuccessListener {
                Log.d("registerActivity", "User data is saved to db")

                //start LatestMessagesActivity
                val intent = Intent(this, LatestMessagesActivity::class.java)
                //to clear all login plus register screen (use plus sign kor ban)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }

    }

    override fun onDestroy() {
        Log.d("Destroy Activity", "RegisterActivity destroyed")
        super.onDestroy()

    }

}
