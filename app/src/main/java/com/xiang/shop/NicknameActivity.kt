package com.xiang.shop

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.xiang.shop.databinding.ActivityNicknameBinding

class NicknameActivity : AppCompatActivity() {
    lateinit var nicknameActivityBinding : ActivityNicknameBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        nicknameActivityBinding = ActivityNicknameBinding.inflate(layoutInflater)
        setContentView(nicknameActivityBinding.root)
        nicknameActivityBinding.btnDone.setOnClickListener {
            val nickName = nicknameActivityBinding.edNickname.text.toString()
            setNickname(nickName)
            FirebaseDatabase.getInstance()
                .getReference("users")
                .child(FirebaseAuth.getInstance().currentUser!!.uid)
                .child("nickname")
                .setValue(nickName)
            setResult(RESULT_OK)
            finish()
        }

    }
}