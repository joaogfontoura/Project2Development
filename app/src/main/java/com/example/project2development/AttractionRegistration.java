package com.example.project2development;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.internal.LockOnGetVariable;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AttractionRegistration extends AppCompatActivity {

    private ImageView img;
    private Button up;
    public Uri imageUri;
    private StorageReference mStorageRef;
    private TextView tvimage,tvdesc,tvname, tvaddress, tvchoose;
    private ImageButton imgBtn;
    public static String generatedFilePath = "";

    LatLng position;

    // usado pra fazer log.d e debugar as variaveis
    private static final String TAG = "AttractionRegistration";

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attration_registration);

        tvimage = findViewById(R.id.textviewImg);
        tvname = findViewById(R.id.textviewName);
        tvdesc = findViewById(R.id.textviewDesc);
        tvchoose = findViewById(R.id.textView);
        tvaddress = findViewById(R.id.textViewAddress);
        img = findViewById(R.id.attractionPic);
        up = findViewById(R.id.btnUpload);
        mStorageRef = FirebaseStorage.getInstance().getReference("Images");

        imgBtn = findViewById(R.id.imageButton);

        imgBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MapPicker.class);
                startActivityForResult(intent, 2);
            }
        });

        img.setOnClickListener(new View.OnClickListener(){

            public void onClick(View view){

                choosePicture();

            }

        });


        up.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {


                if(SetError()){
                    uploadPicture();
                }

            }
        });


    }


    private void choosePicture() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        up.setEnabled(true);
        startActivityForResult(intent, 1);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

            if(requestCode == 1){
                imageUri = data.getData();
                img.setImageURI(imageUri);
                tvimage.setVisibility(View.INVISIBLE);
            }else if(requestCode == 2){

                Bundle b = data.getExtras();

                Double returnLatitude = b.getDouble("Lat");
                Double returnLongitude = b.getDouble("Long");

                position = new LatLng(returnLatitude,returnLongitude);

                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

                List<Address> addresses  = null;
                try {
                    addresses = geocoder.getFromLocation(position.latitude,position.longitude, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                tvchoose.setVisibility(View.INVISIBLE);
                tvaddress.setText(addresses.get(0).getAddressLine(0));
                tvaddress.setVisibility(View.VISIBLE);

            }






    }
    private String getExtension(Uri uri){
        ContentResolver cr=getContentResolver();
        MimeTypeMap mimeTypeMap= MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(cr.getType(uri));


    }

    // VERIFY THE INPUT OF DATA AND SET ERROR
    private boolean SetError(){
        // HERE WE CHECK IF THE VALUES ARE NOT NULL
        String name = tvname.getText().toString();
        String description = tvdesc.getText().toString();
        Boolean r = true;

        if(name.isEmpty() || name.length() == 0){
            tvname.setError("Please set a Name");
            r=false;
        }
        if(description.isEmpty() || description.length() == 0){
            tvdesc.setError("Please set a Description");
            r=false;
        }

        return r;
    }

    private void addAttraction(String filepath){

        // HERE WE ADD THE ATTRACTION TO FIRESTORE
        String name = tvname.getText().toString();
        String description = tvdesc.getText().toString();

        CollectionReference cref = db.collection("Attraction Collection");

        Map<String,Object> data = new HashMap<>();
        data.put("Name",name);
        data.put("Description",description);


        double positionLat = position.latitude;
        double positionLng = position.longitude;
        GeoPoint geoPoint = new GeoPoint(positionLat, positionLng);

        data.put("Coordinates",geoPoint);
        //

        data.put("Image",filepath);

        cref.add(data);

    }

    private void uploadPicture() {
    String imageID;


    imageID=System.currentTimeMillis()+"."+ getExtension(imageUri);
    StorageReference riversRef = mStorageRef.child(imageID);



        riversRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(
                                new OnCompleteListener<Uri>() {

                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {

                                        // SAVE THE FILE PATH FROM STORAGE
                                        String fileLink = task.getResult().toString();

                                        // CALL THE FUNCTION TO CREATE ATTRACTION
                                        addAttraction(fileLink);

                                    }
                                });

                        startActivity(new Intent(getApplicationContext(), AttractionMenu.class));
                        finish();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {

                        Toast.makeText(getApplicationContext(),"Failed to Upload",Toast.LENGTH_LONG).show();
                    }
                })

                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {

                    }
                });

    }
}