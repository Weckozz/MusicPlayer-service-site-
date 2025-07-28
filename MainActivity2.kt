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

class MainActivity2 : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                1
            )
            return
        }
        val songs = getAllSongs(this)
        setContent {
            MaterialTheme {
                SongListScreen(songs)
                LazyColumn {
                    items(songs) { song ->
                        Text("${song.title} - ${song.artist}")
                    }
                }
            }
        }
    }
}
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
                    }
                    .padding(16.dp)
            )
        }
    }
}
