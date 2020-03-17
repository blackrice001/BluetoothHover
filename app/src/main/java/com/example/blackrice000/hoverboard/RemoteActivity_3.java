package com.example.blackrice000.hoverboard;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class RemoteActivity_3 extends AppCompatActivity {
    private String TAG = "RemoteActivity_3";
    private char position = 'a';
    private String Send_Current;

    private Button btnSendP;
    private Button btnBack;
    private Button btnSetCurrent;
    private EditText EditPosition;
    protected RadioButton RadioB2,
            RadioB1,
            RadioB34,
            RadioI1,
            RadioA1_1,
            RadioA1_2,
            RadioA2,
            RadioFamily;
    private RadioGroup PositionGroup;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remote_3);
        findViews();
        setOnClick();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void findViews() {
        btnSetCurrent = (Button) findViewById(R.id.btn_Set_Current);
        EditPosition = (EditText) findViewById(R.id.Edit_Position);
        btnSendP = (Button) findViewById(R.id.btn_Send_Position);
        btnBack = (Button) findViewById(R.id.btn_Back);
        PositionGroup = (RadioGroup) findViewById(R.id.Position_Group);
        RadioB2 = (RadioButton) findViewById(R.id.radio_B2);
        RadioB1 = (RadioButton) findViewById(R.id.radio_B1);
        RadioB34 = (RadioButton) findViewById(R.id.radio_B34);
        RadioI1 = (RadioButton) findViewById(R.id.radio_I1);
        RadioA1_1 = (RadioButton) findViewById(R.id.radio_A1_1);
        RadioA1_2 = (RadioButton) findViewById(R.id.radio_A1_2);
        RadioA2 = (RadioButton) findViewById(R.id.radio_A2);
        RadioFamily = (RadioButton) findViewById(R.id.radio_Family);
    }

    private void setOnClick() {
        btnSendP.setOnClickListener(myListener);
        btnBack.setOnClickListener(myListener);
        btnSetCurrent.setOnClickListener(myListener);
        PositionGroup.setOnCheckedChangeListener(PositionGroupListener);
    }

    private Button.OnClickListener myListener = new Button.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_Back:
                    finish();
                    break;
                case R.id.btn_Set_Current:
                    Set_Current_Position();
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
                case R.id.radio_B2:
                    position = 'a';
                    break;
                case R.id.radio_B1:
                    position = 'b';
                    break;
                case R.id.radio_B34:
                    position = 'c';
                    break;
                case R.id.radio_I1:
                    position = 'd';
                    break;
                case R.id.radio_A1_1:
                    position = 'e';
                    break;
                case R.id.radio_A2:
                    position = 'f';
                    break;
                case R.id.radio_Family:
                    position = 'g';
                    break;
                case R.id.radio_A1_2:
                    position = 'h';
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
                Toast.makeText(RemoteActivity_3.this, "Not connect", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "---BluetoothSocket is null---", e);
            Toast.makeText(RemoteActivity_3.this, "BluetoothSocket is null", Toast.LENGTH_SHORT).show();
        }
    }

    private void Set_Current_Position() {
        if (Integer.valueOf(EditPosition.getText().toString()) > 52) {
            Toast.makeText(RemoteActivity_3.this, "Out of range", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            if (mBluetoothSocket.isConnected()) {
                try {
                    OPStream = mBluetoothSocket.getOutputStream();
                } catch (IOException e2) {
                    Log.e(TAG, "Output stream creation failed", e2);
                }
                try {
                    Send_Current = EditPosition.getText().toString();
                    OPStream.write(0x0A);
                    OPStream.write(Integer.valueOf(EditPosition.getText().toString()) / 10);
                    OPStream.write(Integer.valueOf(EditPosition.getText().toString()) % 10);
                } catch (IOException e2) {
                    Log.e(TAG, "Exception during write", e2);
                } finally {
                    Toast.makeText(RemoteActivity_3.this, "Send " + Send_Current, Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(RemoteActivity_3.this, "Not connect", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "---BluetoothSocket is null---", e);
            Toast.makeText(RemoteActivity_3.this, "BluetoothSocket is null", Toast.LENGTH_SHORT).show();
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
                .setName("RemoteActivity_3 Page") // TODO: Define a title for the content shown.
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
