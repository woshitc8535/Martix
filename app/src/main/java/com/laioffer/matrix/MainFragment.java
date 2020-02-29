package com.laioffer.matrix;


import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;


import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;
import static com.laioffer.matrix.Config.listItems;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment implements OnMapReadyCallback, ReportDialog.DialogCallBack,GoogleMap.OnMarkerClickListener {
        private static final int REQUEST_CAPTURE_IMAGE = 100;
    private static final int REQ_CODE_SPEECH_INPUT = 101;
    private final String path = Environment.getExternalStorageDirectory() + "/temp.png";
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };


    private MapView mapView;
    private View view;
    private GoogleMap googleMap;
    private LocationTracker locationTracker;
    private FloatingActionButton fabReport;
    private ReportDialog dialog;
    private FloatingActionButton fabFocus;
    private FloatingActionButton speakNow;

    private DatabaseReference database;

    private FirebaseStorage storage;
    private StorageReference storageRef;

    private Marker currentMarker;




    //event information part
    private BottomSheetBehavior bottomSheetBehavior;
    private ImageView mEventImageLike;
    private ImageView mEventImageComment;
    private ImageView mEventImageType;
    private TextView mEventTextLike;
    private TextView mEventTextType;
    private TextView mEventTextLocation;
    private TextView mEventTextTime;
    private TrafficEvent mEvent;


    private void setupBottomBehavior() {
        //set up bottom up slide
        final View nestedScrollView = (View) view.findViewById(R.id.nestedScrollView);
        bottomSheetBehavior = BottomSheetBehavior.from(nestedScrollView);

        //set hidden initially
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        //set expansion speed
        bottomSheetBehavior.setPeekHeight(1000);

        mEventImageLike = (ImageView) view.findViewById(R.id.event_info_like_img);
        mEventImageComment = (ImageView) view.findViewById(R.id.event_info_comment_img);
        mEventImageType = (ImageView) view.findViewById(R.id.event_info_type_img);
        mEventTextLike = (TextView) view.findViewById(R.id.event_info_like_text);
        mEventTextType = (TextView) view.findViewById(R.id.event_info_type_text);
        mEventTextLocation = (TextView) view.findViewById(R.id.event_info_location_text);
        mEventTextTime = (TextView) view.findViewById(R.id.event_info_time_text);

        mEventImageLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int number = Integer.parseInt(mEventTextLike.getText().toString());
                database.child("events").child(mEvent.getId()).child("event_like_number").setValue(number + 1);
                mEventTextLike.setText(String.valueOf(number + 1));
                if (currentMarker != null && mEvent != null) {
                    mEvent.setEvent_like_number(number + 1);
                    currentMarker.setTag(mEvent);
                }

            }
        });

    }



    public MainFragment() {
        // Required empty public constructor
    }

    public static MainFragment newInstance() {
        
        Bundle args = new Bundle();
        
        MainFragment fragment = new MainFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public boolean onMarkerClick(Marker marker) {
        mEvent = (TrafficEvent) marker.getTag();
        if (mEvent == null) {
            return false;
        }
        currentMarker = marker;
        String user = mEvent.getEvent_reporter_id();
        String type = mEvent.getEvent_type();
        long time = mEvent.getEvent_timestamp();
        double latitude = mEvent.getEvent_latitude();
        double longitutde = mEvent.getEvent_longitude();
        int likeNumber = mEvent.getEvent_like_number();

        String description = mEvent.getEvent_description();
        marker.setTitle(description);
        mEventTextLike.setText(String.valueOf(likeNumber));
        mEventTextType.setText(type);


        final String url = mEvent.getImgUri();
        if (url == null) {
            mEventImageType.setImageDrawable(ContextCompat.getDrawable(getContext(), Config.trafficMap.get(type)));
        } else {
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    Log.d("Here", Thread.currentThread().getName());
//                    final Bitmap bitmap = Utils.getBitmapFromURL(url);
//                    getActivity().runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            mEventImageType.setImageBitmap(bitmap);
//                        }
//                    });
//                }
//            }).start();
            new AsyncTask<Void, Void, Bitmap>() {
                @Override
                protected Bitmap doInBackground(Void... voids) {
                    Bitmap bitmap = Utils.getBitmapFromURL(url);
                    return bitmap;
                }

                @Override
                protected void onPostExecute(Bitmap bitmap) {
                    super.onPostExecute(bitmap);
                    mEventImageType.setImageBitmap(bitmap);
                }
            }.execute();
        }


        mEventImageType.setImageDrawable(ContextCompat.getDrawable(getContext(), Config.trafficMap.get(type)));

        if (user == null) {
            user = "";
        }
        String info = "Reported by " + user + " " + Utils.timeTransformer(time);
        mEventTextTime.setText(info);


        int distance = 0;
        locationTracker = new LocationTracker(getActivity());
        locationTracker.getLocation();
        if (locationTracker != null) {
            distance = Utils.distanceBetweenTwoLocations(latitude, longitutde, locationTracker.getLatitude(), locationTracker.getLongitude());
        }
        mEventTextLocation.setText(distance + " miles away");

        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_HIDDEN) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        } else {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }

        return true;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_main, container, false);
        database = FirebaseDatabase.getInstance().getReference();

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        verifyStoragePermissions(getActivity());

        setupBottomBehavior();

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapView = (MapView) this.view.findViewById(R.id.event_map_view);


        fabReport = (FloatingActionButton)view.findViewById(R.id.fab);
        fabReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show dialog
                showDialog(null, null);
            }
        });


        fabFocus = (FloatingActionButton) view.findViewById(R.id.fab_focus);

        fabFocus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mapView.getMapAsync(MainFragment.this);
            }
        });

        speakNow = view.findViewById(R.id.voice);
        speakNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askSpeechInput("Hi speak something");
            }
        });


        if (mapView != null) {
            mapView.onCreate(null);
            mapView.onResume();// needed to get the map to display immediately
            mapView.getMapAsync(this);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    private void showDialog(String label, String prefillText) {
        int cx = (int) (fabReport.getX() + (fabReport.getWidth() / 2));
        int cy = (int) (fabReport.getY()) + fabReport.getHeight() + 56;

        dialog = ReportDialog.newInstance(getContext(), cx, cy, this, label, prefillText);



//        dialog = new ReportDialog(getContext());
        dialog.show();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(getContext());
        googleMap.setOnMarkerClickListener(this);
        this.googleMap = googleMap;
        this.googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                        getActivity(), R.raw.style_json));

        locationTracker = new LocationTracker(getActivity());
        locationTracker.getLocation();

        LatLng latLng = new LatLng(locationTracker.getLatitude(), locationTracker.getLongitude());

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng)      // Sets the center of the map to Mountain View
                .zoom(16)// Sets the zoom
                .bearing(90)           // Sets the orientation of the camera to east
                .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder

        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        MarkerOptions marker = new MarkerOptions().position(latLng).
                title("You");

        // Changing marker icon
        marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.boy));

        // adding marker
        googleMap.addMarker(marker);
        loadEventInVisibleMap();




//        double latitude = 17.385044;
//        double longitude = 78.486671;
//
//        // Create marker on google map
//        MarkerOptions marker = new MarkerOptions().position(
//                new LatLng(latitude, longitude)).title("This is your focus");
//
//        // Change marker Icon on google map
//        marker.icon(BitmapDescriptorFactory
//                .defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
//
//        // Add marker to google map
//        googleMap.addMarker(marker);
//
//
//        // Set up camera configuration, set camera to latitude = 17.385044, longitude = 78.486671, and set Zoom to 12
//        CameraPosition cameraPosition = new CameraPosition.Builder()
//                .target(new LatLng(latitude, longitude)).zoom(12).build();
//
//        // Animate the zoom process
//        googleMap.animateCamera(CameraUpdateFactory
//                .newCameraPosition(cameraPosition));

    }


    private String uploadEvent(String user_id, String editString, String event_type) {
        TrafficEvent event = new TrafficEvent();

        event.setEvent_type(event_type);
        event.setEvent_description(editString);
        event.setEvent_reporter_id(user_id);
        event.setEvent_timestamp(System.currentTimeMillis());
        event.setEvent_latitude(locationTracker.getLatitude());
        event.setEvent_longitude(locationTracker.getLongitude());
        event.setEvent_like_number(0);
        event.setEvent_comment_number(0);

        String key = database.child("events").push().getKey();
        event.setId(key);
        database.child("events").child(key).setValue(event, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    Toast toast = Toast.makeText(getContext(),
                            "The event is failed, please check your network status.", Toast.LENGTH_SHORT);
                    toast.show();
                    dialog.dismiss();
                } else {
                    Toast toast = Toast.makeText(getContext(), "The event is reported", Toast.LENGTH_SHORT);
                    toast.show();
                    //TODO: update map fragment
                }
            }
        });

        return key;
    }

    private void uploadImage(final String key) {
        File file = new File(path);
        if (!file.exists()) {
            dialog.dismiss();
            loadEventInVisibleMap();
            return;
        }


        Uri uri = Uri.fromFile(file);
        final StorageReference imgRef = storageRef.child("images/" + uri.getLastPathSegment() + "_" + System.currentTimeMillis());

        imgRef.putFile(uri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return imgRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                Uri downloadUri = task.getResult();
                database.child("events").child(key).child("imgUri").
                        setValue(downloadUri.toString());
                File file = new File(path);
                file.delete();
                dialog.dismiss();
                loadEventInVisibleMap();
            }
        });
    }



    @Override
    public void onSubmit(String editString, String event_type) {
        String key = uploadEvent(Config.username, editString, event_type);
        //upload image and link the image to the corresponding key
        uploadImage(key);


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CAPTURE_IMAGE: {
                if (resultCode == RESULT_OK && data != null && data.getExtras() != null) {
                    Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
                    if (dialog != null && dialog.isShowing()) {
                        dialog.updateImage(imageBitmap);
                    }
                    //Compress the image, this is optional
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    imageBitmap.compress(Bitmap.CompressFormat.PNG, 90, bytes);


                    File destination = new File(Environment.getExternalStorageDirectory(), "temp.png");
                    if (!destination.exists()) {
                        try {
                            destination.createNewFile();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                    FileOutputStream fo;
                    try {
                        fo = new FileOutputStream(destination);
                        fo.write(bytes.toByteArray());
                        fo.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;

            }
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    if (result.size() > 0) {
                        final String sentence = result.get(0);
                        boolean isMatch = false;
                        for (int i = 0; i < listItems.size(); i++) {
                            final String label = listItems.get(i).getDrawable_label();
                            if (sentence.toLowerCase().contains(label.toLowerCase())) {
                                showDialog(label, sentence);;
                                isMatch = true;
                                break;
                            }
                        }
                        if (!isMatch) {
                            askSpeechInput("Try again");
                        }
                    }
                }
                break;
            }
                default:
        }
    }
    private void loadEventInVisibleMap() {
        database.child("events").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot noteDataSnapshot : dataSnapshot.getChildren()) {
                    TrafficEvent event = noteDataSnapshot.getValue(TrafficEvent.class);
                    double eventLatitude = event.getEvent_latitude();
                    double eventLongitude = event.getEvent_longitude();

                    locationTracker.getLocation();
                    double centerLatitude = locationTracker.getLatitude();
                    double centerLongitude = locationTracker.getLongitude();


                    int distance = Utils.distanceBetweenTwoLocations(centerLatitude, centerLongitude,
                            eventLatitude, eventLongitude);

                    if (distance < 20) {
                        LatLng latLng = new LatLng(eventLatitude, eventLongitude);
                        MarkerOptions marker = new MarkerOptions().position(latLng);

                        // Changing marker icon
                        String type = event.getEvent_type();
                        Bitmap icon = BitmapFactory.decodeResource(getContext().getResources(),
                                Config.trafficMap.get(type));

                        Bitmap resizeBitmap = Utils.getResizedBitmap(icon, 130, 130);

                        marker.icon(BitmapDescriptorFactory.fromBitmap(resizeBitmap));

                        // adding marker
                        Marker mker = googleMap.addMarker(marker);
                        mker.setTag(event);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //TODO: do something
            }
        });
    }


    @Override
    public void startCamera() {
        Intent pictureIntent = new Intent(
                MediaStore.ACTION_IMAGE_CAPTURE
        );
        startActivityForResult(pictureIntent, REQUEST_CAPTURE_IMAGE);


    }

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    private void askSpeechInput(String string) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 2000);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                string);
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {

        }
    }





}
