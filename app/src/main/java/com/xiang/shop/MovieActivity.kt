package com.xiang.shop

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.xiang.shop.databinding.ActivityMovieBinding
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
import javax.net.ssl.*

class MovieActivity : AppCompatActivity() {
    val TAG = MovieActivity::class.java.simpleName
    lateinit var movieActivityBinding : ActivityMovieBinding
//    val jsonWeb = "https://myjson.dit.upm.es/api/bins/2t4v"
    var movies : ArrayList<MovieItem>? = null
    val okhttpClient = OkHttpClient()
    val retrofit = Retrofit.Builder()
        .baseUrl("https://myjson.dit.upm.es/api/bins/")
        .client(okhttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        handleSSL()
        movieActivityBinding = ActivityMovieBinding.inflate(layoutInflater)
        setContentView(movieActivityBinding.root)
        handleSSLForHttp3()
        getMovieJson()
    }

    inner class MoiveAdapter : RecyclerView.Adapter<MovieHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.row_movie, parent, false)
            val holder = MovieHolder(view)
            return holder
        }

        override fun onBindViewHolder(holder: MovieHolder, position: Int) {
            val movie = movies?.get(position)
            holder.holerBinding(movie!!)
        }

        override fun getItemCount(): Int {
            val size = movies?.size?:0
            Log.d(TAG, "getItemCount: $size")
            return size
        }

    }

    inner class MovieHolder(view : View) : RecyclerView.ViewHolder(view) {
        var movieName = view.findViewById<TextView>(R.id.tv_movie_name)
        var movieImdb = view.findViewById<TextView>(R.id.tv_moive_imdb)
        var movieDirector = view.findViewById<TextView>(R.id.tv_moive_director)
        var posterImage = view.findViewById<ImageView>(R.id.imageview_poster)
        fun holerBinding(movie : MovieItem) {
            movieName.text = movie.Title
            movieImdb.text = movie.imdbRating
            movieDirector.text = movie.Director
            Glide.with(this@MovieActivity)
                .load(movie.Images[0])
                .override(300)
                .into(posterImage)
        }
    }

    private fun getMovieJson(/*jsonWeb: String*/) {
        val jsonData = StringBuilder()
        GlobalScope.launch(Dispatchers.IO) {
//            val parkingUrl = URL(jsonWeb)
//            val connection = parkingUrl.openConnection() as HttpURLConnection
//            val bufReader = connection.getInputStream()
//                .bufferedReader()
//            var str = bufReader.readLine()
//            while (str != null) {
//                jsonData.append(str)
//                jsonData.append("\n")
//                str = bufReader.readLine()
//            }
            val movieService = retrofit.create(MovieService::class.java)
            movies = movieService.listMovies().execute().body()
            //movies = Gson().fromJson<Movie>(jsonData.toString(), Movie::class.java)
            movies?.forEach {
                Log.i(TAG, "${it.Title} ${it.imdbRating}")
            }
            launch (Dispatchers.Main) {
                val movieAdapter = MoiveAdapter()
                movieAdapter.setHasStableIds(true)
                movieActivityBinding.recyclerMovie.layoutManager = LinearLayoutManager(this@MovieActivity)
                movieActivityBinding.recyclerMovie.adapter = movieAdapter
            }
        }
    }

    fun handleSSL() {
        try {
            val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager{
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
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.socketFactory)
            val okhttpClient = OkHttpClient().newBuilder().sslSocketFactory(sslContext.getSocketFactory())
                .hostnameVerifier{ s, sslSession ->  true}.build()
            HttpsURLConnection.setDefaultHostnameVerifier{ s, sslSession ->  true}
        } catch (ignore : Exception) {

        }
    }

    fun handleSSLForHttp3() {
        try {
            val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager{
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

class Movie : ArrayList<MovieItem>()

data class MovieItem(
    val Actors: String,
    val Awards: String,
    val ComingSoon: Boolean,
    val Country: String,
    val Director: String,
    val Genre: String,
    val Images: List<String>,
    val Language: String,
    val Metascore: String,
    val Plot: String,
    val Poster: String,
    val Rated: String,
    val Released: String,
    val Response: String,
    val Runtime: String,
    val Title: String,
    val Type: String,
    val Writer: String,
    val Year: String,
    val imdbID: String,
    val imdbRating: String,
    val imdbVotes: String,
    val totalSeasons: String
)

interface MovieService {
    @GET("2t4v")
    fun listMovies() : Call<ArrayList<MovieItem>>
}

