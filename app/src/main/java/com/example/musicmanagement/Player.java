package com.example.musicmanagement;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class Player extends AppCompatActivity {
    // Khai báo các biến giao diện cho nút điều khiển và hiển thị thông tin
    ImageView play, prev, next, imageView, imgRepeat;
    TextView songTitle, txtCurrentTime, txtTotalTime; // TextView để hiển thị tiêu đề và thời gian bài hát
    SeekBar mSeekBarTime, mSeekBarVol; // SeekBar để điều khiển thời gian và âm lượng
    MediaPlayer mMediaPlayer; // MediaPlayer để phát nhạc
    private AudioManager mAudioManager; // AudioManager để quản lý âm lượng của hệ thống
    int currentIndex = 0; // Chỉ số của bài hát hiện tại trong danh sách phát
    final ArrayList<Integer> songs = new ArrayList<>(); // Danh sách các bài hát
    boolean isRepeat = false; // Cờ để kiểm tra xem chế độ lặp bài hát có được bật hay không

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        // Liên kết layout của activity với code
        Button btnRedirectSongList = findViewById(R.id.btn_show_song_list);
        btnRedirectSongList.setOnClickListener(v -> {
            Intent listIntent = new Intent(Player.this, SongListPlayerClass.class);
            startActivity(listIntent); // Khởi động Activity danh sách bài hát
        });

        Button btnRedirectHome = findViewById(R.id.btnRedirectHome);
        btnRedirectHome.setOnClickListener(v -> {
            Intent intentHome = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intentHome);
        });

        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE); // Khởi tạo AudioManager

        // Ánh xạ các view từ layout
        play = findViewById(R.id.img_play);
        prev = findViewById(R.id.img_pre);
        next = findViewById(R.id.img_next);
        imgRepeat = findViewById(R.id.img_repeat);
        songTitle = findViewById(R.id.txt_song_title);
        imageView = findViewById(R.id.imageView);
        mSeekBarTime = findViewById(R.id.seekbar1);
        mSeekBarVol = findViewById(R.id.seekbar2);
        txtCurrentTime = findViewById(R.id.txt_current_time);
        txtTotalTime = findViewById(R.id.txt_total_time);

        // Thêm các bài hát vào danh sách
        songs.add(R.raw.ecnhqmtp); // Em của ngày hôm qua - Sơn Tùng MTP
        songs.add(R.raw.kieuchi); // Anh thôi nhân nhượng Remix - Kiều Chi
        songs.add(R.raw.ltktthanhung); // Lao tâm khổ tứ Remix - Thanh Hưng
        songs.add(R.raw.vianhdaucobiet); // Vì anh đâu có biết Remix - Vũ
        songs.add(R.raw.lalung); // Lạ lùng - Vũ

        // Kiểm tra xem có chuyển bài hát từ intent trước không
        Intent intent = getIntent();
        if (intent.hasExtra("songIndex")) {
            currentIndex = intent.getIntExtra("songIndex", 0); // Nếu có, lấy chỉ số bài hát được chọn
        }

        playSong(); // Phát bài hát đầu tiên khi ứng dụng khởi động

        // Đặt sự kiện click cho các nút điều khiển
        play.setOnClickListener(v -> togglePlayPause()); // Phát hoặc tạm dừng khi bấm nút Play
        next.setOnClickListener(v -> playNextSong()); // Phát bài hát tiếp theo khi bấm nút Next
        prev.setOnClickListener(v -> playPreviousSong()); // Phát bài hát trước đó khi bấm nút Prev
        imgRepeat.setOnClickListener(v -> toggleRepeat()); // Bật/tắt chế độ lặp bài hát

        setupVolumeSeekBar(); // Thiết lập thanh điều chỉnh âm lượng
        setupTimeSeekBar(); // Thiết lập thanh điều chỉnh thời gian
    }

    // Phương thức phát bài hát
    private void playSong() {
        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop(); // Dừng bài hát hiện tại nếu đang phát
            }
            mMediaPlayer.release(); // Giải phóng MediaPlayer để chuẩn bị phát bài khác
        }

        // Tạo một MediaPlayer mới cho bài hát hiện tại và bắt đầu phát
        mMediaPlayer = MediaPlayer.create(getApplicationContext(), songs.get(currentIndex));
        mMediaPlayer.start();
        updateSongInfo(); // Cập nhật thông tin bài hát

        // Lấy tổng thời gian của bài hát và hiển thị
        int duration = mMediaPlayer.getDuration();
        txtTotalTime.setText(formatTime(duration));

        startImageRotation(); // Bắt đầu xoay ảnh khi bài hát phát
        play.setImageResource(R.drawable.ic_pause_24); // Đổi icon của nút Play thành nút Pause

        // Thiết lập sự kiện khi bài hát phát xong
        mMediaPlayer.setOnCompletionListener(mp -> {
            if (isRepeat) {
                playSong(); // Nếu chế độ lặp bật, phát lại bài hát
            } else {
                playNextSong(); // Nếu không, chuyển sang bài tiếp theo
            }
        });
    }

    // Phương thức phát bài hát tiếp theo
    private void playNextSong() {
        currentIndex = (currentIndex + 1) % songs.size(); // Tăng chỉ số bài hát
        playSong(); // Phát bài hát mới
    }

    // Phương thức phát bài hát trước đó
    private void playPreviousSong() {
        currentIndex = (currentIndex - 1 + songs.size()) % songs.size(); // Giảm chỉ số bài hát
        playSong(); // Phát bài hát mới
    }

    // Phương thức chuyển đổi giữa phát và tạm dừng
    private void togglePlayPause() {
        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.pause(); // Nếu đang phát, tạm dừng
                play.setImageResource(R.drawable.ic_play_arrow_32); // Đổi icon thành nút Play
            } else {
                mMediaPlayer.start(); // Nếu tạm dừng, tiếp tục phát
                play.setImageResource(R.drawable.ic_pause_24); // Đổi icon thành nút Pause
            }
        }
    }

    // Phương thức cập nhật thông tin bài hát dựa trên chỉ số bài hát hiện tại
    private void updateSongInfo() {
        String title = "";
        switch (currentIndex) {
            case 0:
                title = "Em của ngày hôm qua - Sơn Tùng MTP";
                imageView.setImageResource(R.drawable.mtp_1);
                break;
            case 1:
                title = "Anh thôi nhân nhượng Remix - Kiều Chi";
                imageView.setImageResource(R.drawable.kieuchianhthoinhannhuong);
                break;
            case 2:
                title = "Lao tâm khổ tứ Remix - Thanh Hưng";
                imageView.setImageResource(R.drawable.laotamkhotu);
                break;
            case 3:
                title = "Vì anh đâu có biết Remix - Vũ";
                imageView.setImageResource(R.drawable.vianhdaucobiet);
                break;
            case 4:
                title = "Lạ lùng - Vũ";
                imageView.setImageResource(R.drawable.lalung);
                break;
        }
        songTitle.setText(title); // Hiển thị tiêu đề bài hát
    }

    // Phương thức bắt đầu hoạt ảnh xoay ảnh của bài hát
    private void startImageRotation() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(imageView, "rotation", 0f, 360f);
        animator.setDuration(10000); // Thời gian xoay là 10 giây
        animator.setInterpolator(new LinearInterpolator()); // Xoay với tốc độ đều
        animator.setRepeatCount(ObjectAnimator.INFINITE); // Xoay vô hạn
        animator.start();
    }

    // Thiết lập thanh điều chỉnh âm lượng
    private void setupVolumeSeekBar() {
        int maxV = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC); // Lấy giá trị âm lượng tối đa
        int curV = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC); // Lấy giá trị âm lượng hiện tại
        mSeekBarVol.setMax(maxV); // Đặt giá trị tối đa cho SeekBar
        mSeekBarVol.setProgress(curV); // Đặt giá trị hiện tại cho SeekBar
        mSeekBarVol.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0); // Cập nhật âm lượng khi thay đổi SeekBar
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    // Thiết lập thanh điều chỉnh thời gian của bài hát
    private void setupTimeSeekBar() {
        mSeekBarTime.setMax(100); // Đặt giá trị tối đa cho SeekBar là 100 (tính theo % thời gian bài hát)
        mSeekBarTime.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && mMediaPlayer != null) {
                    int newPosition = (mMediaPlayer.getDuration() * progress) / 100; // Tính toán vị trí mới của bài hát
                    mMediaPlayer.seekTo(newPosition); // Chuyển đến vị trí mới
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        // Tạo một thread để cập nhật vị trí của thanh SeekBar thời gian khi bài hát đang phát
        new Thread(() -> {
            while (mMediaPlayer != null) {
                try {
                    if (mMediaPlayer.isPlaying()) {
                        int currentPosition = mMediaPlayer.getCurrentPosition(); // Lấy thời gian hiện tại của bài hát
                        mSeekBarTime.setProgress((int) ((currentPosition * 100) / mMediaPlayer.getDuration())); // Cập nhật giá trị của SeekBar
                        runOnUiThread(() -> {
                            txtCurrentTime.setText(formatTime(currentPosition)); // Cập nhật thời gian hiện tại
                        });
                    }
                    Thread.sleep(1000); // Ngủ 1 giây rồi cập nhật tiếp
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    // Phương thức chuyển đổi thời gian từ mili giây thành định dạng phút:giây
    private String formatTime(int time) {
        int minutes = (time / 1000) / 60; // Lấy số phút
        int seconds = (time / 1000) % 60; // Lấy số giây
        return String.format("%02d:%02d", minutes, seconds); // Trả về chuỗi định dạng "phút:giây"
    }

    // Phương thức bật/tắt chế độ lặp lại bài hát
    private void toggleRepeat() {
        isRepeat = !isRepeat; // Đảo ngược giá trị của biến isRepeat
        imgRepeat.setImageResource(isRepeat ? R.drawable.ic_repeat_on : R.drawable.ic_repeat_off); // Thay đổi hình ảnh nút lặp lại
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mMediaPlayer != null) {
            mMediaPlayer.release(); // Giải phóng tài nguyên của MediaPlayer khi activity bị hủy
            mMediaPlayer = null;
        }
    }
}
