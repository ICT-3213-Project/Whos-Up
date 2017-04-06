package lightning.structby.whosup;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Geocoder;
import android.location.Location;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//TODO: Don't show events which are behind in time
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final float DEFAULT_ZOOM = 17;
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    ProgressDialog progressDialog;

    private GoogleMap mMap;
    private static final String TAG = MapsActivity.class.getSimpleName();

    private CameraPosition mCameraPosition;
    private LatLng mDefaultLocation;
    private Location mLastKnownLocation;
    private GoogleApiClient mGoogleApiClient;
    private boolean mLocationPermissionGranted;
    private List<Marker> markers;

    private DatabaseReference eventref;
    private FirebaseUser firebaseUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        progressDialog = new ProgressDialog(MapsActivity.this);
        progressDialog.setMessage("Retrieving");
        progressDialog.show();

        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */,
                        this /* OnConnectionFailedListener */)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();


        // Get signed in user
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                RoundedImageView profileImage = (RoundedImageView) findViewById(R.id.profile_image);
                User user = dataSnapshot.getValue(User.class);
                String encodedImage = user.getProfileImage();
                byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                profileImage.setImageBitmap(decodedByte);

                // Hide the loading screen
                progressDialog.hide();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
            super.onSaveInstanceState(outState);
        }
    }

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.style_json));

            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }

        // Padding for location button
        mMap.setPadding(0, 200, 0, 0);

        mMap.setInfoWindowAdapter(new PopupAdapter(getLayoutInflater()));

        // Marker on click listener
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                int index = markers.indexOf(marker);

                final HorizontalScrollView cardScrollView = (HorizontalScrollView) findViewById(R.id.card_scroll_view);
                LinearLayout cardHolder = (LinearLayout) findViewById(R.id.card_holder);
                final CardView eventCV = (CardView) cardHolder.getChildAt(index);

                cardScrollView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        cardScrollView.smoothScrollTo(eventCV.getLeft(), 0);
                    }
                },100);

                mMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
                marker.showInfoWindow();
                return true;
            }
        });

        // Info window click: opens event detail actvity
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Intent i = new Intent(MapsActivity.this, EventDetailsActivity.class);
                DataSnapshot eventSnapshot = (DataSnapshot) marker.getTag();
                Event event = eventSnapshot.getValue(Event.class);
                String eventJson = (new Gson()).toJson(event);
                i.putExtra("event", eventJson);
                i.putExtra("eventId", eventSnapshot.getKey());
                startActivity(i);
            }
        });

        updateLocationUI();

        getDeviceLocation();

        getEvents();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Sorry, an error occured!", Toast.LENGTH_SHORT).show();
    }


    private void getDeviceLocation() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }

        if (mLocationPermissionGranted) {
            mLastKnownLocation = LocationServices.FusedLocationApi
                    .getLastLocation(mGoogleApiClient);
        }

        // Set the map's camera position to the current location of the device.
        if (mCameraPosition != null) {
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(mCameraPosition));
        } else if (mLastKnownLocation != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(mLastKnownLocation.getLatitude(),
                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));

            // Set "Events around you in.."
            TextView personCity = (TextView) findViewById(R.id.person_city);
            Geocoder geocoder = new Geocoder(this);
            try {
                personCity.setText(geocoder.getFromLocation(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude(), 1).get(0).getLocality());
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            Log.d(TAG, "Current location is null. Using defaults.");
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }

        if (mLocationPermissionGranted) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
        } else {
            mMap.setMyLocationEnabled(false);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            mLastKnownLocation = null;
        }
    }

    private void getEvents() {

        eventref = FirebaseDatabase.getInstance().getReference("Events");
        final LinearLayout cardHolder = (LinearLayout) findViewById(R.id.card_holder);

        eventref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Clear all events
                cardHolder.removeAllViews();
                mMap.clear();
                markers = new ArrayList<>();

                // Get new ones
                for(DataSnapshot eventSnapshot:dataSnapshot.getChildren()) {
                    Event event = eventSnapshot.getValue(Event.class);
                    Marker marker = mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(event.getPlaceLat(), event.getPlaceLng()))
                            .title(event.getEventName())
                            .snippet(event.getEventDate() + "\n" + event.getEventTime())
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                    marker.setTag(eventSnapshot);

                    markers.add(marker);
                    generateLayout(event);
                }

                // Add listeners for cards
                for(int i=0; i<cardHolder.getChildCount(); i++) {
                    final int val = i;
                    cardHolder.getChildAt(i).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mMap.animateCamera(CameraUpdateFactory.newLatLng(markers.get(val).getPosition()));
                            markers.get(val).showInfoWindow();

                            final HorizontalScrollView cardScrollView = (HorizontalScrollView) findViewById(R.id.card_scroll_view);
                            cardScrollView.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    cardScrollView.smoothScrollTo(cardHolder.getChildAt(val).getLeft(), 0);
                                }
                            },100);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void generateLayout(Event event) {
        CardView cardView = (CardView) getLayoutInflater().inflate(R.layout.map_event_fragment, null);

        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200, getResources().getDisplayMetrics());
        int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 150, getResources().getDisplayMetrics());

        RelativeLayout.LayoutParams cardViewLP = new RelativeLayout.LayoutParams(width, height);
        cardViewLP.setMargins(30, 0, 0, 0);
        cardView.setLayoutParams(cardViewLP);

        TextView eventName = (TextView) cardView.findViewById(R.id.event_name);
        TextView eventPlace = (TextView) cardView.findViewById(R.id.event_place);
        TextView eventDate = (TextView) cardView.findViewById(R.id.event_date);
        TextView eventDist = (TextView) cardView.findViewById(R.id.event_dist);

        Location eventLocation = new Location("");
        eventLocation.setLatitude(event.getPlaceLat());
        eventLocation.setLongitude(event.getPlaceLng());

        if(event.getEventName()!= null && event.getEventName().length() > 20) {
            eventName.setText(event.getEventName().substring(0, 19) + "...");
        } else {
            eventName.setText(event.getEventName());
        }

        if(event.getPlaceName()!= null && event.getPlaceName().length() > 25) {
            eventPlace.setText(event.getPlaceName().substring(0, 24) + "...");
        } else {
            eventPlace.setText(event.getPlaceName());
        }

        eventDate.setText(event.getEventDate());
        eventDist.setText(String.format("%.2f", mLastKnownLocation.distanceTo(eventLocation)/1000) + " km");


        //Fall-through ifs
        if(event.getPeopleAttendingCount() >= 1) {
            final RoundedImageView personAttendingImage = (RoundedImageView) cardView.findViewById(R.id.people1);
            String uid = event.getPeopleAttending().get(0);
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(uid);

            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    if(user != null) {
                        String encodedImage = user.getProfileImage();
                        byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        personAttendingImage.setImageBitmap(decodedByte);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else {
            RoundedImageView personAttendingImage = (RoundedImageView) cardView.findViewById(R.id.people1);
            cardView.removeView(personAttendingImage);
        }

        if(event.getPeopleAttendingCount() >= 2) {
            final RoundedImageView personAttendingImage = (RoundedImageView) cardView.findViewById(R.id.people2);
            String uid = event.getPeopleAttending().get(1);
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(uid);

            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    if(user != null) {
                        String encodedImage = user.getProfileImage();
                        byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        personAttendingImage.setImageBitmap(decodedByte);
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        } else {
            RoundedImageView personAttendingImage = (RoundedImageView) cardView.findViewById(R.id.people2);
            cardView.removeView(personAttendingImage);
        }

        if(event.getPeopleAttendingCount() > 2) {
            CardView peopleAttendingCV = (CardView) cardView.findViewById(R.id.card_view_inner);
            TextView peopleCountTV = (TextView) peopleAttendingCV.findViewById(R.id.people_count);
            peopleCountTV.setText("+" + (event.getPeopleAttendingCount()-2));
        } else {
            CardView peopleAttendingCV = (CardView) cardView.findViewById(R.id.card_view_inner);
            cardView.removeView(peopleAttendingCV);
        }

        LinearLayout cardHolder = (LinearLayout) findViewById(R.id.card_holder);
        cardHolder.addView(cardView);

    }

    public void addEvent(View v) {
        Intent i = new Intent(MapsActivity.this, EventActivity.class);
        startActivity(i);
    }

    public void openProfile(View v) {
        Intent i = new Intent(MapsActivity.this, ProfileActivity.class);
        startActivity(i);
    }


}