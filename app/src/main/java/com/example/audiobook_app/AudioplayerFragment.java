package com.example.audiobook_app;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.core.util.Pair;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.audiobook_app.databinding.FragmentAudioplayerBinding;

import java.util.concurrent.TimeUnit;

//Roko Kaulecko
public class AudioplayerFragment extends Fragment {

    private FragmentAudioplayerBinding binding;
    private MediaPlayer mediaPlayer; // Declare MediaPlayer object
    private int[] audioFiles = { R.raw.music, R.raw.music2, R.raw.music3}; // Example audio files
    private int currentTrack = 0; // Index of the current audio track
    private boolean isPlaying = true; // Flag to track audio playback state
    private SeekBar seekBar; // SeekBar for audio progress
    private Runnable updateSeekBar; // Runnable for updating SeekBar progress
    private TextView lastListenedFileTextView;
    private TextView lastListenedTimestampTextView;
    private TextView currentTimeTextView;
    private TextView remainingTimeTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAudioplayerBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        lastListenedFileTextView = view.findViewById(R.id.lastListenedFile);
        lastListenedTimestampTextView = view.findViewById(R.id.lastListenedTimestamp);
//        Pair<String, Long> lastListened = getLastListened(getContext());
//        lastListenedFileTextView.setText(lastListened.first);
//        lastListenedTimestampTextView.setText(String.valueOf(lastListened.second));
        currentTimeTextView = view.findViewById(R.id.currentTime);
        remainingTimeTextView = view.findViewById(R.id.remainingTime);
        return view;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize MediaPlayer with the first audio track
        mediaPlayer = MediaPlayer.create(requireContext(), audioFiles[currentTrack]);

        // Initialize SeekBar
        seekBar = binding.seekBar;

        // Set SeekBar max value to the duration of the audio track
        seekBar.setMax(mediaPlayer.getDuration());

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null) {
                    int mCurrentPosition = mediaPlayer.getCurrentPosition(); // In milliseconds
                    int mRemainingPosition = mediaPlayer.getDuration(); // In milliseconds

                    // Convert the positions from milliseconds to minutes and seconds
                    String currentTime = String.format("%02d:%02d",
                            TimeUnit.MILLISECONDS.toMinutes(mCurrentPosition),
                            TimeUnit.MILLISECONDS.toSeconds(mCurrentPosition) % 60);
                    String remainingTime = String.format("%02d:%02d",
                            TimeUnit.MILLISECONDS.toMinutes(mRemainingPosition),
                            TimeUnit.MILLISECONDS.toSeconds(mRemainingPosition) % 60);

                    // Update the TextViews
                    currentTimeTextView.setText(currentTime);
                    remainingTimeTextView.setText(remainingTime);

                    // Update the SeekBar
                    //seekBar.setProgress(mCurrentPosition / 1000);
                }
                handler.postDelayed(this, 1000);
            }
        }, 0);

        // Set click listener for stop button
        binding.stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPlaying) {
                    mediaPlayer.pause(); // Pause the audio playback
                    binding.stop.setImageResource(android.R.drawable.ic_media_play);
                } else {
                    mediaPlayer.start(); // Resume the audio playback
                    binding.stop.setImageResource(android.R.drawable.ic_media_pause);
                }
                isPlaying = !isPlaying; // Toggle the playback state
            }
        });

        // Set click listener for skip button
        binding.skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Check if there are more audio tracks available
                if (currentTrack < audioFiles.length - 1) {
                    // Save the current audio file and its timestamp before moving to the next track
                    saveLastListened(getContext(), getResources().getResourceEntryName(audioFiles[currentTrack]), mediaPlayer.getCurrentPosition());
                    Pair<String, Long> lastListened = getLastListened(getContext());
                    lastListenedFileTextView.setText("Last Listened File: " + lastListened.first);
                    long minutes = TimeUnit.MILLISECONDS.toMinutes(lastListened.second);
                    long seconds = TimeUnit.MILLISECONDS.toSeconds(lastListened.second) % 60;
                    lastListenedTimestampTextView.setText("Last Stopped At: " + String.format("%02d:%02d", minutes, seconds));

                    currentTrack++; // Move to the next track
                    playAudio(); // Play the new audio track
                }
            }
        });

        // Set click listener for back button
        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Check if it's possible to go back to the previous audio track
                if (currentTrack > 0) {
                    saveLastListened(getContext(), getResources().getResourceEntryName(audioFiles[currentTrack]), mediaPlayer.getCurrentPosition());
                    Pair<String, Long> lastListened = getLastListened(getContext());
                    lastListenedFileTextView.setText("Last Listened File: " + lastListened.first);
                    long minutes = TimeUnit.MILLISECONDS.toMinutes(lastListened.second);
                    long seconds = TimeUnit.MILLISECONDS.toSeconds(lastListened.second) % 60;
                    lastListenedTimestampTextView.setText("Last Stopped At: " + String.format("%02d:%02d", minutes, seconds));

                    currentTrack--; // Move to the previous track
                    playAudio(); // Play the new audio track
                }
            }
        });

        // Update SeekBar progress while audio is playing
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                seekBar.setProgress(0);
                seekBar.setMax(mp.getDuration());
                mp.start();
            }
        });

        // Update SeekBar progress during audio playback
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (currentTrack < audioFiles.length - 1) {
                    currentTrack++;
                    playAudio(); // Play the next audio track
                }
            }
        });

        // Set SeekBar change listener to update audio playback position
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // Initialize Runnable for updating SeekBar progress
        updateSeekBar = new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null) {
                    int currentPosition = mediaPlayer.getCurrentPosition();
                    seekBar.setProgress(currentPosition);
                }
                // Update SeekBar progress every second
                seekBar.postDelayed(this, 1000);
            }
        };

        // Start audio playback
        playAudio();
    }

    private void playAudio() {
        mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer = MediaPlayer.create(requireContext(), audioFiles[currentTrack]);
        mediaPlayer.start();

        // Update SeekBar progress
        getActivity().runOnUiThread(updateSeekBar);
    }


    @Override
    public void onPause() {
        super.onPause();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mediaPlayer != null) {
            mediaPlayer.release(); // Release the MediaPlayer resources
        }
        // Remove the callbacks to stop updating the SeekBar progress
        seekBar.removeCallbacks(updateSeekBar);
        binding = null;
    }

    /**
     * Save the last listened audio file and its timestamp
     * @param context Application context
     * @param fileName Name of the audio file
     * @param timestamp Timestamp of the last listened audio file
     */
    public void saveLastListened(Context context, String fileName, long timestamp) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("LastListened", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("FileName", fileName);
        editor.putLong("Timestamp", timestamp);
        editor.apply();
    }

    /**
     * Get the last listened audio file and its timestamp
     * @param context Application context
     * @return Pair containing the name of the audio file and its timestamp
     */
    public Pair<String, Long> getLastListened(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("LastListened", Context.MODE_PRIVATE);
        String fileName = sharedPreferences.getString("FileName", "");
        long timestamp = sharedPreferences.getLong("Timestamp", 0);
        return new Pair<>(fileName, timestamp);
    }


}
