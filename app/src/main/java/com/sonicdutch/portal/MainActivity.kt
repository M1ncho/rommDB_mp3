package com.sonicdutch.portal

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat


class MainActivity : AppCompatActivity() {

    //현 시각
    val date_now = SimpleDateFormat("yyyy-MM-dd")
    val time_now = SimpleDateFormat("HH")


    //현 위치
    var locationmanger : LocationManager? = null
    private val REQUEST_CODE_LOCATION : Int = 2
    var latitude : Double? = null
    var longitude : Double? =null

    private fun getCurrentLoc()
    {
        locationmanger = getSystemService(Context.LOCATION_SERVICE)as LocationManager?
        var userlocation: Location = getLatLang()
        if(userlocation!= null)
        {
            latitude = userlocation.latitude
            longitude = userlocation.longitude
            Log.d("cheak","$latitude, $longitude" )

        }
        
    }


    private fun getLatLang(): Location {

        var currentLatLng: Location? = null
        if(ActivityCompat.checkSelfPermission(applicationContext, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(applicationContext, android.Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), this.REQUEST_CODE_LOCATION)
            getLatLang()
        }
        else
        {
            val locationProvider = LocationManager.GPS_PROVIDER
            currentLatLng = locationmanger?.getLastKnownLocation(locationProvider)
        }
        return currentLatLng!!           //currentLatLng가 null이 아님을 표시
    }



    //날씨...검사




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        tv_home.setOnClickListener {
            var in_home = Intent(Intent.ACTION_VIEW, Uri.parse("https://sonicdutch.modoo.at/"))
            startActivity(in_home)
        }

        tv_way.setOnClickListener {
            var in_useway = Intent(Intent.ACTION_VIEW, Uri.parse("https://youtu.be/IptQbHSC4I0"))
            startActivity(in_useway)
        }


        tv_Maintenance.setOnClickListener {
            var in_as = Intent(Intent.ACTION_VIEW, Uri.parse("http://palmcloud.co.kr/sonicdutch/view/contact_register.html"))
            startActivity(in_as)
        }

        tv_q_an.setOnClickListener {
            var in_qanswer = Intent(Intent.ACTION_VIEW,Uri.parse("https://sonicdutch.modoo.at/?link=aa5s09di"))
            startActivity(in_qanswer)
        }


        tv_recipes.setOnClickListener {
            var in_recipes = Intent(Intent.ACTION_VIEW, Uri.parse("https://sonicdutch.modoo.at/?link=bv2huhtg"))
            startActivity(in_recipes)
        }


        iv_face.setOnClickListener {
            var in_face = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/soniccoldbrew"))
            startActivity(in_face)
        }

        iv_instar.setOnClickListener {
            var in_instar = Intent(Intent.ACTION_VIEW,Uri.parse("https://www.instagram.com/sonicdutch"))
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

            var in_notice = Intent(Intent.ACTION_VIEW, Uri.parse("https://sonicdutch.modoo.at/?link=3x3fk95u"))
            startActivity(in_notice)
        }

        iv_video.setOnClickListener {

            var in_video = Intent(Intent.ACTION_VIEW, Uri.parse("https://youtu.be/z_3FokLgjT4"))
            startActivity(in_video)
        }


        tv_download.setOnClickListener {

            var in_down = Intent(Intent.ACTION_VIEW, Uri.parse("http://palmcloud.co.kr/sonicdutch/module/manualDownload.php"))
            startActivity(in_down)
        }



        tv_call.setOnClickListener {

            var builder = AlertDialog.Builder(this)

            var poplayout = layoutInflater.inflate(R.layout.popup_menu,null)

            var call_tv : TextView=poplayout.findViewById(R.id.tv_as_call)
            var send_tv : TextView=poplayout.findViewById(R.id.tv_as_send)

            call_tv.setOnClickListener {

                val num = Uri.parse("tel:010-7233-0754")
                var callintent = Intent(Intent.ACTION_DIAL,num)                //전화 창으로만 가도록
                startActivity(callintent)
            }

            send_tv.setOnClickListener {

                val magnum = Uri.parse("sms:010-7233-0754")
                var magintent = Intent(Intent.ACTION_SENDTO,magnum)
                startActivity(magintent)

            }

            builder.setView(poplayout)
            builder.create()
            builder.show()
        }



        //데이터 베이스 내 노래??들을 재생
        tv_song.setOnClickListener {




        }









    }    //oncreat 닫는


}