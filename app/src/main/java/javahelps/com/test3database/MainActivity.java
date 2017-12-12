package javahelps.com.test3database;

import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.ArrayList;
import java.util.List;
import android.app.Dialog;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.util.Map;
import android.*;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "MainActivity";
    private static final int ERROR_DIALOG_REQUEST = 9001;
    private ListView listView;
    private Marker mMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getLocationPermission();

        ListView mListView = (ListView) findViewById(R.id.listView);
        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this);
        databaseAccess.open();
        final ArrayList<HouseDetails> retrivedetails = databaseAccess.housedetails();
        final ArrayList<LatLng> retrivePositions = databaseAccess.position_details();
        databaseAccess.close();

        PersonListAdapter adapter = new PersonListAdapter(this, R.layout.adapter_view_layout, retrivedetails);
        mListView.setAdapter(adapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                //Creates a new marker set
                MarkerOptions options = new MarkerOptions();
                //clears the existing map
                mMap.clear();
                //getting all the markers.
                markLocations();
                /*for(LatLng location : retrivePositions) {
                    options.position(location)
                            .icon(BitmapDescriptorFactory.defaultMarker(0f));
                    mMap.addMarker(options);
                }*/
                //options.icon(BitmapDescriptorFactory.defaultMarker(0f));
                //moveCamera(retrivePositions.get(i),12f);
                Log.d(TAG, "onItemClick: postion obtained" + retrivePositions.get(i));
                        options.position(retrivePositions.get(i))
                        .title(String.valueOf(retrivedetails.get(i).getAddress()))
                        .alpha(1f)
                        .icon(BitmapDescriptorFactory.defaultMarker(200f));
                mMap.addMarker(options);
                moveCamera(retrivePositions.get(i),17f);

            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onMapready : map is ready");
        mMap = googleMap;

        if (mLocationPermissionGranted) {
            getDeviceLocation();

            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.setBuildingsEnabled(true);
            mMap.setIndoorEnabled(true);

        }
        
    }

    private static final String FINE_LOCATION = android.Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = android.Manifest.permission.ACCESS_FINE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15;

    //var
    private Boolean mLocationPermissionGranted = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        Log.i("FilterClicked",  "Filter");
        Intent i = new Intent(this, FilterActivity.class);
        startActivity(i);
        return super.onOptionsItemSelected(item);
    }


    public boolean isServicesOK() {
        Log.d(TAG, "isServiceOk: checking google services version");
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);

        if (available == ConnectionResult.SUCCESS) {
            //everything is fine and the user can make the map requests
            Log.d(TAG, "isServiceOk: Google Play Services is working");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            //an error occured but we can resolve it
            Log.d(TAG, "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private void getDeviceLocation () {
        Log.d(TAG, "getDeviceLocation: getting the device current location");
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            if (mLocationPermissionGranted) {
                Task<Location> location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: found location!");
                            Location currentLocation = (Location) task.getResult();
                            //moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), DEFAULT_ZOOM);
                        } else {
                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(MainActivity.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e(TAG, "getDeviceLocation: SecurityException" + e.getMessage());

        }
        markLocations();
    }

    private void moveCamera(LatLng latLag, float zoom) {
        Log.d(TAG, "moveCamera: moving camera to :lat" + latLag.latitude + ", lng: " + latLag.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLag, zoom));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLag, zoom));
    }

    private void initMap() {
        Log.d(TAG, "initMap: initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(MainActivity.this);
    }

    //Location Permission method
    private void getLocationPermission() {
        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permission = {android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
                initMap();
            } else {
                ActivityCompat.requestPermissions(this, permission, LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this, permission, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void markLocations(){
        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this);
        databaseAccess.open();
        ArrayList<HouseDetails> retrivedetails = databaseAccess.housedetails();
        ArrayList<LatLng> retrivePositions = databaseAccess.position_details();
        int i=0;

        //Adding markers according to the location values from db(on state zip and city selected)
        for(LatLng location : retrivePositions){
            Log.d(TAG, "getDeviceLocation: location" + location);
            MarkerOptions options = new MarkerOptions()
                    .position(location)
                    .title(String.valueOf(retrivedetails.get(i).getAddress()))
                    .alpha(0.85f);
                    if((Integer.parseInt(retrivedetails.get(i).getPrice()) > 0) && (Integer.parseInt(retrivedetails.get(i).getPrice()) <= 500000)){
                        options.icon(BitmapDescriptorFactory.defaultMarker(60f));;
                    }
                    else if ((Integer.parseInt(retrivedetails.get(i).getPrice()) > 500000) && (Integer.parseInt(retrivedetails.get(i).getPrice()) <= 1000000)){
                        options.icon(BitmapDescriptorFactory.defaultMarker(180f));;
                    }
                    else if ((Integer.parseInt(retrivedetails.get(i).getPrice()) > 1000000) && (Integer.parseInt(retrivedetails.get(i).getPrice()) <= 1500000)){
                        options.icon(BitmapDescriptorFactory.defaultMarker(30f));;
                    }else {
                        options.icon(BitmapDescriptorFactory.defaultMarker(0f));;
                    }


            mMap.addMarker(options);
            Log.d(TAG, "getDeviceLocation: title:" + String.valueOf(retrivedetails.get(i).getAddress()));
            moveCamera(location,12f);
            i++;
        }

        databaseAccess.close();

    }

    public void jump(View view){
        String packageName = "io.ionic.starter";
        Intent intent = getPackageManager().getLaunchIntentForPackage(packageName);

        if(intent == null) {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="+packageName));
        }

        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called");
        mLocationPermissionGranted = false;

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: permission failed");
                            return;
                        }
                        Log.d(TAG, "onRequestPermissionsResult: permission granted");
                        mLocationPermissionGranted = true;
                        //initialize our  map
                        initMap();
                    }
                }
            }
        }
    }
}