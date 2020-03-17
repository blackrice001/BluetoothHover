package com.example.blackrice000.hoverboard;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import java.io.IOException;

import static com.example.blackrice000.hoverboard.SetActivity.OPStream;
import static com.example.blackrice000.hoverboard.SetActivity.mBluetoothSocket;

public class RemoteActivity extends AppCompatActivity {

    private Button btnBack_1;
    private Button btnBack_2;
    private Button btnFront;
    private Button btnLeft;
    private Button btnRight;
    private Button btnReverse;
    private Button btnStop;
    private Button btnSpeedUp;
    private Button btnSpeedDown;

    private int back = 0;
    private String MotionData = "N";
    private String SendData;
    private Byte SpeedSection = 0;
    private String TAG = "RemoteActivity";
    private boolean RUN_THREAD = false;
    private static final boolean Debug = true;

    private Handler mThreadHandler;
    private HandlerThread mThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        setContentView(R.layout.activity_remote);
        findViews();
        setOnClick();
    }

    private void findViews() {
        btnBack_1 = (Button) findViewById(R.id.btn_Back_1);
        btnBack_2 = (Button) findViewById(R.id.btn_Back_2);
        btnFront = (Button) findViewById(R.id.btn_Front);
        btnReverse = (Button) findViewById(R.id.btn_Reverse);
        btnLeft = (Button) findViewById(R.id.btn_Left);
        btnRight = (Button) findViewById(R.id.btn_Right);
        btnStop = (Button) findViewById(R.id.btn_Stop);
        btnSpeedDown = (Button) findViewById(R.id.btn_speeddown);
        btnSpeedUp = (Button) findViewById(R.id.btn_speedup);
    }

    private void setOnClick() {
        btnBack_1.setOnTouchListener(btnBackListener);
        btnBack_2.setOnTouchListener(btnBackListener);
        btnLeft.setOnTouchListener(btnMotionListener);
        btnRight.setOnTouchListener(btnMotionListener);
        btnFront.setOnTouchListener(btnMotionListener);
        btnReverse.setOnTouchListener(btnMotionListener);
        btnStop.setOnClickListener(btnSpeedListener);
        btnSpeedUp.setOnClickListener(btnSpeedListener);
        btnSpeedDown.setOnClickListener(btnSpeedListener);
    }

    private Button.OnTouchListener btnBackListener = new Button.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (v.getId()) {
                case R.id.btn_Back_1:
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        back = back + 1;
                        if (back == 2)
                            finish();
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        back = back - 1;
                    }
                    break;
                case R.id.btn_Back_2:
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        back = back + 1;
                        if (back == 2)
                            finish();
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        back = back - 1;
                    }
                    break;
            }
            return false;
        }
    };

    private Button.OnClickListener btnSpeedListener = new Button.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_speeddown:
                    if (SpeedSection > 0)
                        SpeedSection--;
                    break;
                case R.id.btn_speedup:
                    if (SpeedSection < 8)
                        SpeedSection++;
                    break;
                case R.id.btn_Stop:
                    SpeedSection = 0;
                    break;
            }
            btnStop.setText(String.valueOf(SpeedSection));
        }
    };

    private Button.OnTouchListener btnMotionListener = new Button.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                switch (v.getId()) {
                    case R.id.btn_Front:
                        MotionData = "F";
                        break;
                    case R.id.btn_Reverse:
                        MotionData = "B";
                        break;
                    case R.id.btn_Right:
                        MotionData = "R";
                        break;
                    case R.id.btn_Left:
                        MotionData = "L";
                        break;
                }
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                MotionData = "N";
            }
            return false;
        }
    };

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
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        RUN_THREAD = false;
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RUN_THREAD = false;
        Runnable1 = null;
    }

    private void Speed_Trans() {
        if (MotionData == "F")
            SendData = Integer.toString(SpeedSection);
        else
            SendData = MotionData;
    }

    private void Motion_Send() {
        try {
            if (mBluetoothSocket.isConnected()) {
                try {
                    OPStream = mBluetoothSocket.getOutputStream();
                } catch (IOException e2) {
                    Log.e(TAG, "Output stream creation failed", e2);
                }
                try {
                    OPStream.write(SendData.getBytes());
                } catch (IOException e) {
                    if (Debug) Log.e(TAG, "Exception during write", e);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "---BluetoothSocket is null---", e);
        }
    }

    private Runnable Runnable1 = new Runnable() {
        public void run() {
            while (RUN_THREAD) {
                try {
                    Thread.sleep(150);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Speed_Trans();
                Motion_Send();
            }
        }
    };


}

