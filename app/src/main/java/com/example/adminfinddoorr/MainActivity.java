package com.example.adminfinddoorr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.location.Location;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import cn.pedant.SweetAlert.SweetAlertDialog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.util.Log;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity {


    TextView tvLocation;

    LocationHelper locationHelper;
    GeoPoint geoPoint;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String[] PERMS_INITIAL={
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
        };
        ActivityCompat.requestPermissions(this, PERMS_INITIAL, 127);

        tvLocation=findViewById(R.id.tv_location_lat);


    }

    @Override
    protected void onResume() {
        super.onResume();
    createLocationHelper();
    }

    void createLocationHelper(){


        locationHelper=new LocationHelper(this, new LocationManager() {
            @Override
            public void onLocationChanged(Location myCurrentlocation) {
                tvLocation.setText(myCurrentlocation.getLatitude()+" : "+myCurrentlocation.getLongitude()+"");
            }

            @Override

            public void getLastKnownLocation(Location location) {

            }
        });
        if (locationHelper.checkLocationPermissions()) {
            if (locationHelper.checkMapServices()) {
                locationHelper.startLocationUpdates();
            }
        }


    }



}