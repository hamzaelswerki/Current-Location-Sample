package com.example.adminfinddoorr;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsRequest.Builder;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnSuccessListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.Iterator;
import java.util.List;


public final class LocationHelper{



    LocationManager locationManager;

    private final int INTERVAL;
    private final int FAST_INTERVAL;
    private final String[] PERMISSIONS;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;
    private double longitude;
    private double latitude;
    private boolean permissionLocationGranted;
    private Activity activity;

    private final void createLocationRequest() {
        this.locationRequest = new LocationRequest();
        LocationRequest var10000 = this.locationRequest;
        var10000.setInterval((long)this.INTERVAL);
        var10000 = this.locationRequest;
        var10000.setFastestInterval((long)this.FAST_INTERVAL);
        var10000 = this.locationRequest;
        var10000.setPriority(100);
        Builder builder = new Builder();
        LocationRequest var10001 = this.locationRequest;
        builder.addLocationRequest(var10001);
        LocationSettingsRequest locationSettingsRequest = builder.build();
        SettingsClient settingsClient = LocationServices.getSettingsClient(this.activity);
        settingsClient.checkLocationSettings(locationSettingsRequest);
    }

    private final void createLocationCallBack() {
        if (this.locationCallback == null) {
            this.locationCallback = (LocationCallback)(new LocationCallback() {
                public void onLocationResult( LocationResult locationResult) {
                    super.onLocationResult(locationResult);
                    if (locationResult != null) {
                        Iterator var3 = locationResult.getLocations().iterator();

                        while(var3.hasNext()) {
                            Location location = (Location)var3.next();
                            if (location != null) {
                                locationManager.onLocationChanged(location);
                          longitude = location.getLongitude();
                               latitude = location.getLatitude();
                            }
                        }
                    }

                }
            });
        }

    }

    public final void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission((Context)this.activity,
                "android.permission.ACCESS_FINE_LOCATION") != 0 && ActivityCompat.checkSelfPermission((Context)this.activity, "android.permission.ACCESS_COARSE_LOCATION") != 0) {
            ActivityCompat.requestPermissions(this.activity, this.PERMISSIONS, 1234);
        } else {
            FusedLocationProviderClient var10000 = this.fusedLocationClient;
            var10000.requestLocationUpdates(this.locationRequest, this.locationCallback, (Looper)null);
            this.getLastKnownLocation();
        }
    }

    private final void getLastKnownLocation() {
        if (this.fusedLocationClient == null) {
            this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(this.activity);
        }

        if (ActivityCompat.checkSelfPermission((Context)this.activity,
                "android.permission.ACCESS_FINE_LOCATION") == 0 || ActivityCompat.checkSelfPermission((Context)this.activity, "android.permission.ACCESS_COARSE_LOCATION") == 0) {

            FusedLocationProviderClient var10000 = this.fusedLocationClient;
            var10000.getLastLocation().addOnSuccessListener((OnSuccessListener)(new OnSuccessListener() {
                public void onSuccess(Object var1) {
                    this.onSuccess((Location)var1);
                }

                public final void onSuccess(Location location) {
                    if (location != null) {
                        locationManager.getLastKnownLocation(location);
                    longitude = location.getLongitude();
                     latitude = location.getLatitude();
                    }

                }
            }));
        }
    }

    public final void stopLocationUpdates() {
        FusedLocationProviderClient var10000 = this.fusedLocationClient;
        var10000.removeLocationUpdates(this.locationCallback);
    }

    private final boolean isServicesOK() {
        Log.d(this.activity.getLocalClassName(), "isServicesOK: checking google services version");
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable((Context)this.activity);
        if (available == 0) {
            Log.d(this.activity.getLocalClassName(), "isServicesOK: Google Play Services is working");
            return true;
        } else {
            if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
                Log.d(this.activity.getLocalClassName(), "isServicesOK: an error occured but we can fix it");
                Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(this.activity, available, 901);
                dialog.show();
            } else {
                Toast.makeText((Context)this.activity, (CharSequence)"You can't make map requests",
                        Toast.LENGTH_SHORT).show();
            }

            return false;
        }
    }

    private final boolean isGPSEnabled() {
        Object var10000 = this.activity.getSystemService(Context.LOCATION_SERVICE);
        if (var10000 == null) {
            throw new NullPointerException("null cannot be cast to non-null type android.location.LocationManager");
        } else {
            android.location.LocationManager manager = (android.location.LocationManager)var10000;
            if (!manager.isProviderEnabled("gps")) {
                Toast.makeText((Context)this.activity, (CharSequence)"Please Enable GPS!", 0).show();
                Intent gpsOptionsIntent = new Intent(
                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
               activity.startActivity(gpsOptionsIntent);
                return false;
            } else {
                return true;
            }
        }
    }

    public final boolean checkLocationPermissions() {
        if (ActivityCompat.checkSelfPermission((Context)this.activity, "android.permission.ACCESS_COARSE_LOCATION") == 0 && ActivityCompat.checkSelfPermission((Context)this.activity, "android.permission.ACCESS_FINE_LOCATION") == 0) {
            return true;
        } else {
            this.requestLocationPermission();
            return false;
        }
    }

    private final void requestLocationPermission() {
        Dexter.withContext((Context)this.activity).withPermissions
                (new String[]{"android.permission.ACCESS_COARSE_LOCATION",
                        "android.permission.ACCESS_FINE_LOCATION"}).withListener
                ((MultiplePermissionsListener)(new MultiplePermissionsListener() {
            public void onPermissionsChecked( MultiplePermissionsReport report) {
                if (report != null) {
                    boolean var3 = false;
                    boolean var4 = false;
                    if (report.areAllPermissionsGranted()) {
                   permissionLocationGranted = true;
                    }
                }

            }

            public void onPermissionRationaleShouldBeShown( List permissions,  PermissionToken token) {
                if (token != null) {
                    token.continuePermissionRequest();
                }

            }
        })).check();
    }

    public final boolean checkMapServices() {
        return this.isServicesOK() && this.isGPSEnabled();
    }

    public final boolean getGrantedLocation() {
        return this.permissionLocationGranted;
    }

    public final Activity getActivity() {
        return this.activity;
    }

    public final void setActivity( Activity var1) {
        this.activity = var1;
    }

    public final  LocationManager getLocationManager() {
        return this.locationManager;
    }

    public final void setLocationManager(  LocationManager var1) {
        this.locationManager = var1;
    }

    public LocationHelper(Activity activity,  LocationManager locationManager) {
        super();
        this.activity = activity;
        this.locationManager = locationManager;
        this.INTERVAL = 5000;
        this.FAST_INTERVAL = 2000;
        this.PERMISSIONS = new String[]{"android.permission.ACCESS_FINE_LOCATION"};
        if (this.fusedLocationClient == null) {
            this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(this.activity);
        }

        this.createLocationRequest();
        this.createLocationCallBack();
    }

    // $FF: synthetic method
    public static final double access$getLongitude$p(LocationHelper $this) {
        return $this.longitude;
    }

    // $FF: synthetic method
    public static final double access$getLatitude$p( LocationHelper $this) {
        return $this.latitude;
    }

    // $FF: synthetic method
    public static final boolean access$getPermissionLocationGranted$p( LocationHelper $this) {
        return $this.permissionLocationGranted;
    }
}



