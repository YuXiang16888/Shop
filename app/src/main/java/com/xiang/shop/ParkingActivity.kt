package com.xiang.shop

import android.accounts.Account
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.net.HttpURLConnection
import java.net.URL


class ParkingActivity : AppCompatActivity() {
    val TAG = ParkingActivity::class.java.simpleName
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_parking)
        val parkingWeb = "\thttps://data.tycg.gov.tw/opendata/datalist/datasetMeta/download?id=f4cc0b12-86ac-40f9-8745-885bddc18f79&rid=0daad6e6-0632-44f5-bd25-5e1de1e9146f"
        getJsonForCorutines(parkingWeb)
    }

    fun getJsonForCorutines(url : String) {
        val json = StringBuilder()
        GlobalScope.launch (Dispatchers.IO) {
            val parkingUrl = URL(url)
            val connection = parkingUrl.openConnection() as HttpURLConnection
            val bufReader = connection.getInputStream()
                .bufferedReader()
            var str = bufReader.readLine()
            while (str != null) {
                json.append(str)
                json.append("\n")
                str = bufReader.readLine()
            }
            launch (Dispatchers.Main){
                Log.d(TAG, "getJsonForCorutines: ")
                AlertDialog.Builder(this@ParkingActivity)
                    .setTitle("Json Data")
                    .setMessage("Got it")
                    .setPositiveButton("OK", {dialog, which ->
                        val gson = Gson()
                        val parkingList = gson.fromJson<Parking>(json.toString(), Parking::class.java)
                        parkingList.parkingLots.forEach {
                            Log.i(TAG, "${it.areaId} ${it.areaName} ${it.parkName} ${it.totalSpace}")
                        }
                    }).show()
            }
        }
    }
}

/*class Parking(val parkingLots : List<ParkingLot>)

data class ParkingLot(
    val areaId : String,
    val areaName : String,
    val parkName : String,
    val totalSpace : Int,
    val surplusSpace : String,
    val payGuide : String,
    val introduction : String,
    val address : String,
    val wgsX : Float,
    val wgsY : Float,
    val parkId : String
)*/

/*
data class Parking(
    val parkingLots: List<ParkingLot>
)

data class ParkingLot(
    val address: String,
    val areaId: String,
    val areaName: String,
    val introduction: String,
    val parkId: String,
    val parkName: String,
    val payGuide: String,
    val surplusSpace: String,
    val totalSpace: Int,
    val wgsX: Double,
    val wgsY: Double
){
  "parkingLots" : [ {
    "areaId" : "1",
    "areaName" : "桃園區",
    "parkName" : "桃園縣公有府前地下停車場",
    "totalSpace" : 334,
    "surplusSpace" : "17",
    "payGuide" : "停車費率:30 元/小時。停車時數未滿一小時者，以一小時計算。逾一小時者，其超過之不滿一小時部分，如不逾三十分鐘者，以半小時計算；如逾三十分鐘者，仍以一小時計算收費。",
    "introduction" : "桃園市政府管轄之停車場",
    "address" : "桃園區縣府路1號(出入口位於桃園市政府警察局前)",
    "wgsX" : 121.3011,
    "wgsY" : 24.9934,
    "parkId" : "P-TY-001"
  }
  ]
  }
* */

data class Parking(
    val parkingLots: List<ParkingLot>
)

data class ParkingLot(
    val address: String,
    val areaId: String,
    val areaName: String,
    val introduction: String,
    val parkId: String,
    val parkName: String,
    val payGuide: String,
    val surplusSpace: String,
    val totalSpace: Int,
    val wgsX: Double,
    val wgsY: Double
)