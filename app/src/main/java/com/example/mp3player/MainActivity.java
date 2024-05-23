package com.example.mp3player;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private SeekBar seekBar;
    private TextView artistNameTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listView = findViewById(R.id.musicListView);
        String[] musicItems = {"one_two_three_four_five", "die", "kick_bass"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, musicItems);
        listView.setAdapter(adapter);

        seekBar = findViewById(R.id.seekBar);
        artistNameTextView = findViewById(R.id.musicComposerTextView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedMusic = (String) parent.getItemAtPosition(position);
                playMusic(selectedMusic);
                updateSeekBar();
                setMusicDuration(selectedMusic);
            }
        });
    }

    private void playMusic(String musicName) {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }

        int musicResId = getMusicResIdByName(musicName);
        mediaPlayer = MediaPlayer.create(this, musicResId);
        mediaPlayer.start();
        updateArtistName();
    }

    private void updateSeekBar() {
        seekBar.setMax(mediaPlayer.getDuration());

        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                seekBar.setProgress(mediaPlayer.getCurrentPosition());
                handler.postDelayed(this, 1000);
            }
        };
        handler.postDelayed(runnable, 1000);
    }

    private void updateArtistName() {
        String artistName = "BGMPRESIDENT";
        artistNameTextView.setText(artistName);
    }

    private void setMusicDuration(String musicTitle) {
        try {
            AssetFileDescriptor afd = getResources().openRawResourceFd(getMusicResIdByName(musicTitle));
            MediaPlayer tempMediaPlayer = new MediaPlayer();
            tempMediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            tempMediaPlayer.prepare();
            int duration = tempMediaPlayer.getDuration();
            tempMediaPlayer.release();
            afd.close();

            int minutes = duration / 1000 / 60;
            int seconds = (duration / 1000) % 60;
            String durationString = String.format("%02d:%02d", minutes, seconds);

            TextView durationTextView = findViewById(R.id.musicDurationTextView);
            durationTextView.setText(durationString);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int getMusicResIdByName(String musicName) {
        return getResources().getIdentifier(musicName, "raw", getPackageName());
    }
}