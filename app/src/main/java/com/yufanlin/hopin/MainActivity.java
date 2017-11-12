package com.yufanlin.hopin;

import android.Manifest;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.yufanlin.hopin.POJO.Example;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class MainActivity
        extends AppCompatActivity
        implements OnMapReadyCallback,
                   GoogleApiClient.ConnectionCallbacks,
                   GoogleApiClient.OnConnectionFailedListener {

    private static final int ERROR_DIALOG_REQUEST = 9000; //Dialog request
    private static final int GRANT_PERMISSION_REQUEST = 8000; //Permission request

    private GoogleMap mMap; //An instance of GoogleMap
    private GoogleApiClient mLocationClient; //An instance of GoogleApiClient

    LatLng origin;
    LatLng dest;
    ArrayList<LatLng> MarkerPoints = new ArrayList<>();
    TextView ShowDistanceDuration;
    Polyline line;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(servicesOK()){
            setContentView(R.layout.activity_map);
            ShowDistanceDuration = (TextView) findViewById(R.id.show_distance_time);

            if(initMap()){

                mLocationClient = new GoogleApiClient.Builder(this)
                        .addApi(LocationServices.API)
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .build();

                mLocationClient.connect();

            } else {
                Toast.makeText(this, "Map not connected!", Toast.LENGTH_SHORT).show();
            }
        } else {
            setContentView(R.layout.activity_main);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case GRANT_PERMISSION_REQUEST: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Enable location services.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Disable location services.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    //Check for APK
    public boolean servicesOK(){

        int isAvailable =
                GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);

        if(isAvailable == ConnectionResult.SUCCESS){
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(isAvailable)){
            Dialog dialog =
                    GoogleApiAvailability.getInstance().getErrorDialog(this, isAvailable, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(this, "Cannot connect to mapping service", Toast.LENGTH_SHORT).show();
        }

        return false;
    }

    //Initiate Google Map
    private boolean initMap(){

        if(mMap == null){
            SupportMapFragment mapFragment =
                    (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }

        return true;
    }

    //Find location
    public void geoLocate(View v) throws IOException{

        hideSoftKeyboard(v);

        TextView tv = (TextView) findViewById(R.id.editText1);
        String searchString = tv.getText().toString();

        if(!searchString.equals("")) {
            Toast.makeText(this, "Searching for: " + searchString, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Search bar cannot leave blank!" + searchString, Toast.LENGTH_SHORT).show();
        }

        Geocoder gc = new Geocoder(this);
        List<Address> list = gc.getFromLocationName(searchString, 1);

        if(list.size() > 0) {

            Address add = list.get(0);
            String locality = add.getLocality();
            Toast.makeText(this, "Found: " + locality, Toast.LENGTH_SHORT).show();

            double lat = add.getLatitude();
            double lng = add.getLongitude();
            gotoLocation(lat, lng, 15);

            MarkerOptions options = new MarkerOptions()
                    .title(locality)
                    .position(new LatLng(lat, lng));

            origin_and_destination(options.getPosition());
        }
    }

    //Soft keyboard
    private void hideSoftKeyboard(View v){

        InputMethodManager imm =
                (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);

        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    //Go to location
    private void gotoLocation(double lat, double lng, float zoom){

        LatLng latLng = new LatLng(lat, lng);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latLng, zoom);
        mMap.animateCamera(update);
    }

    //Show current location
    public void showCurrentLocation(MenuItem item) {

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            Location currentLocation = LocationServices.FusedLocationApi
                    .getLastLocation(mLocationClient);

            if (currentLocation == null) {
                Toast.makeText(this, "Couldn't connect!", Toast.LENGTH_SHORT).show();
            } else {
                LatLng latLng = new LatLng(
                        currentLocation.getLatitude(),
                        currentLocation.getLongitude()
                );
                CameraUpdate update = CameraUpdateFactory.newLatLngZoom(
                        latLng, 15
                );
                mMap.animateCamera(update);

                origin_and_destination(latLng);
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap map){

        mMap = map;

        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    GRANT_PERMISSION_REQUEST);
        }

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                origin_and_destination(point);
            }
        });

        Button btnDriving = (Button) findViewById(R.id.button2);
        btnDriving.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                build_retrofit_and_get_response("driving");
            }
        });
    }

    public void origin_and_destination(LatLng point){

        //Clearing map and generating new marker points if user clicks on map more than two times.
        if (MarkerPoints.size() > 1) {
            mMap.clear();
            MarkerPoints.clear();
            ShowDistanceDuration.setText("");
        }

        //Adding new item to the ArrayList.
        MarkerPoints.add(point);

        //Creating MarketOptions.
        MarkerOptions options = new MarkerOptions();

        //Setting the position of the marker.
        options.position(point);

        /**
         * For the start location, the color of marker is GREEN and
         * for the end location, the color of marker is RED.
         */
        if (MarkerPoints.size() == 1) {
            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        } else if (MarkerPoints.size() == 2) {
            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        }

        //Add new marker to the Google Map Android API V2.
        mMap.addMarker(options);

        //Checks, whether start and end locations are captured.
        if (MarkerPoints.size() == 2) {
            origin = MarkerPoints.get(0);
            dest = MarkerPoints.get(1);
        }
    }

    private void build_retrofit_and_get_response(String type){

        if (MarkerPoints.size() < 2) {
            Toast.makeText(this, "You need two points to make a line!", Toast.LENGTH_SHORT).show();
        } else {

            origin = MarkerPoints.get(0);
            dest = MarkerPoints.get(1);

            String url = "https://maps.googleapis.com/maps/";

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            RetrofitMaps service = retrofit.create(RetrofitMaps.class);

            Call<Example> call = service.getDistanceDuration("metric", origin.latitude + "," + origin.longitude, dest.latitude + "," + dest.longitude, type);

            call.enqueue(new Callback<Example>() {
                @Override
                public void onResponse(Response<Example> response, Retrofit retrofit) {

                    try {

                        if (line != null) {
                            line.remove();
                        }

                        for (int i = 0; i < response.body().getRoutes().size(); i++) {
                            String distance = response.body().getRoutes().get(i).getLegs().get(i).getDistance().getText();
                            String time = response.body().getRoutes().get(i).getLegs().get(i).getDuration().getText();
                            ShowDistanceDuration.setText("Distance:" + distance + ", Duration:" + time);
                            String encodedString = response.body().getRoutes().get(0).getOverviewPolyline().getPoints();
                            List<LatLng> list = decodePoly(encodedString);
                            line = mMap.addPolyline(new PolylineOptions()
                                    .addAll(list)
                                    .width(20)
                                    .color(Color.RED)
                                    .geodesic(true)
                            );
                        }
                    } catch (Exception e) {
                        Log.d("onResponse", "There is an error");
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    Log.d("onFailure", t.toString());
                }
            });
        }

    }

    /**
     * DECODE
     */
    private List<LatLng> decodePoly(String encoded) {
        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng( (((double) lat / 1E5)),
                    (((double) lng / 1E5) ));
            poly.add(p);
        }

        return poly;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Toast.makeText(this, "Ready to map!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}