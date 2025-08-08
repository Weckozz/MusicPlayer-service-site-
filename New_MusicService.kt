package com.example.ballgame

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.MediaPlayer
import android.os.IBinder
import android.util.Log
import android.os.Build

class MusicService : Service() {

    private var mediaPlayer: MediaPlayer? = null

    companion object {
        const val ACTION_PLAY = "com.example.ballgame.PLAY"
        const val ACTION_PAUSE = "com.example.ballgame.PAUSE"
        const val ACTION_STOP = "com.example.ballgame.STOP"
    }

    // 广播接收器
    private val musicControlReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                ACTION_PLAY -> mediaPlayer?.start()
                ACTION_PAUSE -> mediaPlayer?.pause()
                ACTION_STOP -> {
                    mediaPlayer?.stop()
                    stopSelf()
                }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("MusicService", "onCreate: Service created")

        val filter = IntentFilter().apply {
            addAction(ACTION_PLAY)
            addAction(ACTION_PAUSE)
            addAction(ACTION_STOP)
        }

            registerReceiver(musicControlReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val path = intent?.getStringExtra("SONG_PATH")
        if (path != null) {
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer().apply {
                setDataSource(path)
                prepare()
                start()
            }
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        Log.d("MusicService", "onBind: Client bound")
        return null
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.d("MusicService", "onUnbind: Client unbound")
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        Log.d("MusicService", "onDestroy: Service destroyed")
        unregisterReceiver(musicControlReceiver) // 注销广播
        mediaPlayer?.release()
        super.onDestroy()
    }
}
