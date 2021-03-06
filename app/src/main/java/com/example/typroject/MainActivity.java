package com.example.typroject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    private static final int MY_CAMERA_REQUEST_CODE = 100;
    Button bSelGallery;
    Button bSelCamera;
    TextView tvHeading;
    DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SkinLesionClassifier.init(getAssets(), "march_28_skin_opti.tflite");
        bSelGallery = findViewById(R.id.gallerychooser);
        bSelCamera = findViewById(R.id.Cameraclicker);
        tvHeading = findViewById(R.id.top_Heading);

        bSelCamera.setOnClickListener(this);
        bSelGallery.setOnClickListener(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ((NavigationView) findViewById(R.id.navigation_drawer)).setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, this.drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        this.drawer.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0: //select from camera
                if (resultCode == RESULT_OK) {
                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                    startActivity(new Intent(this, Report.class).putExtra("image-bitmap", bitmap));
                }
                break;
            case 1: //select from gallery
                if (resultCode == RESULT_OK) {
                    Uri imageUri = data.getData();
                    startActivity(new Intent(this, Report.class).putExtra("image-uri", imageUri));
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePicture, 0);//zero can be replaced with any action code (called requestCode)
            } else {
                Snackbar.make(findViewById(android.R.id.content), "Camera permission needed!", Snackbar.LENGTH_LONG).show();
            }
        }
    }


    @Override
    public void onClick(View v) {
        if (v instanceof ImageView) {
            Bitmap image = ((BitmapDrawable) ((ImageView) v).getDrawable()).getBitmap();
        }
        switch (v.getId()) {
            case R.id.Cameraclicker:
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE);
                    Log.d("permission status", "onClick: denied");
                } else {
                    Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(takePicture, 0);//zero can be replaced with any action code (called requestCode)
                }
                break;
            case R.id.gallerychooser:
                Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto, 1);
                break;
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {

            case R.id.nav_lesion:
                Intent lesion = new Intent(getApplicationContext(),Lesion_types.class);
                startActivity(lesion);
                break;

            case R.id.nav_tutorial :
                Intent tutorial = new Intent(getApplicationContext(),Tutorial.class);
                startActivity(tutorial);

                break;

            case R.id.nav_about:
                Intent about = new Intent(getApplicationContext(),About_me.class);
                startActivity(about);
                break;

            case R.id.nav_refer :
                Toast.makeText(getApplicationContext(),"Sharing",Toast.LENGTH_SHORT).show();
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_SUBJECT, "Sharing URL");
                i.putExtra(Intent.EXTRA_TEXT, "https://drive.google.com/file/d/1vGSOhytv20-F0Za8M6qBTrYBj7DQ-X6z/view?usp=sharing");
                startActivity(Intent.createChooser(i, "Share URL"));
                break;

        }
        this.drawer.closeDrawer((int) GravityCompat.START);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_exit){
            finishAndRemoveTask();
        }
        return super.onOptionsItemSelected(item);
    }
}