package com.maxi.audiotools.tasks;

import android.os.AsyncTask;

import com.maxi.audiotools.tools.VoiceFileUtils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Mao Jiqing on 2016/10/31.
 */

public class AudioAsyncTask extends AsyncTask<String, Void, Void> {
    private VoiceFileUtils audioFile;

    public AudioAsyncTask(VoiceFileUtils audioFile) {
        this.audioFile = audioFile;
    }

    @Override
    protected Void doInBackground(String... params) {
        try {
            URL url = new URL(params[0]); // 构建URL
            // 构造网络连接
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            // 保存音频文件
            audioFile.exists(params[0]);
            audioFile.saveFile(conn.getInputStream());
            conn.disconnect(); // 断开网络连接
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}