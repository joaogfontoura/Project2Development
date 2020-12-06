package com.example.project2development;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class AttractionMenu extends AppCompatActivity implements OnMapReadyCallback {

    // usado pra fazer log.d e debugar as variaveis
    private static final String TAG = "AttractionMenu";


    FusedLocationProviderClient fusedLocationProviderClient;
    Button BtnRefresh;
    private Marker mMarker;
    Location currentLocation;
    private static final int REQUEST_CODE = 101;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private CollectionReference mColRef = db.collection("Attraction Collection");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attraction_menu);

        BtnRefresh = findViewById(R.id.btnLocation);

        //fused location inicializer
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fetchLastLocation();

    }


    // function used to fetch the location of the user

    // comment para test
    // test 2

    private void fetchLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location != null){
                    currentLocation = location;
                    SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map);
                    supportMapFragment.getMapAsync(AttractionMenu.this);

                }
            }
        });
    }

    
    public void onMapReady(GoogleMap googleMap){
        //LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());

        LatLng latLng = new LatLng(-33.87365, 151.20689);
        Log.d(TAG, "LONG AND LAT"+latLng);
        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getApplicationContext(), R.raw.map_style));

        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("You are Here").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        Log.d(TAG, "MARKER OPTIONS"+markerOptions);
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        // v = zoom ( bigger value = closer in zoom )
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,14));
        googleMap.addMarker(markerOptions);
        Log.d(TAG, "GOOGLE MAPS"+googleMap);
        populateMarkers(googleMap);

        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if(!"You are Here".equals(marker.getTitle())){

                    String attId = marker.getSnippet();

                    Intent markerDetails = new Intent(getApplicationContext(), AttractionDetails.class);

                    markerDetails.putExtra("Id",attId);

                    startActivity(markerDetails);


                }

                return false;
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case REQUEST_CODE:
                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    fetchLastLocation();
                }
                break;
        }
    }

    public void populateMarkers(final GoogleMap googleMap){

        mColRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){

                    GeoPoint coordinates = documentSnapshot.getGeoPoint("Coordinates");
                    double coordinatesLat = coordinates.getLatitude();
                    double coordinatesLng = coordinates.getLongitude();

                    final LatLng finalcoordinates = new LatLng(coordinatesLat, coordinatesLng);

                    Marker mMarker = googleMap.addMarker(
                            new MarkerOptions()
                                    .position(finalcoordinates)
                                    .snippet(documentSnapshot.getId())
                    );

                }
            }
        });

    }



    public void returnMainPage(View view) {
        startActivity(new Intent(getApplicationContext(), MainPage.class));
        finish();
    }

    public void refreshMap(View view){
        fetchLastLocation();
    }

}