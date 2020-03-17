package com.example.blackrice000.hoverboard;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.IOException;

import static com.example.blackrice000.hoverboard.SetActivity.OPStream;
import static com.example.blackrice000.hoverboard.SetActivity.mBluetoothSocket;

public class RemoteActivity_4 extends AppCompatActivity {
    private String TAG = "RemoteActivity_4";
    private char position = 0x60;

    private Button btnSendP;
    private Button btnBack;
    protected RadioButton
            RadioPlayground_A,
            RadioPlayground_B,
            RadioPlayground_C,
            RadioPlayground_D;
    private RadioGroup PositionGroup;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remote_4);
        findViews();
        setOnClick();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void findViews() {
        btnSendP = (Button) findViewById(R.id.btn_Send_Position);
        btnBack = (Button) findViewById(R.id.btn_Back);
        RadioPlayground_A = (RadioButton) findViewById(R.id.radio_Playground_A);
        RadioPlayground_B = (RadioButton) findViewById(R.id.radio_Playground_B);
        RadioPlayground_C = (RadioButton) findViewById(R.id.radio_Playground_C);
        RadioPlayground_D = (RadioButton) findViewById(R.id.radio_Playground_D);
        PositionGroup = (RadioGroup) findViewById(R.id.Position_Group);
    }

    private void setOnClick() {
        btnSendP.setOnClickListener(myListener);
        btnBack.setOnClickListener(myListener);
        PositionGroup.setOnCheckedChangeListener(PositionGroupListener);
    }

    private Button.OnClickListener myListener = new Button.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_Back:
                    finish();
                    break;
                case R.id.btn_Send_Position:
                    Position_Send();
                    break;
            }
        }
    };

    private RadioGroup.OnCheckedChangeListener PositionGroupListener = new RadioGroup.OnCheckedChangeListener() {
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
                case R.id.radio_Playground_A:
                    position = 0x60;
                    break;
                case R.id.radio_Playground_B:
                    position = 0x5F;
                    break;
                case R.id.radio_Playground_C:
                    position = 0x5E;
                    break;
                case R.id.radio_Playground_D:
                    position = 0x5D;
                    break;
            }
        }
    };

    private void Position_Send() {
        try {
            if (mBluetoothSocket.isConnected()) {
                try {
                    OPStream = mBluetoothSocket.getOutputStream();
                } catch (IOException e2) {
                    Log.e(TAG, "Output stream creation failed", e2);
                }
                try {
                    OPStream.write(position);
                } catch (IOException e2) {
                    Log.e(TAG, "Exception during write", e2);
                }
            } else {
                Toast.makeText(RemoteActivity_4.this, "Not connect", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "---BluetoothSocket is null---", e);
            Toast.makeText(RemoteActivity_4.this, "BluetoothSocket is null", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();// ATTENTION: This was auto-generated to implement the App Indexing API.
// See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("RemoteActivity_4 Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}
