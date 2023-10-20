package com.pdf.and.image.cropper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;

public class ViewImageActivity extends AppCompatActivity {

    String path="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);
        Bundle extra=getIntent().getExtras();
        if(extra!=null)
        {
            path=extra.getString("path");
        }
        ImageView img = findViewById(R.id.img);
        Bitmap bmImg = BitmapFactory.decodeFile(path);
        img.setImageBitmap(bmImg);
        findViewById(R.id.btnShare).setOnClickListener(v -> shareImage(path));

    }

    private void shareImage(String filePath)
    {
        File outputPath = new File(filePath);
        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("image/*");
            Uri fileUri = FileProvider.getUriForFile(getApplicationContext(),
                    getPackageName() + ".fileprovider", outputPath);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
            startActivity(Intent.createChooser(shareIntent, "Share it"));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "Error While Opening Image", Toast.LENGTH_LONG).show();
        }
    }

}