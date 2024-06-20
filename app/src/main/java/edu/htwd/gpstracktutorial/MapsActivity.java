package edu.htwd.gpstracktutorial;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

import edu.htwd.gpstracktutorial.databinding.ActivityMapsBinding;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    List<Location> savedLocations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        MyApplication myApplication = (MyApplication) getApplication();
        savedLocations = myApplication.getMyLocations();
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
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng dresden = new LatLng(51.03645, 13.73524);
        mMap.addMarker(new MarkerOptions().position(dresden).title("Dresden"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(dresden));

        LatLng lastlocationPlaced = dresden;


        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(dresden);
        Geocoder geocoder = new Geocoder(MapsActivity.this);
        try {
            List<Address> addresses = geocoder.getFromLocation(51.03645,13.73524,1);
            markerOptions.title(addresses.get(0).getAddressLine(0));
            mMap.addMarker(markerOptions);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }





        /**for (Location location: savedLocations
        ){
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            Geocoder geocoder = new Geocoder(MapsActivity.this);
            try {
                List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
                markerOptions.title(addresses.get(0).getAddressLine(0));
                mMap.addMarker(markerOptions);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }**/

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lastlocationPlaced, 15.0f));

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {

                //count numbers of times the pin is click
                Integer clicks = (Integer) marker.getTag();
                if(clicks == null){
                    clicks = 0;
                }

                clicks++;
                marker.setTag(clicks);
                Toast.makeText(MapsActivity.this, "Marker" + marker.getTitle() + " was click " + marker.getTag() + " times", Toast.LENGTH_SHORT).show();

                return false;
            }
        });
    }
}