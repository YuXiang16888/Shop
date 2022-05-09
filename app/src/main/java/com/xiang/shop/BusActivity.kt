package com.xiang.shop

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.annotations.SerializedName
import com.xiang.shop.databinding.ActivityBusBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class BusActivity : AppCompatActivity() {
    val TAG = BusActivity::class.java.simpleName
    lateinit var busActivityBinding : ActivityBusBinding
    val okhttpClient = OkHttpClient()
    val retrofit = Retrofit.Builder()
        .baseUrl("https://data.tycg.gov.tw/opendata/datalist/datasetMeta/")
        .client(okhttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    var bus : Bus? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        busActivityBinding = ActivityBusBinding.inflate(layoutInflater)
        setContentView(busActivityBinding.root)
        handleSSLForHttp3()
        getBusData()
    }

    inner class BusAdapter : RecyclerView.Adapter<BusViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BusViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.row_bus, parent, false)
            val holder = BusViewHolder(view)
            return holder
        }

        override fun onBindViewHolder(holder: BusViewHolder, position: Int) {
            holder.holderBinding(bus?.busItem!!.get(position))
        }

        override fun getItemCount(): Int {
            val size = bus?.busItem?.size?:0
            return size
        }

    }

    inner class BusViewHolder(view : View) : RecyclerView.ViewHolder(view) {
        val busId = view.findViewById<TextView>(R.id.tv_busid)
        val routeId = view.findViewById<TextView>(R.id.tv_routeid)
        val speed = view.findViewById<TextView>(R.id.tv_speed)
        fun holderBinding(busItem : BusItem) {
            busId.text = busItem.BusID
            routeId.text = busItem.RouteID
            speed.text = busItem.Speed
        }
    }

    fun getBusData() {
        GlobalScope.launch (Dispatchers.IO) {
            bus = retrofit.create(BusService::class.java)
                .listBus()
                .execute()
                .body()
            bus?.busItem?.forEach {
                Log.d(TAG, "${it.BusID} ${it.RouteID} ${it.Speed}")
            }

            launch (Dispatchers.Main) {
                val busAdapter = BusAdapter()
                busAdapter.setHasStableIds(true)
                busActivityBinding.recyclerBus.layoutManager = LinearLayoutManager(this@BusActivity)
                busActivityBinding.recyclerBus.adapter = busAdapter
            }
        }
    }

    fun handleSSLForHttp3() {
        try {
            val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
                override fun checkClientTrusted(p0: Array<out X509Certificate>?, p1: String?) {
                }

                override fun checkServerTrusted(p0: Array<out X509Certificate>?, p1: String?) {
                }

                override fun getAcceptedIssuers(): Array<X509Certificate>? {
                    return null
                }
            })
            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, SecureRandom())

            val hostnameVerifier = HostnameVerifier { s, sslSession ->  true }
            val workerClassName="okhttp3.OkHttpClient"
            val workClass = Class.forName(workerClassName)
            val hostnameVerifierField = workClass.getDeclaredField("hostnameVerifier")
            hostnameVerifierField.isAccessible = true
            hostnameVerifierField.set(okhttpClient, hostnameVerifier)
            val sslSocketFactory = workClass.getDeclaredField("sslSocketFactory")
            sslSocketFactory.isAccessible = true
            sslSocketFactory.set(okhttpClient, sslContext.socketFactory)
        } catch (ignore : Exception) {

        }
    }
}

class Bus(@SerializedName("datas")val busItem: List<BusItem>)

data class BusItem(
    val Azimuth: String,
    val BusID: String,
    val BusStatus: String,
    val DataTime: String,
    val DutyStatus: String,
    val GoBack: String,
    val Latitude: String,
    val Longitude: String,
    val ProviderID: String,
    val RouteID: String,
    val Speed: String,
    val ledstate: String,
    val sections: String
)

interface BusService {
    @GET("download?id=b3abedf0-aeae-4523-a804-6e807cbad589&rid=bf55b21a-2b7c-4ede-8048-f75420344aed")
    fun listBus() : Call<Bus>
}

