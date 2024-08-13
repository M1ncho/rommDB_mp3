package com.sonicdutch.portal

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.webkit.WebViewClient
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.*
import kotlin.text.toInt as toInt1


class MainActivity : AppCompatActivity() {

    //현 시각, calder로
    var year: String? = null
    var month: String? = null
    var day: String?= null
    var time: String?= null
    var minu: Int? =null
    var date_now: String?= null
    var time_now: String? = null


    lateinit var songdb: SongDatabase.songDatabase
    private var backPressedTime : Long = 0

    var cal: Calendar = Calendar.getInstance()
    val open_decoding = "공공 데이터 api key"

    var gson = GsonBuilder().setLenient().create()


    val RE = 6371.00877   // 지구 반경
    val GRID = 5.0        // 격자 간격
    val SLAT1 = 30.0      // 투영 위도1
    val SLAT2 = 60.0      // 투영 위도2
    val OLON = 126.0      // 기준점 경도
    val OLAT = 38.0       // 기준점 위도
    val XO = 43           // 기준점 x좌표
    val YO = 136          // 기준점 y좌표


    //현 위치
    var locationmanger : LocationManager? = null
    val REQUEST_CODE : Int = 2
    var latitude : Double? = null
    var longitude : Double? =null


    //위치 가져오기 위한 권한 처리
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode==REQUEST_CODE)
        {
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                getCurrentLoc()
            }
            else
            {
                Toast.makeText(this, "권한이 없어 실행할 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }



    //현재 위치 가져오기
    private fun getCurrentLoc() {
        locationmanger = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        var userlocation: Location? = getLatLang()

        if(ActivityCompat.checkSelfPermission(applicationContext, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(applicationContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_CODE)
        }
        else {
            var locationProvider = LocationManager.GPS_PROVIDER
            userlocation = locationmanger?.getLastKnownLocation(locationProvider)

            latitude = userlocation?.latitude
            longitude = userlocation?.longitude

            Log.d("Location CHECK ", "$latitude   $longitude")
        }
    }



    // 위치 초기화
    private fun getLatLang(): Location? {
        var currentLatLng: Location? = null

        if(ActivityCompat.checkSelfPermission(applicationContext, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(applicationContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                this.REQUEST_CODE
            )
        }
        else
        {
            var locationProvider = LocationManager.GPS_PROVIDER
            currentLatLng = locationmanger?.getLastKnownLocation(locationProvider)

            if(currentLatLng == null)
            {
                currentLatLng = Location("provider")
                currentLatLng.latitude = 37.0
                currentLatLng.longitude = 126.0
            }
        }

        return currentLatLng      //currentLatLng가 null이 아님을 표시
    }



    // 날씨를 받아오기 위한 data class
    data class WEATHER(
        val response: RESPONSE
    )

    data class RESPONSE(
        val header: HEADER,
        val body: BODY
    )

    data class HEADER(
        val resultCode: Int,
        val resultMsg: String
    )

    data class BODY(
        val datType: String,
        val items: ITEMS
    )

    data class ITEMS(
        val item: List<ITEM>
    )

    data class ITEM(
        var category: String,
        var fcstValue: Double
    )


    // retrofit 선언
    private val retrofit = Retrofit.Builder()
        .baseUrl("http://apis.data.go.kr/")
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()


    //날씨검사. api활용하여 gson으로 파싱. 데이터 확인. (retrofit 사용)
    interface WeatherInterface
    {
        @GET("1360000/VilageFcstInfoService_2.0/getVilageFcst")
        fun GetWeather(
            @Query("ServiceKey") key: String,
            @Query("pageNo") page_num: Int,
            @Query("numOfRows") num_row: Int,
            @Query("dataType") data_type: String,
            @Query("base_date") base_date: String,
            @Query("base_time") base_time: String,
            @Query("nx") nx: String,
            @Query("ny") ny: String
        ): Call<WEATHER>
    }

    val service = retrofit.create(WeatherInterface::class.java)




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // db 생성 관련
        songdb = SongDatabase.songDatabase.getInstance(this@MainActivity)
        CoroutineScope(Dispatchers.Main).launch {
            songdb.songDao().getAll()
        }


        // 현재 위치 가져오기
        getLatLang()
        getCurrentLoc()


        var weather_timesong: String? = null
        var song_id: Int? = null


        btn_move.setOnClickListener {
            var Degrad = Math.PI / 180.0
            var Raddeg = 180.0 / Math.PI

            var re = RE / GRID
            var slat1 = SLAT1 * Degrad
            var slat2 = SLAT2 * Degrad
            var olon = OLON * Degrad
            var olat = OLAT * Degrad

            var sn = tan(Math.PI * 0.25 + slat2 * 0.5) / tan(Math.PI * 0.25 + slat1 * 0.5)
            sn = ln(cos(slat1) / cos(slat2)) / ln(sn)

            var sf = tan(Math.PI * 0.25 + slat1 * 0.5)
            sf = sf.pow(sn) * Math.cos(slat1) / sn

            var ro = Math.tan(Math.PI * 0.25 + olat * 0.5)
            ro = re * sf / Math.pow(ro, sn)


            var ra = Math.tan(Math.PI * 0.25 + (latitude)!! * Degrad * 0.5)
            ra = re * sf / Math.pow(ra, sn)
            var theta = longitude!! * Degrad - olon
            if (theta > Math.PI) {
                theta -= 2.0 * Math.PI
            }
            if (theta < -Math.PI) {
                theta += 2.0 * Math.PI
            }
            theta *= sn

            var x_long = floor(ra * Math.sin(theta) + XO + 0.5).toInt()
            var y_lat = floor(ro - ra * Math.cos(theta) + YO + 0.5).toInt()


            //
            var weather_key: Int? = null
            var time_key: Int? = null

            val formatter = SimpleDateFormat("yyyyMMdd")
            val formattedDate = formatter.format(Calendar.getInstance().time)

            var time_to_key = cal.get(Calendar.HOUR_OF_DAY)
            var time = cal.get(Calendar.HOUR_OF_DAY)
            when (time) {
                2,3,4 -> time = 2
                5,6,7 -> time = 5
                8,9,10 -> time = 8
                11,12,13 -> time = 11
                14,15,16 -> time = 14
                17,18,19 -> time = 17
                20,21,22 -> time = 20
                23,24,0,1 -> time = 23
            }
            var now_time = String.format("%02d", time) + "00"


            //날씨 api 호출
            service.GetWeather(open_decoding, 1, 9, "JSON", formattedDate, now_time, x_long.toString(), y_lat.toString())
                .enqueue(object : Callback<WEATHER> {
                override fun onResponse(call: Call<WEATHER>, response: Response<WEATHER>) {

                    if (response.isSuccessful) {
                        Log.d("RETROFIT SUCCESS ", "CHECK   ${response.body()}")

                        var pty_value : Double? = null
                        var sky : String? = null
                        var sky_value: Double? = null

                        for (i in 0..8) {
                            if (response.body()!!.response.body.items.item[i].category == "SKY") {
                                sky = response.body()!!.response.body.items.item[i].category
                                sky_value = response.body()!!.response.body.items.item[i].fcstValue
                            } else if (response.body()!!.response.body.items.item[i].category == "PTY") {
                                pty_value = response.body()!!.response.body.items.item[i].fcstValue
                            }
                        }


                        //데이터 베이스에서 부를 url구별을 위한 key
                        if (pty_value!! >= 1)
                            weather_key = 2
                        else if (pty_value == 0.0 || sky_value!! < 3.0)
                            weather_key = 0
                        else if (pty_value == 0.0 || sky_value in 3.0..4.0)
                            weather_key = 1

                        if (time_to_key in 3..5)
                            time_key = 0
                        else if (time_to_key in 6..11)
                            time_key = 1
                        else if (time_to_key in 12..19)
                            time_key = 2
                        else if (time_to_key >= 20 && time_to_key in 0..2)
                            time_key = 3


                        //기본 디폴트 url = 맑은날 12시
                        weather_timesong = "http://kccba.net/M0003-1.mp3"

                        CoroutineScope(Dispatchers.Main).launch {
                            Log.d("CoroutineScope SUCCESS ", "CHECK     $weather_key  $time_key")

                            var song: Songentitiy.Song = songdb.songDao().findUrl(
                                time_key!!, weather_key!!)

                            var id: Songentitiy.Song = songdb.songDao().findid(
                                time_key!!,weather_key!!)

                            weather_timesong = song.url
                            song_id = id.id

                            var in_musicplay = Intent(this@MainActivity, MusicPlayActivity::class.java)
                            in_musicplay.putExtra("url","$weather_timesong")
                            in_musicplay.putExtra("id","${song_id.toString()}")

                            startActivity(in_musicplay)
                        }
                    }

                    else {
                        weather_key = 0
                        time_key = 1

                        Log.d("RETROFIT FAILED ", " $weather_key  $time_key")

                        CoroutineScope(Dispatchers.Main).launch {
                            var song: Songentitiy.Song = songdb.songDao().findUrl(
                                time_key!!, weather_key!!)

                            var id: Songentitiy.Song = songdb.songDao().findid(
                                time_key!!,weather_key!!)

                            weather_timesong = song.url
                            song_id = id.id


                            var in_musicplay = Intent(this@MainActivity, MusicPlayActivity::class.java)
                            in_musicplay.putExtra("url","$weather_timesong")
                            in_musicplay.putExtra("id","${song_id.toString()}")

                            startActivity(in_musicplay)
                        }
                    }
                }
                override fun onFailure(call: Call<WEATHER>, t: Throwable) {
                    Log.d("api ", "${t.message}")
                }
            })
        }

    }


    override fun onBackPressed() {
        if(System.currentTimeMillis() - backPressedTime >=1000 ) {
            wb_pdf.visibility = View.INVISIBLE
            backPressedTime = System.currentTimeMillis()
        }
        else {
            finish()
        }
    }


}
