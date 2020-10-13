package com.example.photocrop;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageOptions;
import com.theartofdev.edmodo.cropper.CropImageView;

public class MainActivity extends AppCompatActivity {
    private CropImageView mCropImageView;
    Uri imageUri;
    ConstraintLayout layout;
    ImageView imageView;
    Button upload;
    private static final int PICK_REQUEST = 53;
    private CropImageOptions mOptions;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        upload=findViewById(R.id.upload);
        imageView=findViewById(R.id.imageView);
        layout=findViewById(R.id.layout);

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_REQUEST);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.select_ratio, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.a9_16:
                //CropImage.activity().setAspectRatio(9, 16).start(MainActivity.this)
                ConstraintSet set = new ConstraintSet();
                set.clone(layout);
                set.setDimensionRatio(R.id.imageView, "16:9");
                set.applyTo(layout);

                break;

            case R.id.a16_9:
                CropImage.activity().setAspectRatio(16, 9).start(MainActivity.this);
                break;

            case R.id.a3_4:
                CropImage.activity().setAspectRatio(3, 4).start(MainActivity.this);
                break;
            case R.id.a4_3:
                CropImage.activity().setAspectRatio(4, 3).start(MainActivity.this);
                break;

        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_REQUEST && resultCode == RESULT_OK) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri=data.getData();
            try{
                Bitmap bitmap= MediaStore.Images.Media.getBitmap(getContentResolver(),imageUri);
                imageView.setImageBitmap(bitmap);
            }catch(Exception e){
                e.printStackTrace();
            }
        } else {
            Toast.makeText(MainActivity.this, "Something gone wrong!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(MainActivity.this, MainActivity.class));
            finish();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void uploadImage(View view)
    {

                if (CropImage.isExplicitCameraPermissionRequired(this)) {
                    // request permissions and handle the result in onRequestPermissionsResult()
                    requestPermissions(
                            new String[]{Manifest.permission.CAMERA},
                            CropImage.CAMERA_CAPTURE_PERMISSIONS_REQUEST_CODE);

                    requestPermissions(
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE);
                } else {
                    mCropImageView.setImageResource(R.id.imageView);
                    CropImage.startPickImageActivity(this);
                }


        final ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Posting");
        progressDialog.show();

    }


}





