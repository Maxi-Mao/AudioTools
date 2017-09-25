package com.maxi.audio;

import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.maxi.audiotools.IMAudioManager;

public class MainActivity extends AppCompatActivity {
    private String audioUrl = "http://abv.cn/music/光辉岁月.mp3";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        IMAudioManager.instance().init(this);

        ((Button) findViewById(R.id.start_audio)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IMAudioManager.instance().playSound(audioUrl, new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        Toast.makeText(MainActivity.this, "播放结束", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        ((Button) findViewById(R.id.pause_audio)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IMAudioManager.instance().pause();
            }
        });

        ((Button) findViewById(R.id.resume_audio)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IMAudioManager.instance().resume();
            }
        });

        ((Button) findViewById(R.id.release_audio)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IMAudioManager.instance().release();
            }
        });
    }
}
