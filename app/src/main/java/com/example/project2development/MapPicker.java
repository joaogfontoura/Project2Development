package com.example.project2development;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapPicker extends AppCompatActivity implements OnMapReadyCallback{

    private GoogleMap mMap;
    Button confirmBtn;
    LatLng coordinates;

    private static final String TAG = "MapPicker";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_picker);

        Button btn1 = (Button) findViewById(R.id.selectBtn);

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();

                double latitude = coordinates.latitude;
                double longitude = coordinates.longitude;

                Bundle b = new Bundle();

                b.putDouble("Lat", latitude);
                b.putDouble("Long", longitude);

                intent.putExtras(b);


                setResult(RESULT_OK, intent);
                finish();
            }
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }




    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(final GoogleMap googleMap) {
        //LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());

        LatLng latLng = new LatLng(-33.87365, 151.20689);

        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getApplicationContext(), R.raw.map_style));

        final MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("Select here").draggable(true).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        coordinates = latLng;
        googleMap.addMarker(markerOptions);
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        // v = zoom ( bigger value = closer in zoom )
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,14));

        googleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {

            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                LatLng markerpos = marker.getPosition();


                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

                List<Address> addresses  = null;
                try {
                    addresses = geocoder.getFromLocation(markerpos.latitude,markerpos.longitude, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                coordinates = markerpos;

                marker.setTitle(addresses.get(0).getAddressLine(0));
                marker.showInfoWindow();

            }
        });

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                googleMap.clear();
                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

                List<Address> addresses  = null;
                try {
                    addresses = geocoder.getFromLocation(latLng.latitude,latLng.longitude, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                coordinates = latLng;

                final MarkerOptions markerOptions = new MarkerOptions().position(latLng).title(addresses.get(0).getAddressLine(0)).draggable(true).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

                googleMap.addMarker(markerOptions).showInfoWindow();
            }
        });



    }


}