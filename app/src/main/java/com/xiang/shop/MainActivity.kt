package com.xiang.shop

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xiang.shop.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val TAG = MainActivity::class.java.simpleName
    private lateinit var mainActivityBinding: ActivityMainBinding
    val functions = listOf<String>("Camera",
        "Invite friend",
        "Parking",
        "Download coupons",
        "News",
        "Movie",
        "Bus",
        "News",
        "News",
        "News",
        "News",
        "Maps")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainActivityBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainActivityBinding.root)

        setSupportActionBar(mainActivityBinding.toolbar)

        mainActivityBinding.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        val colorArray = arrayOf("Red", "Green", "Blue")
        val spinnerAdapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, colorArray)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        mainActivityBinding.layoutContentMain.spinnerColor.adapter = spinnerAdapter
        mainActivityBinding.layoutContentMain.spinnerColor
            .onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                Log.d(TAG, "onItemSelected: ${colorArray[position]}")
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }
        val functionAdapter = FunctionAdapter()
        functionAdapter.setHasStableIds(true)
        mainActivityBinding.layoutContentMain
            .recyclerFunction.layoutManager = LinearLayoutManager(this)
        mainActivityBinding.layoutContentMain
            .recyclerFunction.adapter = functionAdapter
    }

    inner class FunctionAdapter : RecyclerView.Adapter<FunctionViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FunctionViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.row_function, parent, false)
            val holder = FunctionViewHolder(view)
            return holder
        }

        override fun onBindViewHolder(holder: FunctionViewHolder, position: Int) {
            holder.nameText.text = functions[position]
            holder.itemView.setOnClickListener {
                functionClicked(holder, position)
            }
        }

        override fun getItemCount(): Int {
            return functions.size
        }

    }

    private fun functionClicked(holder: FunctionViewHolder, position: Int) {
        Log.d(TAG, "functionClicked: $position")
        when(position) {
            1 -> startActivity(Intent(this, ContactActivity::class.java))
            2 -> startActivity(Intent(this, ParkingActivity::class.java))
            5 -> startActivity(Intent(this, MovieActivity::class.java))
            6 -> startActivity(Intent(this, BusActivity::class.java))
        }
    }


    class FunctionViewHolder(view : View) : RecyclerView.ViewHolder(view) {
        var nameText = view.findViewById<TextView>(R.id.tv_name)
    }

    override fun onResume() {
        super.onResume()
        FirebaseDatabase.getInstance()
            .getReference("users")
            .child(FirebaseAuth.getInstance().currentUser?.uid.toString())
            .child("nickname")
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    mainActivityBinding.layoutContentMain.tvNickname.text = snapshot.value as String
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
    }
}