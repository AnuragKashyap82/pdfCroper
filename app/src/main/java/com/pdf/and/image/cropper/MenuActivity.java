package com.pdf.and.image.cropper;


import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class MenuActivity extends AppCompatActivity {
    PermissionUtil permissionUtil;
    private static final int PERMISSION_REQUEST_CODE = 111;
    int cnt=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       /* requestWindowFeature( Window.FEATURE_NO_TITLE );
        getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN );*/
        setContentView(R.layout.activity_menu);
        permissionUtil = new PermissionUtil(this);
        CardView c1, c2, c3, c4,c5,c6;
        c1 = findViewById(R.id.card1);
        c2 = findViewById(R.id.card2);
        c3 = findViewById(R.id.card3);
        c4 = findViewById(R.id.card4);
        c5 = findViewById(R.id.card5);
        c6 = findViewById(R.id.card6);

        c1.setOnClickListener(v ->
        {

            cnt=1;
            if (isAbove32()) {
                moveToActivity();
            } else {

                requestPermission();
            }
        });
        c2.setOnClickListener(v ->
        {
            cnt=2;
            if (isAbove32()) {

                moveToActivity();
            } else {

                requestPermission();
            }
            //startActivity(new Intent(this,PdfToImageActivity.class));
        });
        c3.setOnClickListener(v ->
        {
           cnt=3;
            if (isAbove32()) {

                moveToActivity();
            } else {

                requestPermission();
            }
        });

        c4.setOnClickListener(v ->
        {
            cnt=4;
            if (isAbove32()) {

                moveToActivity();
            } else {

                requestPermission();
            }
        });
        c5.setOnClickListener(v ->
        {
            startActivity(new Intent(this, DownloadActivity.class));
            finish();
        });
        c6.setOnClickListener(v ->
        {
            startActivity(new Intent(this, AboutActivity.class));
            finish();
          //  share();
        });


    }

    private void moveToActivity()
    {
        Intent intent;
        if(cnt==1)
        intent = new Intent(this, PdfActivity.class);
        else if(cnt==2)
            intent = new Intent(this, ImagePdfActivity.class);
        else if(cnt==3)
            intent = new Intent(this, EdgeCropperActivity.class);
        else
            intent = new Intent(this, CropToImageActivity.class);
        intent.putExtra("cnt",cnt);
        startActivity(intent);
        finish();
    }

    private void moveToActivity1() {
        Intent intent;
        intent = new Intent(this, CropToImageActivity.class);
        intent.putExtra("cnt",cnt);
        startActivity(intent);
    }







    boolean isAbove32() {
        return Build.VERSION.SDK_INT > Build.VERSION_CODES.S_V2;
    }


    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                moveToActivity();
            } else {
                Toast.makeText(this, "Permission Denied, You cannot use local drive .", Toast.LENGTH_SHORT).show();
            }
        }
    }


}