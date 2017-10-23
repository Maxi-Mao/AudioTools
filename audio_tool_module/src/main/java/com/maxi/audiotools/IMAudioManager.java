package com.maxi.audiotools;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;

import com.maxi.audiotools.apis.DeleteListener;
import com.maxi.audiotools.tasks.AudioAsyncTask;
import com.maxi.audiotools.tools.VoiceFileUtils;

import java.io.IOException;

/**
 * Created by Mao Jiqing on 2016/10/12.
 */
public class IMAudioManager {
    private volatile static IMAudioManager imAudioManager;

    private MediaPlayer mPlayer;

    public String audioFileCache = "/audio_cache";

    private boolean isPause;

    public Context mContext;

    /**
     * 单例
     *
     * @return IMAudioManager对象
     */
    public static IMAudioManager instance() {
        if (imAudioManager == null) {
            synchronized (IMAudioManager.class) {
                if (imAudioManager == null) {
                    imAudioManager = new IMAudioManager();
                }
            }
        }
        return imAudioManager;
    }

    public void init(Context mContext){
        this.mContext = mContext;
    }

    public void playSound(String voicePath,
                                 MediaPlayer.OnCompletionListener onCompletionListener) {
        // TODO Auto-generated method stub
        if (mPlayer == null) {
            mPlayer = new MediaPlayer();
            //保险起见，设置报错监听
            mPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {

                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    // TODO Auto-generated method stub
                    mPlayer.reset();
                    return false;
                }
            });
        } else {
            mPlayer.reset();//恢复
        }
        VoiceFileUtils fileUtils = new VoiceFileUtils();
        try {
            String path = fileUtils.exists(voicePath); // 判断是否存在缓存文件
            mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mPlayer.setOnCompletionListener(onCompletionListener);
            if (path != null) { // 存在缓存文件
                mPlayer.setDataSource(path);
                mPlayer.prepare();
                mPlayer.start();
            } else { // 不存在音频缓存文件,则边存边播
                // 异步下载音频文件
                new AudioAsyncTask(fileUtils).execute(voicePath);
                mPlayer.setDataSource(voicePath);
                mPlayer.prepareAsync(); // 准备(InputStream), 异步
                mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        // 准备完成后, 开始播放音频文件
                        mp.start();
                    }
                });
            }
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    //停止函数
    public void pause() {
        if (mPlayer != null && mPlayer.isPlaying()) {
            mPlayer.pause();
            isPause = true;
        }
    }

    //继续
    public void resume() {
        if (mPlayer != null && isPause) {
            mPlayer.start();
            isPause = false;
        }
    }


    public void release() {
        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }

    public void delete(DeleteListener mDeleteListener) {
        VoiceFileUtils fileUtils = new VoiceFileUtils();
        fileUtils.recursionDeleteFile(mDeleteListener);
    }
}
