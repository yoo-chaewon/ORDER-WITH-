package com.example.order_with.Ui

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.order_with.Adapter.MenuAdapter
import com.example.order_with.Data.Menu
import com.example.order_with.R
import kotlinx.android.synthetic.main.activity_recipt.*
import java.util.*

class ReciptActivity : AppCompatActivity(), TextToSpeech.OnInitListener {
    private var tts: TextToSpeech? = null
    internal var startVoice: String = ""
    internal var delayHandler: Handler? = null

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS){
            val result = tts!!.setLanguage(Locale.KOREAN)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){
                Log.e("TTS", "The Language specified is not supported!")
            }else{
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    makeVoice(startVoice)
                } else {
                    @Suppress("DEPRECATION")
                    tts!!.speak(startVoice, TextToSpeech.QUEUE_FLUSH, null)
                }
            }
        }else{
            Log.e("TTS", "Initialization Failed!")
        }
    }

    fun makeVoice(voice: String) {
        tts = TextToSpeech(this, object : TextToSpeech.OnInitListener {
            override fun onInit(status: Int) {
                if (status != TextToSpeech.ERROR) {
                    tts!!.setLanguage(Locale.KOREAN)
                    tts!!.setSpeechRate(1.toFloat())
                    tts!!.speak(startVoice, TextToSpeech.QUEUE_FLUSH, null)
                }
            }
        })
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipt)

        val layoutManager = LinearLayoutManager(this)
        rv_receipt.setLayoutManager(layoutManager)

        val intent = intent
        val menuList: ArrayList<Menu>?
        menuList = intent.getParcelableArrayListExtra<Menu>("clickedItem")

        val voice1 = "결제창에는"
        val voice2 = "가 있습니다. 총액은"
        val voice3 = "원 입니다."
        var menu = " "
        var sum = 0
        for (i in menuList.indices) {
            menu += menuList[i].name
            sum += Integer.parseInt(menuList[i].price)
        }
        startVoice = voice1 + menu + voice2 + sum + voice3

        val adapter = MenuAdapter(menuList)
        adapter.notifyItemInserted(0)
        rv_receipt.setAdapter(adapter)

        val tv_sum = findViewById(R.id.tv_receiptsum) as TextView
        tv_sum.append(sum.toString())

    }

    override fun onResume() {
        super.onResume()
        makeVoice(startVoice)
        delayHandler = Handler()
        delayHandler!!.postDelayed(Runnable {
            //StartSTT();
        }, 8000)
    }

    override fun onPause() {
        super.onPause()
        tts!!.stop()
        tts!!.shutdown()
        delayHandler!!.removeMessages(0)
    }
}
