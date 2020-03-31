package com.example.order_with.Service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.widget.Toast
import com.example.order_with.Ui.VoiceStartActivity

class HeadsetReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        if (intent.action == Intent.ACTION_HEADSET_PLUG) {
            val state = intent.getIntExtra("state", -1)
            when (state) {
                0 -> Toast.makeText(context, "이어폰 해지 ", Toast.LENGTH_LONG).show()
                1 -> {
                    Toast.makeText(context, "이어폰 연결 ", Toast.LENGTH_LONG).show()
                    startVoiceActivity(context)
                }
            }//이어폰 중간에 해지,연결에도 계속 상태 출력
        }
    }

    fun startVoiceActivity(context: Context) {
        val intent = Intent(context, VoiceStartActivity::class.java)
        context.startActivity(intent)
    }
}