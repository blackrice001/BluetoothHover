package com.example.blackrice000.hoverboard;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import static com.example.blackrice000.hoverboard.Constants.MESSAGE_READ;

public class SetActivity extends AppCompatActivity {
    private Button btnBack;
    private Button btnScan;
    private Button btnConnect;
    private Button btnDiscover;
    private Button btnSend;
    private Button btnTurnOff;
    private Button btnTurnOn;
    private Button btnDisconnect;
    private TextView TVReceive;
    private EditText EditData;

/**  BluetoothAdapter
 *      cancelDiscovery()
 *      disable()
 *      enable()
 *      Intent enabler=new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
 *      startActivityForResult(enabler,reCode);
 *      getAddress()
 *      getDefaultAdapter()
 *      getName()
 *      getRemoteDevice(String address)
 *      getState()
 *      isDiscovering()
 *      isEnabled()
 *      listenUsingRfcommWithServiceRecord(String name,UUID uuid)
 *      startDiscovery()
 */

/**  BluetoothDevice
 *      connectGatt(Context context, boolean autoConnect, BluetoothGattCallback callback)
 *      createBond()
 *      createInsecureRfcommSocketToServiceRecord(UUID uuid)
 *      createRfcommSocketToServiceRecord(UUID uuid)
 *      describeContents()
 *      equals(Object o)
 *      fetchUuidsWithSdp()
 *      getAddress()
 *      getBluetoothClass()
 *      getBondState()
 *      getName()
 *      getType()
 *      getUuids()
 *      hashCode()
 *      setPairingConfirmation(boolean confirm)
 *      setPin(byte[] pin)
 *      toString()
 *      writeToParcel(Parcel out, int flags)
 */

/**  BluetoothSocket
 *      close()
 *      connect()
 *      getInptuStream()
 *      getOutputStream()
 *      getRemoteDevice()
 *      isConnected()
 */

    /**
     * BluetoothServerSocket
     * accept(int timeout)
     * accept()
     * close()
     */

    public static BluetoothAdapter mBluetoothAdapter;
    public static BluetoothDevice mBluetoothDevice;
    public static BluetoothSocket mBluetoothSocket;
    public static BluetoothServerSocket mBluetoothServerSocket;

    private String TAG = "SetActivity";
    private String Send_Data = "Hello";
    private String Receive_Data = null;
    private static final boolean Debug = true;

    private static final int REQUEST_ENABLE_BT = 1;            // Request bluetooth activity intent
    private static final int REQUEST_CONNECT_DEVICE = 2;

    private GoogleApiClient client;

    public static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    public static String Device_Bluetooth_name = null;
    public static String Bluetooth_name = null;
    public static String Bluetooth_address = "00:00:00:00:00:00";
    public static InputStream IPStream = null;
    public static OutputStream OPStream = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Debug) Log.e(TAG, "+++ ON CREATE +++");
        setContentView(R.layout.activity_set);
        findViews();
        setOnClick();

        //  獲取默認 BluetoothAdapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "No bluetooth support", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

    }

    // 取得 R.java
    private void findViews() {
        btnBack = (Button) findViewById(R.id.btn_Back_1);
        btnScan = (Button) findViewById(R.id.btn_Scan);
        btnConnect = (Button) findViewById(R.id.btn_Connect);
        btnDiscover = (Button) findViewById(R.id.btn_Discover);
        btnSend = (Button) findViewById(R.id.btn_Send);
        btnTurnOff = (Button) findViewById(R.id.btn_TurnOff);
        btnTurnOn = (Button) findViewById(R.id.btn_TurnOn);
        btnDisconnect = (Button) findViewById(R.id.btn_Disconnect);
        TVReceive = (TextView) findViewById(R.id.TV_Receive);
        EditData = (EditText)findViewById(R.id.Edit_Data);
    }
    // 偵聽動作
    private void setOnClick() {
        btnBack.setOnClickListener(myListener);
        btnScan.setOnClickListener(myListener);
        btnConnect.setOnClickListener(myListener);
        btnDiscover.setOnClickListener(myListener);
        btnSend.setOnClickListener(myListener);
        btnTurnOff.setOnClickListener(myListener);
        btnTurnOn.setOnClickListener(myListener);
        btnDisconnect.setOnClickListener(myListener);
    }

    private Button.OnClickListener myListener = new Button.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_Scan:
                    // 未開啟藍芽
                    if (!mBluetoothAdapter.isEnabled()) {
                        Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
                    }
                    // 選取配對設備
                    if (mBluetoothAdapter.isEnabled()) {
                        Intent serverIntent = new Intent(SetActivity.this, DeviceListActivity.class);
                        startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
                    }
                    break;
                case R.id.btn_Discover:
                    ensureDiscoverable();
                    break;
                case R.id.btn_Send:
                    try {
                        if (mBluetoothSocket.isConnected()) {
                            try {
                                // 獲得讀寫數據的 OutputStream
                                OPStream = mBluetoothSocket.getOutputStream();
                            } catch (IOException e2) {
                                Log.e(TAG, "Output stream creation failed", e2);
                            }
                            try {
                                // 將字串轉成 Byte 陣列，再寫入串流中
                                Send_Data = EditData.getText().toString();
                                OPStream.write(Send_Data.getBytes());
                                // 第二種方法
                                //  將字串轉成 Byte 陣列，建立新物件，藉由 ConnectedThread.write 寫入串流中
                                //ConnectedThread mmConnectedThread = new ConnectedThread(mBluetoothSocket);
                                //mmConnectedThread.write(Send_Data.getBytes());
                            } catch (IOException e2) {
                                Log.e(TAG, "Exception during write", e2);
                            } finally {
                                Toast.makeText(SetActivity.this, "Send " + Send_Data, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(SetActivity.this, "Not connect", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "---BluetoothSocket is null---", e);
                        Toast.makeText(SetActivity.this, "BluetoothSocket is null", Toast.LENGTH_SHORT).show();
                    }
                    break;

                case R.id.btn_Connect:
                    // 不支援藍芽
                    if (mBluetoothAdapter == null) {
                        Toast.makeText(SetActivity.this, "No support Bluetooth", Toast.LENGTH_SHORT).show();
                        finish();
                        return;
                    }

                    // 未開啟藍芽
                    if (!mBluetoothAdapter.isEnabled()) {
                        Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
                    }

                    if (mBluetoothAdapter.isEnabled()) {
                        // 已選取藍芽設備
                        if (Bluetooth_address != "00:00:00:00:00:00") {
                            mBluetoothDevice = mBluetoothAdapter.getRemoteDevice(Bluetooth_address);
                            try {
                                // 創建 BluetoothServerSocket 對象
                                mBluetoothSocket = mBluetoothDevice.createRfcommSocketToServiceRecord(MY_UUID);
                            } catch (IOException e) {
                                Log.e(TAG, "Socket creation failed", e);
                            }
                            mBluetoothAdapter.cancelDiscovery();

                            try {
                                mBluetoothSocket.connect();
                                Toast.makeText(SetActivity.this, "Connect", Toast.LENGTH_SHORT).show();
                                Thread mConnectedThread = new ConnectedThread(mBluetoothSocket);
                                mConnectedThread.start();
                            } catch (IOException e) {
                                try {
                                    mBluetoothSocket.close();
                                } catch (IOException e2) {
                                    Log.e(TAG, "Unable to close socket during connection failure", e2);
                                }
                            }
                        } else {
                            Toast.makeText(SetActivity.this, "Not select device address", Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
                case R.id.btn_TurnOn:
                    turnOnBluetooth();
                    break;
                case R.id.btn_TurnOff:
                    turnOffBluetooth();
                    break;
                case R.id.btn_Disconnect:
                    Disconnect_Bluetooth();
                    break;
                case R.id.btn_Back_1:
                    finish();
                    break;
            }
        }
    };

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Debug)
            Log.d(TAG, "onActivityResult" + resultCode);

        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                if (resultCode == RESULT_OK) {
                    Toast.makeText(this, "Bluetooth has open", Toast.LENGTH_SHORT).show();
                } else if (requestCode == RESULT_CANCELED) {
                    Toast.makeText(this, "Bluetooth not open", Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUEST_CONNECT_DEVICE:
                if (resultCode == Activity.RESULT_OK) {
                    // Get MAC address and device name of bluetooth device
                    Bluetooth_address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                    Device_Bluetooth_name = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_NAME);

                    // Get name of bluetooth device
                    BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(Bluetooth_address);
                    Bluetooth_name = device.getName();

                    Toast.makeText(this, "Your adapter name : \n" + Device_Bluetooth_name, Toast.LENGTH_SHORT).show();
                    Toast.makeText(this, "You choose device : \n" + Bluetooth_name + "\n" + Bluetooth_address, Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void ensureDiscoverable() {
        if (Debug) Log.d(TAG, "ensure discoverable");
        if (mBluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
        if (Debug) Log.e(TAG, "++ ON START ++");
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (Debug) Log.e(TAG, "-- ON STOP --");
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Set Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    public static boolean isBluetoothSupported() {
        return BluetoothAdapter.getDefaultAdapter() != null ? true : false;
    }

    public static boolean isBluetoothEnabled() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null) {
            return bluetoothAdapter.isEnabled();
        } else {
            return false;
        }
    }

    public static boolean turnOnBluetooth() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null) {
            return bluetoothAdapter.enable();
        } else {
            return false;
        }
    }

    public static boolean turnOffBluetooth() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null) {
            return bluetoothAdapter.disable();
        } else {
            return false;
        }
    }

    public void Disconnect_Bluetooth() {
        if (Debug) Log.d(TAG, "cancel " + this);
        Toast.makeText(SetActivity.this, "Disconnect", Toast.LENGTH_SHORT).show();
        if (mBluetoothSocket != null) {
            try {
                mBluetoothSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of server failed", e);
            }
        }
    }

    public Handler mHandler = new Handler();

    private class ConnectedThread extends Thread {

        public ConnectedThread(BluetoothSocket socket) {
            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                IPStream = mBluetoothSocket.getInputStream();
                OPStream = mBluetoothSocket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "temp sockets not created", e);
            }
        }

        public void run() {
            Log.i(TAG, "BEGIN mConnectedThread");
            byte[] buffer;
            int bytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    buffer = new byte[1024];  // buffer store for the stream
                    // Read from the InputStream
                    bytes = IPStream.read(buffer);
                    // Send the obtained bytes to the UI activity
                    mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer).sendToTarget();
                    Receive_Data = new String(buffer);
                    new Job1Task().execute();
                } catch (Exception e) {
                    Log.e(TAG, "!!!!!  Read Error  !!!!!", e);
                    break;
                }
            }
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(byte[] bytes) {
            try {
                OPStream.write(bytes);
            } catch (IOException e) {
            }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                mBluetoothSocket.close();
            } catch (IOException e) {
            }
        }
    }

    class Job1Task extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            TVReceive.setText(Receive_Data);
            Toast.makeText(SetActivity.this, "Receive data", Toast.LENGTH_SHORT).show();
        }
    }
}

