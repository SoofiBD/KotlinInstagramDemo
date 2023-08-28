package com.example.kotlininstagramdemo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.kotlininstagramdemo.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root // get the root view
        setContentView(view)

        auth = Firebase.auth
        val currentUser = auth.currentUser

        if (currentUser != null){
            val intent = Intent(this, FeedActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    fun signinclick(view: View){

        val email = binding.mailText.text.toString()
        val password = binding.passwordText.text.toString()

        if(email.isNotEmpty() && password.isNotEmpty()){
            auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    val intent = Intent(this, FeedActivity::class.java)
                    startActivity(intent)
                    finish()
                }.addOnFailureListener { exception ->
                    Toast.makeText(this , exception.localizedMessage, Toast.LENGTH_LONG).show()
                }
        }


    }

    fun signupclick(view: View){
        val email = binding.mailText.text.toString()
        val password = binding.passwordText.text.toString()

        if (email.isNotEmpty() && password.isNotEmpty()){

            auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    val intent = Intent(this, FeedActivity::class.java)
                    startActivity(intent)
                    finish()
                }.addOnFailureListener { exception ->
                        Toast.makeText(this , exception.localizedMessage, Toast.LENGTH_LONG).show()
                        //Toast.makeText(this , "Enter your E-mail and Password", Toast.LENGTH_LONG).show()
                }
                    /*if (exception != null){
                        auth.signInWithEmailAndPassword(email, password)
                            .addOnSuccessListener {
                                val intent = Intent(this, FeedActivity::class.java)
                                startActivity(intent)
                                finish()
                            }.addOnFailureListener { exception ->
                                // if the user is already registered, then sign in
                                if (exception != null){
                                    Toast.makeText(this , exception.localizedMessage, Toast.LENGTH_LONG).show()
                                }
                            }
                    }*/

        }
    }


}