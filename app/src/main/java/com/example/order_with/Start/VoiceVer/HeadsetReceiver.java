package com.example.order_with.Start.VoiceVer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.widget.Toast;

public class HeadsetReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        AudioManager audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        if(intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)){
            int state = intent.getIntExtra("state", -1);
            switch (state){
                case 0:
                    Toast.makeText(context, "이어폰 해지 ", Toast.LENGTH_LONG).show();
                    break;
                case 1:
                    Toast.makeText(context, "이어폰 연결 ", Toast.LENGTH_LONG).show();
                    startVoiceActivity(context);
                    break;
            }//이어폰 중간에 해지,연결에도 계속 상태 출력
        }
    }

    public void startVoiceActivity(Context context) {
        Intent intent = new Intent(context, VoiceMenu.class);
        context.startActivity(intent);
    }
}
