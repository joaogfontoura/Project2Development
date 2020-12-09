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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Map;

public class AttractionRegistration extends AppCompatActivity {

    private ImageView img;
    private Button up;
    public Uri imageUri;
    private StorageReference mStorageRef;
    private TextView tvimage,tvloc,tvdesc,tvname;
    public static String generatedFilePath = "";

    // usado pra fazer log.d e debugar as variaveis
    private static final String TAG = "AttractionRegistration";

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attration_registration);

        tvimage = findViewById(R.id.textviewImg);
        tvname = findViewById(R.id.textviewName);
        tvloc= findViewById(R.id.textviewLoc);
        tvdesc = findViewById(R.id.textviewDesc);
        img = findViewById(R.id.attractionPic);
        up = findViewById(R.id.btnUpload);
        mStorageRef = FirebaseStorage.getInstance().getReference("Images");



        img.setOnClickListener(new View.OnClickListener(){

            public void onClick(View view){
                choosePicture();

            }

        });


        up.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadPicture();
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


                imageUri = data.getData();
                img.setImageURI(imageUri);
                tvimage.setVisibility(View.INVISIBLE);



    }
    private String getExtension(Uri uri){
        ContentResolver cr=getContentResolver();
        MimeTypeMap mimeTypeMap= MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(cr.getType(uri));


    }

    private void addAttraction(String filepath){

        // HERE WE ADD THE ATTRACTION TO FIRESTORE
        String name = tvname.getText().toString();
        String description = tvdesc.getText().toString();
        String location = tvloc.getText().toString();

        CollectionReference cref = db.collection("Attraction Collection");

        Map<String,Object> data = new HashMap<>();
        data.put("Name",name);
        data.put("Description",description);

        LatLng position = getLocationFromAddress(getApplicationContext(),location);
        double positionLat = position.latitude;
        double positionLng = position.longitude;
        GeoPoint geoPoint = new GeoPoint(positionLat, positionLng);

        data.put("Coordinates",geoPoint);
        //

        data.put("Image",filepath);

        cref.add(data);
        //
    }

    public LatLng getLocationFromAddress(Context context, String strAddress) {

        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;

        try {
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }

            Address location = address.get(0);
            p1 = new LatLng(location.getLatitude(), location.getLongitude() );

        } catch (IOException ex) {

            ex.printStackTrace();
        }

        return p1;
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