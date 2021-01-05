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
import java.util.*
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


   private fun Date_time()
    {
        val cal = Calendar.getInstance()
        year = cal.get(Calendar.YEAR).toString()
        month = (cal.get(Calendar.MONTH)+1).toString()
        day = cal.get(Calendar.DATE).toString()
        time = cal.get(Calendar.HOUR_OF_DAY).toString()
        minu = cal.get(Calendar.MINUTE)

        if(month!!.toInt1()<10)
            month = "0$month"

        if(day!!.toInt1()<10)
            day = "0$day"

        date_now = year+month+day


        //시간조정.
        if(minu!! <30)
        {
            time_now = String.format("%02d", time!!.toInt1() - 1)+"30"
        }

        else
        {
            time_now = String.format("%02d", time!!.toInt1())+minu
        }

    }


    //현 위치
    var locationmanger : LocationManager? = null
    val REQUEST_CODE : Int = 2
    var latitude : Int? = null
    var longitude : Int? =null
    //var currentLatLng: Location? = null



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
    private fun getCurrentLoc() : Boolean
    {
        var result:Boolean = false
        locationmanger = getSystemService(Context.LOCATION_SERVICE) as LocationManager?
        var userlocation: Location? = getLatLang()
        if(userlocation != null)
        {
            latitude = userlocation.latitude.toInt()
            longitude = userlocation.longitude.toInt()
            //Log.e("cheak", "$latitude, $longitude")

            result = true
        }

        return result
    }


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
                currentLatLng = Location("blife_provider")
                currentLatLng.latitude = 37.0
                currentLatLng.longitude = 126.0
            }

        }

        return currentLatLng      //currentLatLng가 null이 아님을 표시
    }



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
        val baseDate: Int,
        val baseTime: Int,
        val category: String,
        val fcstValue: Float
    )


    private val retrofit = Retrofit.Builder()
        .baseUrl("http://apis.data.go.kr/1360000/VilageFcstInfoService/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()



    //날씨검사. api활용하여 gson으로 파싱? 데이터 확인. (retrofit 사용)
    interface WeatherInterface
    {
        @GET("getUltraSrtFcst" + "?serviceKey=6f6aP3gCujBq5ihYIEraET%2FDgrOvkij3nVmab7ZpPzHjVHWW7wWjM%2BXY5f5yA%2B5Nve%2BPAn68P66zPX%2F9gXSVnQ%3D%3D")
        fun GetWeather(
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


        songdb = SongDatabase.songDatabase.getInstance(this@MainActivity)
        CoroutineScope(Dispatchers.Main).launch {
            songdb.songDao().getAll()
        }


        tv_home.setOnClickListener {
            var in_home = Intent(Intent.ACTION_VIEW, Uri.parse("https://sonicdutch.modoo.at/"))
            startActivity(in_home)
        }

        tv_way.setOnClickListener {

            var in_useway = Intent(Intent.ACTION_VIEW, Uri.parse("https://youtu.be/IptQbHSC4I0"))
            startActivity(in_useway)
        }


        tv_Maintenance.setOnClickListener {
            var in_as = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("http://palmcloud.co.kr/sonicdutch/view/contact_register.html")
            )
            startActivity(in_as)
        }

        tv_q_an.setOnClickListener {
            var in_qanswer = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://sonicdutch.modoo.at/?link=aa5s09di")
            )
            startActivity(in_qanswer)
        }

        tv_recipes.setOnClickListener {
            var in_recipes = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://sonicdutch.modoo.at/?link=bv2huhtg")
            )
            startActivity(in_recipes)
        }


        iv_face.setOnClickListener {
            var in_face = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://www.facebook.com/soniccoldbrew")
            )
            startActivity(in_face)
        }

        iv_instar.setOnClickListener {
            var in_instar = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://www.instagram.com/sonicdutch")
            )
            startActivity(in_instar)
        }


        iv_kakao.setOnClickListener {
            var in_kakao = Intent(Intent.ACTION_VIEW, Uri.parse("https://pf.kakao.com/_tiMxlxl"))
            startActivity(in_kakao)
        }

        iv_blog.setOnClickListener {
            var in_blog = Intent(Intent.ACTION_VIEW, Uri.parse("https://blog.naver.com/sonicdutch"))
            startActivity(in_blog)
        }


        tv_notice.setOnClickListener {

            var in_notice = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://sonicdutch.modoo.at/?link=3x3fk95u")
            )
            startActivity(in_notice)
        }


        iv_video.setOnClickListener {

            var in_video = Intent(Intent.ACTION_VIEW, Uri.parse("https://youtu.be/z_3FokLgjT4"))
            startActivity(in_video)
        }


        tv_download.setOnClickListener {

            wb_pdf.visibility = View.VISIBLE
            wb_pdf.apply {
                settings.javaScriptEnabled = true
                webViewClient = WebViewClient()
            }
            val pdf_url = "http://www.kccba.net/sonicdutch/manual.pdf"
            //val test = "http://blifestore.com/download/manual.pdf"


            wb_pdf.loadUrl("https://drive.google.com/viewerng/viewer?embedded=true&url="+pdf_url)
        }



        tv_call.setOnClickListener {

            var builder = AlertDialog.Builder(this)


            var poplayout = layoutInflater.inflate(R.layout.popup_menu, null)

            var call_tv : TextView=poplayout.findViewById(R.id.tv_as_call)
            var send_tv : TextView=poplayout.findViewById(R.id.tv_as_send)
            builder.setView(poplayout)
            var dialog: AlertDialog = builder.create()


            call_tv.setOnClickListener {

                val num = Uri.parse("tel:010-7233-0754")
                var callintent = Intent(Intent.ACTION_DIAL, num)                //전화 창으로만 가도록
                startActivity(callintent)
                dialog.dismiss()
            }

            send_tv.setOnClickListener {

                val magnum = Uri.parse("sms:010-7233-0754")        //문자창으로만 이동
                var magintent = Intent(Intent.ACTION_SENDTO, magnum)
                startActivity(magintent)
                dialog.dismiss()
            }

            dialog.show()
        }



        var weather_timesong: String? = null
        var song_id: Int? = null


        tv_song.setOnClickListener {

            if(!getCurrentLoc())
            {
                return@setOnClickListener
            }

            Date_time()

            var weather_key: Int? = null
            var time_key: Int? = null

            var now_sky: Float?
            var now_pty: Float?
            var n_s: String? = null
            var n_p: String? = null


            service.GetWeather(1, 20, "JSON", date_now!!, time_now!!, latitude.toString(), longitude.toString())
                .enqueue(object : Callback<WEATHER> {
                override fun onResponse(call: Call<WEATHER>, response: Response<WEATHER>) {
                    if (response.isSuccessful) {
                        now_pty = response.body()!!.response.body.items.item[5].fcstValue
                        n_p = response.body()!!.response.body.items.item[5].category

                        now_sky = response.body()!!.response.body.items.item[15].fcstValue
                        n_s = response.body()!!.response.body.items.item[15].category


                        //데이터 베이스에서 부를 url구별을 위한 key
                        if (now_pty!! >= 1)
                            weather_key = 2
                        else if (now_pty?.equals(0) ?: (0 == null) || now_sky!! <= 2)
                            weather_key = 0
                        else if (now_pty?.equals(0) ?: (0 == null) || now_sky!! > 2 && now_sky!! <= 4)
                            weather_key = 1

                        if (time!!.toInt1() >= 3 && time!!.toInt1() < 6)
                            time_key = 0
                        else if (time!!.toInt1() >= 6 && time!!.toInt1() < 12)
                            time_key = 1
                        else if (time!!.toInt1() >= 12 && time!!.toInt1() < 20)
                            time_key = 2
                        else if (time!!.toInt1() >= 20 && time!!.toInt1() < 3)
                            time_key = 3

                        //url을 받아와 플레이 재생파트.
                        //데이터 불러오기
                        //기본 디폴트 url = 맑은날 12시
                        weather_timesong = "http://kccba.net/M0003-1.mp3"

                        CoroutineScope(Dispatchers.Main).launch {
                            var song: Songentitiy.Song = songdb.songDao().findUrl(
                                time_key!!, weather_key!!)

                            var id: Songentitiy.Song = songdb.songDao().findid(
                                time_key!!,weather_key!!)


                            weather_timesong = song.url
                            song_id = id.id

                            Log.e("Test", "$weather_timesong  $song_id")


                            var in_musicplay = Intent(this@MainActivity, MusicPlayActivity::class.java)
                            in_musicplay.putExtra("url","$weather_timesong")
                            in_musicplay.putExtra("id","${song_id.toString()}")

                            startActivity(in_musicplay)
                        }
                    }
                }

                override fun onFailure(call: Call<WEATHER>, t: Throwable) {
                    Log.d("api", t.message)
                }

            })
        }

    }
    //oncreat 닫는



    override fun onBackPressed() {

        if(System.currentTimeMillis() - backPressedTime >=2000 ) {
            wb_pdf.visibility = View.INVISIBLE
            backPressedTime = System.currentTimeMillis()
        }
        else {
            finish() //액티비티 종료
        }
    }


}
