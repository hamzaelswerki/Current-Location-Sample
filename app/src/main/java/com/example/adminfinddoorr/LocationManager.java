package com.example.adminfinddoorr;

import android.location.Location;

public interface LocationManager {
    void onLocationChanged( Location location);
    void getLastKnownLocation(Location location);
}
