package com.sonicdutch.portal

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

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


        
        tv_call.setOnClickListener {

            var builder = AlertDialog.Builder(this)

            var poplayout = layoutInflater.inflate(R.layout.popup_menu,null)

            var call_tv : TextView=poplayout.findViewById(R.id.tv_as_call)
            var send_tv : TextView=poplayout.findViewById(R.id.tv_as_send)

            call_tv.setOnClickListener {

                Toast.makeText(this,"테스트",Toast.LENGTH_SHORT).show()
            }

            send_tv.setOnClickListener {

                Toast.makeText(this,"테스트",Toast.LENGTH_SHORT).show()
            }


            builder.setView(poplayout)
            builder.create()
            builder.show()
        }



    }
}