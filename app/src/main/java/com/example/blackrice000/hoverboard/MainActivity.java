package com.example.blackrice000.hoverboard;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import static com.example.blackrice000.hoverboard.SetActivity.turnOffBluetooth;

public class MainActivity extends AppCompatActivity {
    private Button btnSet;
    private Button btnRemote;
    private Button btnRemote_1;
    private Button btnGPS_0;
    private Button btnGPS_1;
    private Button btnGPS_2;
    private Button btnExit;

    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViews();
        setOnClick();

        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void findViews() {
        btnSet = (Button) findViewById(R.id.btn_Set);
        btnRemote = (Button) findViewById(R.id.btn_Remote_0);
        btnRemote_1 = (Button) findViewById(R.id.btn_Remote_1);
        btnGPS_0 = (Button) findViewById(R.id.btn_GPS_0);
        btnGPS_1 = (Button) findViewById(R.id.btn_GPS_1);
        btnGPS_2 = (Button) findViewById(R.id.btn_GPS_2);
        btnExit = (Button) findViewById(R.id.btn_Exit);
    }

    private void setOnClick() {
        btnRemote.setOnClickListener(myListener);
        btnRemote_1.setOnClickListener(myListener);
        btnGPS_0.setOnClickListener(myListener);
        btnGPS_1.setOnClickListener(myListener);
        btnGPS_2.setOnClickListener(myListener);
        btnExit.setOnClickListener(myListener);
        btnSet.setOnClickListener(myListener);
    }

    @Override
    protected void onStart() {
        super.onStart();
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();// ATTENTION: This was auto-generated to implement the App Indexing API.
// See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.disconnect();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private Button.OnClickListener myListener = new Button.OnClickListener() {
        public void onClick(View v) {
            Intent intent = new Intent();
            switch (v.getId()) {
                case R.id.btn_Set:
                    intent.setClass(MainActivity.this, SetActivity.class);
                    startActivity(intent);
                    break;

                case R.id.btn_Exit:
                    turnOffBluetooth();
                    finish();
                    break;

                case R.id.btn_Remote_0:
                    intent.setClass(MainActivity.this, RemoteActivity.class);
                    startActivity(intent);
                    break;

                case R.id.btn_Remote_1:
                    intent.setClass(MainActivity.this, RemoteActivity_2.class);
                    startActivity(intent);
                    break;

                case R.id.btn_GPS_0:
                    intent.setClass(MainActivity.this, RemoteActivity_3.class);
                    startActivity(intent);
                    break;

                case R.id.btn_GPS_1:
                    intent.setClass(MainActivity.this, RemoteActivity_4.class);
                    startActivity(intent);
                    break;

                case R.id.btn_GPS_2:
                    intent.setClass(MainActivity.this, RemoteActivity_5.class);
                    startActivity(intent);
                    break;
            }
        }
    };

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }
}
