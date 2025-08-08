package com.example.ballgame

import android.os.Bundle
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.ballgame.Song
import androidx.compose.foundation.clickable
import androidx.compose.ui.platform.LocalContext
import android.content.Intent
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.compose.material3.Button
import android.content.Context
import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log

class MainActivity2 : ComponentActivity() {
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            Log.d("MainActivity2", "Service connected")
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            Log.d("MainActivity2", "Service disconnected")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Text("Hello MainActivity2")
        }
        val intent = Intent(this, MusicService::class.java)
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)

        // 权限判断
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // 请求权限
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                1
            )
        } else {
            // 已有权限 → 加载 UI
            loadUI()
        }
    }

    // 单独的方法：加载 UI
    private fun loadUI() {
        val songs = getAllSongs(this)
        setContent {
            MaterialTheme {
                SongListScreen(songs)
            }
        }
    }

    // 权限回调
    @Suppress("DEPRECATION")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            loadUI()
        }
    }

    // 音乐列表 UI
@Composable
fun SongListScreen(songs: List<Song>) {
    val context = LocalContext.current

    LazyColumn {
        items(songs) { song ->
            Text(
                text = "${song.title} - ${song.artist}",
                modifier = Modifier
                    .clickable {
                        val intent = Intent(context, MusicService::class.java)
                        intent.putExtra("SONG_PATH", song.data)
                        context.startService(intent)
                        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
                    }
                    .padding(16.dp)
            )
        }
    }

        Button(onClick = {
            context.sendBroadcast(Intent(MusicService.ACTION_PLAY))
        }) {
            Text("播放")
        }

        Button(onClick = {
            context.sendBroadcast(Intent(MusicService.ACTION_PAUSE))
        }) {
            Text("暂停")
        }

        Button(onClick = {
            context.sendBroadcast(Intent(MusicService.ACTION_STOP))
        }) {
            Text("停止")
        }
}
}
