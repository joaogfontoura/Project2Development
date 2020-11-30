package com.example.project2development;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class AttractionRegistration extends AppCompatActivity {

    private ImageView img;
    private Button up;
    public Uri imageUri;
    private StorageReference mStorageRef;
    private TextView tvimage,tvloc,tvdesc,tvname;

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

    private void uploadPicture() {
    String imageID;
    String name = tvname.getText().toString();
    String description = tvdesc.getText().toString();
    String location = tvloc.getText().toString();

    imageID=System.currentTimeMillis()+"."+ getExtension(imageUri);
    StorageReference riversRef = mStorageRef.child(imageID);

    CollectionReference cref = db.collection("Attraction Collection");




   Map<String,Object> data = new HashMap<>();
    data.put("Attraction name",name);
    data.put("Description",description);
    data.put("Location",location);
    data.put("Image",imageID);

    cref.add(data);



        riversRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Toast.makeText(AttractionRegistration.this,"Image Uploaded", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(getApplicationContext(), AttractionRegistration.class));
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