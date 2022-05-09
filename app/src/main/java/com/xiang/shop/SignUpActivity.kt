package com.xiang.shop

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.xiang.shop.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {
    private val TAG = SignUpActivity::class.java.simpleName
    private lateinit var signupActivityBinding:ActivitySignUpBinding
    private val nicknameActivityLaunch = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        val resultCode = it.resultCode
        if(resultCode == Activity.RESULT_OK) {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        signupActivityBinding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(signupActivityBinding.root)
        signupActivityBinding.btnSignup.setOnClickListener {
            val strEmail = signupActivityBinding.edEmail.text.toString()
            val strPassword = signupActivityBinding.editPassword.text.toString()
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(strEmail,strPassword)
                .addOnCompleteListener {
                    if(it.isSuccessful()) {
                        AlertDialog.Builder(this)
                            .setTitle("Sign Up")
                            .setMessage("Account created")
                            .setPositiveButton("OK"){dialog, which->
                                setResult(Activity.RESULT_OK)
                                val intent = Intent(this, NicknameActivity::class.java)
                                nicknameActivityLaunch.launch(intent)
                            }.show()
                    } else {
                        AlertDialog.Builder(this)
                            .setTitle("Sign Up")
                            .setMessage(it.exception?.message)
                            .setPositiveButton("OK", null)
                            .show()
                    }
                }
        }
    }
}