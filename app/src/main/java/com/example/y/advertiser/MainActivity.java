package com.example.y.advertiser;

import android.app.Activity;
import android.app.Fragment;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
//import com.google.android.gms.drive;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.AdvertisingOptions;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.Connections;
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes;
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo;
import com.google.android.gms.nearby.connection.DiscoveryOptions;
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadCallback;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;
import com.google.android.gms.nearby.connection.Strategy;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.nio.charset.Charset;
import java.util.jar.Manifest;


public class MainActivity extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks,OnConnectionFailedListener{


    GoogleApiClient mGoogleApiClient;
    private String changes;
    private static final String TAG = "Advertiser";
    public static final String CLIENT_NAME = "Teacher";
    public static final String SERVICE_ID = "Class302";
    public static final Strategy STRATEGY = Strategy.P2P_CLUSTER;
    String[] arr=new String[2];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        TextView tv1 = (TextView) findViewById(R.id.textView);
        tv1.setText("Hallo (CL)");
        ActivityCompat.requestPermissions(MainActivity.this,new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},1);


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */,
                        this /* OnConnectionFailedListener */)
                .addApi(Nearby.CONNECTIONS_API)
                .build();


    }
    //@Override
    public void onRequestPermissionsResult(int rcode,String permissions[],int[] grantResults){
        switch (rcode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(MainActivity.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG,"onConnected");
        startAdvertising();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG,"onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG,"onConnectionFailed");
    }

    private void startAdvertising() {
        Nearby.Connections.startAdvertising(
                mGoogleApiClient,
                CLIENT_NAME,
                SERVICE_ID,
                mConnectionLifecycleCallback,
                new AdvertisingOptions(STRATEGY))
                .setResultCallback(
                        new ResultCallback<Connections.StartAdvertisingResult>() {
                            @Override
                            public void onResult(@NonNull Connections.StartAdvertisingResult result) {
                                if (result.getStatus().isSuccess()) {
                                    Log.i(TAG, "Advertising endpoint");
                                    TextView tv1 = (TextView) findViewById(R.id.textView);
                                    tv1.setText("Advertising endpoint");

                                } else {
                                    Log.i(TAG, "unable to start advertising");
                                    TextView tv1 = (TextView) findViewById(R.id.textView);
                                    tv1.setText("unable to start advertising");

                                }
                            }
                        });
    }



    private final ConnectionLifecycleCallback mConnectionLifecycleCallback =
            new ConnectionLifecycleCallback() {
                @Override
                public void onConnectionInitiated(String endpointId, ConnectionInfo connectionInfo) {
                    Log.i(TAG, endpointId + " connection initiated");
                    TextView tv1 = (TextView) findViewById(R.id.textView);
                    tv1.setText("connection initiated");

                    //establishConnection(endpointId);
                    arr[0]=endpointId;
                    Nearby.Connections.acceptConnection(mGoogleApiClient,endpointId,mPayloadCallback);
                }

                @Override
                public void onConnectionResult(String endpointId, ConnectionResolution result) {
                    TextView tv1 = (TextView) findViewById(R.id.textView);
                    switch (result.getStatus().getStatusCode()) {
                        case ConnectionsStatusCodes.STATUS_OK:
                            Log.i(TAG,"Connected and can send data");

                            tv1.setText("Connected and can send data");
                            break;
                        case ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED:
                            Log.i(TAG,"REjected?");

                            tv1.setText("REjected?");
                            break;
                        case ConnectionsStatusCodes.STATUS_ERROR:
                            Log.i(TAG,"broke before being able to connect");


                            tv1.setText("broke before being able to connect");
                            break;
                    }
                }

                @Override
                public void onDisconnected(String endpointId) {
                    Log.i(TAG, endpointId + " disconnected");

                }
            };


    private PayloadCallback mPayloadCallback=new PayloadCallback() {
        @Override
        public void onPayloadReceived(String s, Payload payload) {
            Log.i(TAG,"PayloadString??? "+s+" Payload");
            TextView tv1 = (TextView) findViewById(R.id.textView);
            tv1.setText("Received");

        }

        @Override
        public void onPayloadTransferUpdate(String s, PayloadTransferUpdate payloadTransferUpdate) {
            Log.i(TAG,"PayloadTransupdate??? "+s+" Payload");
        }
    };

    private void Pl(){
        Nearby.Connections.sendPayload(mGoogleApiClient, arr[0],Payload.fromBytes("?1?".getBytes(Charset.forName("UTF-8"))));
    }

    public void Trial (View v){
        Log.i(TAG,"!");
        startAdvertising();

    }


    public void Trial2 (View v){
        Log.i(TAG,"versenden..");
        TextView tv1 = (TextView) findViewById(R.id.textView);
        tv1.setText("versenden..");
        Pl();


    }











}
