package com.example.order_with.Ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.order_with.R

class VoiceStartActivity : AppCompatActivity() {
    var startVoice = "음성 인식 모드 입니다. 모든 음성은 효과음 발생 이후 말씀해주세요. 메뉴를 듣고 싶으면 메뉴판, 주문하고자 하시면 주문을 말해 주세요."


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_voice_start)
    }
}
