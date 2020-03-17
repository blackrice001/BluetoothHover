package com.example.blackrice000.hoverboard;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import static com.example.blackrice000.hoverboard.SetActivity.OPStream;
import static com.example.blackrice000.hoverboard.SetActivity.mBluetoothSocket;

public class RemoteActivity_2 extends AppCompatActivity {
    private Button btnStart;
    private Button btnStop;
    private Button btnBack;
    private SeekBar SeekBarRight = null;
    private SeekBar SeekBarLeft = null;
    private SeekBar SeekBarAll = null;
    private TextView motor_value;
    private String TAG = "RemoteActivity_2";
    private boolean RUN_THREAD = false;

    private int right_value = 0;
    private char right_data = 'i';
    private int left_value = 0;
    private char left_data = 'r';
    private int count_0 = 0;
    private int stop_time = 0;

    private Handler mThreadHandler;
    private HandlerThread mThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        setContentView(R.layout.activity_remote_2);
        findViews();
        setOnClick();
        InitialSeekBar();
    }

    private void findViews() {
        motor_value = (TextView) findViewById(R.id.motor_value);
        btnStart = (Button) findViewById(R.id.btn_Start);
        btnStop = (Button) findViewById(R.id.btn_Stop);
        btnBack = (Button) findViewById(R.id.btn_Back);
        SeekBarRight = (SeekBar) findViewById(R.id.seekBar_right);
        SeekBarLeft = (SeekBar) findViewById(R.id.seekBar_left);
        SeekBarAll = (SeekBar) findViewById(R.id.seekBar_all);
    }

    private void setOnClick() {
        btnStop.setOnClickListener(btnSpeedListener);
        btnStart.setOnClickListener(btnSpeedListener);
        btnBack.setOnClickListener(btnSpeedListener);
        SeekBarRight.setOnSeekBarChangeListener(SeekBarListener);
        SeekBarLeft.setOnSeekBarChangeListener(SeekBarListener);
        SeekBarAll.setOnSeekBarChangeListener(SeekBarListener);
    }

    private void InitialSeekBar() {
        SeekBarRight.setMax(8);
        SeekBarRight.setProgress(0);
        SeekBarLeft.setMax(8);
        SeekBarLeft.setProgress(0);
        SeekBarAll.setMax(8);
        SeekBarAll.setProgress(0);
    }

    private SeekBar.OnSeekBarChangeListener SeekBarListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            if (seekBar == SeekBarAll) {
                SeekBarRight.setProgress(right_value);
                SeekBarLeft.setProgress(left_value);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (seekBar == SeekBarRight)
                right_value = progress;
            else if (seekBar == SeekBarLeft)
                left_value = progress;
            else {
                right_value = left_value = progress;
            }
            motor_value.setText("Right: " + Integer.toString(right_value) + ", Left: " + Integer.toString(left_value));
            Motion_Transfer();
        }
    };

    private Button.OnClickListener btnSpeedListener = new Button.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_Start:
                    break;
                case R.id.btn_Stop:
                    SeekBarRight.setProgress(0);
                    SeekBarLeft.setProgress(0);
                    SeekBarAll.setProgress(0);
                    stop_time = 0;
                    break;
                case R.id.btn_Back:
                    finish();
                    break;
            }
        }
    };

    private void Motion_Transfer() {
        right_data = (char) (right_value + 'i');
        left_data = (char) (left_value + 'r');
    }

    private void Motion_Send(String msg) {
        try {
            if (mBluetoothSocket.isConnected()) {
                try {
                    OPStream = mBluetoothSocket.getOutputStream();
                } catch (IOException e) {
                    Log.e(TAG, "Output stream creation failed", e);
                }
                try {
                    OPStream.write(msg.getBytes());
                } catch (IOException e2) {
                    Log.e(TAG, "Exception during write", e2);
                }
            } else {
                Toast.makeText(RemoteActivity_2.this, "Not connect", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "---BluetoothSocket is null---", e);
            Toast.makeText(RemoteActivity_2.this, "BluetoothSocket is null", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mThread = new HandlerThread("name");
        mThread.start();
        mThreadHandler = new Handler(mThread.getLooper());
        mThreadHandler.post(Runnable1);
        RUN_THREAD = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        RUN_THREAD = false;
    }

    private Runnable Runnable1 = new Runnable() {
        public void run() {
            while (RUN_THREAD) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (stop_time >= 5) {
                    if (count_0 == 0)
                        Motion_Send(Character.toString(right_data));
                    else if (count_0 == 1)
                        Motion_Send(Character.toString(left_data));
                } else {
                    stop_time++;
                    Motion_Send(Character.toString('0'));
                }
                if (count_0 < 2)
                    count_0++;
                else
                    count_0 = 0;
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RUN_THREAD = false;
        Runnable1 = null;
    }
}
