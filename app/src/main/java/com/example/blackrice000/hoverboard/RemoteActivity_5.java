
package com.example.blackrice000.hoverboard;

import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Iterator;

import static android.content.ContentValues.TAG;
import static com.example.blackrice000.hoverboard.SetActivity.OPStream;
import static com.example.blackrice000.hoverboard.SetActivity.mBluetoothSocket;

public class RemoteActivity_5 extends Activity {
    private LocationManager locationManager = null;
    private Criteria criteria = null;
    private LocationListener locationListener = null;
    private GpsStatus.NmeaListener nmeaListener = null;
    private GpsStatus.Listener gpsStatusListener = null;

    private TextView txtGPS_Latitude = null;
    private TextView txtGPS_Longitude = null;
    private TextView txtGPS_Quality = null;
    private TextView txtGPS_Location = null;
    private TextView txtGPS_Satellites = null;
    private TextView txtGPS_Altitude = null;
    private EditText editSendData;

    private Button btnSendPosition;
    private Button btnGetPosition;
    private Button btnBack;

    private String string_Latitude_d;
    private String string_Latitude_m;
    private String string_Longitude_d;
    private String string_Longitude_m;

    private Handler mHandler = null;

    private static final int LOCATION_UPDATE_MIN_DISTANCE = 0;  // m
    private static final int LOCATION_UPDATE_MIN_TIME = 5000;   // ms

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remote_5);

        findViews();
        setOnClick();

        registerHandler();
        registerListener();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_UPDATE_MIN_TIME, LOCATION_UPDATE_MIN_DISTANCE, locationListener);
        locationManager.addNmeaListener(nmeaListener);

    }

    private void findViews() {
        txtGPS_Quality = (TextView) findViewById(R.id.textGPS_Quality);
        txtGPS_Location = (TextView) findViewById(R.id.textGPS_Location);
        txtGPS_Satellites = (TextView) findViewById(R.id.textGPS_Satellites);
        txtGPS_Latitude = (TextView) findViewById(R.id.textGPS_Latitude);
        txtGPS_Longitude = (TextView) findViewById(R.id.textGPS_Longitude);
        txtGPS_Altitude = (TextView) findViewById(R.id.textGPS_Altitude);
        editSendData = (EditText) findViewById(R.id.edit_Send_Data);
        btnSendPosition = (Button) findViewById(R.id.btn_Send_Position);
        btnGetPosition = (Button) findViewById(R.id.btn_Get_Position);
        btnBack = (Button) findViewById(R.id.btn_Back);
    }

    private void setOnClick() {
        btnSendPosition.setOnClickListener(myListener);
        btnGetPosition.setOnClickListener(myListener);
        btnBack.setOnClickListener(myListener);
    }

    private Button.OnClickListener myListener = new Button.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_Back:
                    finish();
                    break;
                case R.id.btn_Send_Position:
                    Data_Send();
                    break;
                case R.id.btn_Get_Position:
                    Data_Trans();
                    break;
            }
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(locationListener);
        locationManager.removeNmeaListener(nmeaListener);
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    private void registerListener() {
        locationListener = new LocationListener() {

            @Override
            public void onLocationChanged(Location loc) {
                // TODO Auto-generated method stub
                //定位資料更新時會回呼
                Log.d("GPS-NMEA", loc.getLatitude() + "," + loc.getLongitude());
            }

            @Override
            public void onProviderDisabled(String provider) {
                // TODO Auto-generated method stub
                //定位提供者如果關閉時會回呼，並將關閉的提供者傳至provider字串中
            }

            @Override
            public void onProviderEnabled(String provider) {
                // TODO Auto-generated method stub
                //定位提供者如果開啟時會回呼，並將開啟的提供者傳至provider字串中
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                // TODO Auto-generated method stub
                Log.d("GPS-NMEA", provider + "");
                //GPS狀態提供，這只有提供者為gps時才會動作
                switch (status) {
                    case LocationProvider.OUT_OF_SERVICE:
                        Log.d("GPS-NMEA", "OUT_OF_SERVICE");
                        break;
                    case LocationProvider.TEMPORARILY_UNAVAILABLE:
                        Log.d("GPS-NMEA", " TEMPORARILY_UNAVAILABLE");
                        break;
                    case LocationProvider.AVAILABLE:
                        Log.d("GPS-NMEA", "" + provider + "");

                        break;
                }

            }

        };

        nmeaListener = new GpsStatus.NmeaListener() {
            public void onNmeaReceived(long timestamp, String nmea) {
                //check nmea's checksum
                if (isValidForNmea(nmea)) {
                    nmeaProgress(nmea);
                    Log.d("GPS-NMEA", nmea);
                }
            }
        };

        gpsStatusListener = new GpsStatus.Listener() {
            public void onGpsStatusChanged(int event) {
                // TODO Auto-generated method stub
                GpsStatus gpsStatus;
                gpsStatus = locationManager.getGpsStatus(null);

                switch (event) {
                    case GpsStatus.GPS_EVENT_FIRST_FIX:
                        //
                        gpsStatus.getTimeToFirstFix();
                        Log.d("GPS-NMEA", "GPS_EVENT_FIRST_FIX");
                        break;
                    case GpsStatus.GPS_EVENT_SATELLITE_STATUS:

                        Iterable<GpsSatellite> allSatellites = gpsStatus.getSatellites();
                        Iterator<GpsSatellite> it = allSatellites.iterator();

                        int count = 0;
                        while (it.hasNext()) {
                            GpsSatellite gsl = (GpsSatellite) it.next();

                            if (gsl.getSnr() > 0.0) {
                                count++;
                            }

                        }


                        break;
                    case GpsStatus.GPS_EVENT_STARTED:
                        //Event sent when the GPS system has started.
                        Log.d("GPS-NMEA", "GPS_EVENT_STARTED");
                        break;
                    case GpsStatus.GPS_EVENT_STOPPED:
                        //Event sent when the GPS system has stopped.
                        Log.d("GPS-NMEA", "GPS_EVENT_STOPPED");
                        break;
                    default:
                        break;
                }
            }

        };

    }

    private void registerHandler() {
    /*
    GGA Global Positioning System Fix Data. Time, Position and fix related data for a GPS receiver
	11
	1 2 34 5678 910|121314 15
	||||||||||||||| $--GGA,hhmmss.ss,llll.ll,a,yyyyy.yy,a,x,xx,x.x,x.x,M,x.x,M,x.x,xxxx*hh
	1) Time (UTC)
	2) Latitude
	3) N or S (North or South)
	4) Longitude
	5) E or W (East or West)
	6) GPS Quality Indicator,
	0 - fix not available, 
	1 - GPS fix,
	2 - Differential GPS fix
	7) Number of satellites in view, 00 - 12
	8) Horizontal Dilution of precision
	9) Antenna Altitude above/below mean-sea-level (geoid)
	10) Units of antenna altitude, meters
	11) Geoidal separation, the difference between the WGS-84 earth
	ellipsoid and mean-sea-level (geoid), "-" means mean-sea-level below ellipsoid
	12) Units of geoidal separation, meters
	13) Age of differential GPS data, time in seconds since last SC104
	type 1 or 9 update, null field when DGPS is not used
	14) Differential reference station ID, 0000-1023
	15) Checksum
		 */
        mHandler = new Handler() {
            public void handleMessage(Message msg) {

                String str = (String) msg.obj;
                String[] rawNmeaSplit = str.split(",");

                txtGPS_Location.setText(rawNmeaSplit[2] + " " + rawNmeaSplit[3] + "," + rawNmeaSplit[4] + " " + rawNmeaSplit[5]);

                if (rawNmeaSplit[2].length() > 2) {
                    string_Latitude_d = rawNmeaSplit[2].substring(0, 2);
                    string_Latitude_m = rawNmeaSplit[2].substring(2, rawNmeaSplit[2].length());
                    txtGPS_Latitude.setText(string_Latitude_d + "°" + string_Latitude_m + "'");
                } else
                    txtGPS_Latitude.setText("0");

                if (rawNmeaSplit[4].length() > 3) {
                    string_Longitude_d = rawNmeaSplit[4].substring(0, 3);
                    string_Longitude_m = rawNmeaSplit[4].substring(3, rawNmeaSplit[4].length());
                    txtGPS_Longitude.setText(string_Longitude_d + "°" + string_Longitude_m + "'");
                } else
                    txtGPS_Longitude.setText("0");

                txtGPS_Quality.setText(rawNmeaSplit[6] + (Integer.valueOf(rawNmeaSplit[6])==0 ? " (invalid)" : " (valid)"));
                txtGPS_Satellites.setText(rawNmeaSplit[7]);
                txtGPS_Altitude.setText(rawNmeaSplit[9] + " m");
            }
        };
    }

    private void Data_Trans() {
        if (string_Longitude_m != null && string_Latitude_m != null)
            editSendData.setText("$," + string_Latitude_m + "," + string_Longitude_m + "*");
    }

    private void Data_Send() {
        String send_data;
        try {
            if (mBluetoothSocket.isConnected()) {
                try {
                    OPStream = mBluetoothSocket.getOutputStream();
                } catch (IOException e2) {
                    Log.e(TAG, "Output stream creation failed", e2);
                }
                try {
                    send_data = editSendData.getText().toString();
                    OPStream.write(send_data.getBytes());
                } catch (IOException e2) {
                    Log.e(TAG, "Exception during write", e2);
                }
            } else {
                Toast.makeText(RemoteActivity_5.this, "Not connect", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "---BluetoothSocket is null---", e);
            Toast.makeText(RemoteActivity_5.this, "BluetoothSocket is null", Toast.LENGTH_SHORT).show();
        }
    }


    // get  callback of NMEA information
    private void nmeaProgress(String rawNmea) {

        String[] rawNmeaSplit = rawNmea.split(",");

        if (rawNmeaSplit[0].equalsIgnoreCase("$GPGGA")) {
            //send GGA nmea data to handler
            Message msg = new Message();
            msg.obj = rawNmea;
            mHandler.sendMessage(msg);
        }

    }

    private boolean isValidForNmea(String rawNmea) {
        boolean valid = true;
        byte[] bytes = rawNmea.getBytes();
        int checksumIndex = rawNmea.indexOf("*");
        //NMEA 星號後為checksum number
        byte checksumCalcValue = 0;
        int checksumValue;

        //檢查開頭是否為$
        if ((rawNmea.charAt(0) != '$') || (checksumIndex == -1)) {
            valid = false;
        }
        //
        if (valid) {
            String val = rawNmea.substring(checksumIndex + 1, rawNmea.length()).trim();
            checksumValue = Integer.parseInt(val, 16);
            for (int i = 1; i < checksumIndex; i++) {
                checksumCalcValue = (byte) (checksumCalcValue ^ bytes[i]);
            }
            if (checksumValue != checksumCalcValue) {
                valid = false;
            }
        }
        return valid;
    }
}