package com.xiang.shop

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.SyncStateContract
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.security.Permission
import java.util.jar.Manifest

class ContactActivity : AppCompatActivity() {
    val TAG = ContactActivity::class.java.simpleName
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact)
        val permission = ContextCompat.checkSelfPermission(
            this, android.Manifest.permission.READ_CONTACTS)
        if(permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.READ_CONTACTS), 168)
        } else {
            readContacts()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.d(TAG, "onRequestPermissionsResult: ${permissions[0]}")
        if(requestCode == 168) {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                readContacts()
            }
        }
    }

    private fun readContacts() {
        val contactCursor = contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI,
            null, null, null, null
        )
        while (contactCursor!!.moveToNext()) {
            val name = contactCursor.getString(
                contactCursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME)
            )
            Log.d(TAG, "onCreate: $name")
        }
    }
}