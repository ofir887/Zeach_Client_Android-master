package com.zeach.ofirmonis.zeach;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executor;

import static android.content.Context.LOCATION_SERVICE;
import static com.facebook.FacebookSdk.getApplicationContext;
import static com.facebook.GraphRequest.TAG;

/**
 * Created by ofirmonis on 31/05/2017.
 */

public class MapFragment extends android.support.v4.app.Fragment implements OnMapReadyCallback, LocationListener, View.OnClickListener {

    private View rootView;
    private GoogleMap mGoogleMap;
    private MapView mMapView;
    private Button SearchButton;
    private AutoCompleteTextView autoCompleteSearch;
    private LocationManager locationManager;
    private LatLng userLocation;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        this.rootView = inflater.inflate(R.layout.map_fragment, container, false);
        this.SearchButton = (Button) rootView.findViewById(R.id.beach_search_button);
        //this.autoCompleteSearch = (AutoCompleteTextView) rootView.findViewById(R.id.autoCompleteSearchTextView);
//        this.autoCompleteSearch.setOnClickListener(this);
        locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);

        return this.rootView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mMapView = (MapView) rootView.findViewById(R.id.map);
        if (mMapView != null) {
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(getApplicationContext());


        mGoogleMap = googleMap;

        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        // CameraPosition cameraPosition = CameraPosition.builder().target(new LatLng(32.144053, 34.791247)).zoom(16).bearing(0).tilt(45).build();
        // googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        //

        ///
        checkPermissions();
        try {
            if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;

                }
                locationManager.requestLocationUpdates("gps", 5000, 1000, this);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        //
    }


    @Override
    public void onLocationChanged(Location location) {
        Log.d("location",location.toString());
        this.userLocation = new LatLng(location.getLatitude(),location.getLongitude());
        mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(CameraPosition.builder().target(this.userLocation).zoom(16).bearing(0).tilt(45).build()));

        /*
        URL url = null;
        Bitmap myBitmap = null;

        try {
            url = new URL("https://graph.facebook.com/807063519471323/picture?height=200&width=200&migration_overrides=%7Boctober_2012%3Atrue%7D");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            InputStream input = connection.getInputStream();
            myBitmap = BitmapFactory.decodeStream(input);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        */


        mGoogleMap.addMarker((new MarkerOptions().position(this.userLocation).title("my location")));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {
        Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(i);
    }

    @Override
    public void onClick(View v) {
        if (v == autoCompleteSearch){
            this.autoCompleteSearch.setText("");
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 10:
                checkPermissions();
                break;
            default:
                break;
        }
    }
    public void checkPermissions(){
        // first check for permissions
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION,android.Manifest.permission.ACCESS_FINE_LOCATION,android.Manifest.permission.INTERNET}
                        ,10);
            }
            return;
        }
    }

}
