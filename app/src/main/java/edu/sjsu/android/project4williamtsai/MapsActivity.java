package edu.sjsu.android.project4williamtsai;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LoaderManager.LoaderCallbacks<Cursor> {
    private final String AUTHORITY = "edu.sjsu.android.project4williamtsai";
    private final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    private final LatLng LOCATION_UNIV = new LatLng(37.335371, -121.881050);
    private final LatLng LOCATION_CS = new LatLng(37.333714, -121.881860);

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        LoaderManager.getInstance(this).restartLoader(0,  null, this);
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
//        mMap.addMarker(new MarkerOptions().
//                position(LOCATION_CS).
//                title("Find me here!"));
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng position) {
                mMap.addMarker(new MarkerOptions().position(position));
                ContentValues values = new ContentValues();
                values.put("latitude", position.latitude);
                values.put("longitude", position.longitude);
                values.put("zoom", mMap.getCameraPosition().zoom);
                values.put("type", mMap.getMapType());
                new InsertTask().execute(values);
            }
        });

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                new DeleteTask().execute();
                mMap.clear();
            }
        });
    }

    public void getLocation(View view){
        GPSTracker tracker = new GPSTracker(this);
        tracker.getLocation();
    }

    public void switchView(View view) {
        CameraUpdate update = null;
        if (view.getId() == R.id.city) {
            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            update = CameraUpdateFactory.newLatLngZoom(LOCATION_UNIV, 10f);
        } else if (view.getId() == R.id.univ) {
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            update = CameraUpdateFactory.newLatLngZoom(LOCATION_UNIV, 14f);
        } else if (view.getId() == R.id.cs) {
            mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
            update = CameraUpdateFactory.newLatLngZoom(LOCATION_CS, 18f);
        }    mMap.animateCamera(update);
    }

    public void uninstall(View v) {
        Intent delete = new Intent(Intent.ACTION_DELETE,
                Uri.parse("package:" + getPackageName()));
        startActivity(delete);
    }

    public void getAllLocations(MapsActivity view) {
        try (Cursor c = getContentResolver().
                query(CONTENT_URI, null, null, null, "latitude")) {
            if (c.moveToFirst()) {
                String result = "Locations: \n";
                do {
                    result = result.concat
                            (c.getDouble(0) + "\t");

                    result = result.concat("\n");
                } while (c.moveToNext());
                Toast.makeText(this, result, Toast.LENGTH_LONG).show();
            }
        }
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        return new CursorLoader(this, CONTENT_URI, null, null, null, null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {
            //Toast.makeText(this, "test", Toast.LENGTH_LONG).show();
            CameraUpdate update = null;
            int mapType = GoogleMap.MAP_TYPE_NORMAL;
            do {
                LatLng position = new LatLng(data.getDouble(1), data.getDouble(2));
                mMap.addMarker(new MarkerOptions().position(position));
                update = CameraUpdateFactory.newLatLngZoom(position, data.getFloat(3));
                mapType = data.getInt(4);
            } while (data.moveToNext());
            mMap.animateCamera(update);
            mMap.setMapType(mapType);
//            Toast.makeText(this, "William Tsai is at \n"   +
//                            "Lat " + position.latitude + "\nLong: " + position.longitude,
//                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }

    private class InsertTask extends AsyncTask<ContentValues, Void, Void> {
        @Override
        protected Void doInBackground(ContentValues... contentValues){
            getContentResolver().insert(CONTENT_URI, contentValues[0]);
            return null;
        }
    }

    private class DeleteTask extends AsyncTask<ContentValues, Void, Void> {
        @Override
        protected Void doInBackground(ContentValues... contentValues){
            getContentResolver().delete(CONTENT_URI, null,null);
            return null;
        }
    }
}