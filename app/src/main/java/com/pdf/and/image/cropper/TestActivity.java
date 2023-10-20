package com.pdf.and.image.cropper;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.labters.documentscanner.DocumentScannerView;

import java.io.File;

public class TestActivity extends AppCompatActivity {
    DocumentScannerView scannerView;
    String path = "";
    Uri myUri;
    int PICK_IMAGE = 201;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            path = extra.getString("path");
        }
        scannerView = findViewById(R.id.document_scanner);
        imageView = findViewById(R.id.imgTest);

       /* Bitmap bitmap = BitmapFactory.decodeFile(path);
        imageView.setImageBitmap(bitmap);
        File file = new File(path);
        if (file.exists())
        {
             bitmap = BitmapFactory.decodeFile(path);
            scannerView.setImage(bitmap);
        } else {
            // Handle the case where the file does not exist.
           // Toast.makeText(this, "path : missing ", Toast.LENGTH_LONG).show();
            Log.e("TAG", "onCreate: missing ");
            Log.e("TAG", "onCreate: path "+path);
        }*/

       // pickImage();
       Bitmap bitmap=drawableToBitmap(this,R.drawable.img);
        scannerView.setImage(bitmap);
        //  Toast.makeText(this, "path : " + path, Toast.LENGTH_LONG).show();
    }

    private void pickImage() {
        // Create an Intent to pick an image from the gallery
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            Log.e("TAG", "onActivityResult: pick_image");
            Uri selectedImageUri = data.getData();
            // Pass the selected image URI to the cropping library
            try {
                Log.e("TAG", "onActivityResult: result ok" + selectedImageUri.toString());

                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);

              //  imageView.setImageBitmap(bitmap);
               // scannerView.setImage(loadBitmapFromView(imageView));

                //setCropper();
            } catch (Exception e) {
                Log.e("TAG", "onActivityResult: " + e.getMessage());
            }
        } else {
            // show this if no image is selected
            Toast.makeText(this, "You haven't picked Image", Toast.LENGTH_LONG).show();
        }
    }
    public static Bitmap drawableToBitmap(Context context, int drawableId) {
        // Load the drawable
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        if (drawable == null) {
            return null;
        }

        // Create a bitmap with the drawable's width and height
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);

        // Draw the drawable onto the bitmap
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }
    private void setCropper() {
        scannerView.setImage(loadBitmapFromView(imageView));
        Log.e("TAG", "onActivityResult: if cropper");
        cropImage();
    }


    public  Bitmap loadBitmapFromView(View v)
    {
        Bitmap bitmap;
        Drawable drawable = imageView.getDrawable();
        if (v.getMeasuredHeight() <= 0)
        {
            v.measure(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            Bitmap b = Bitmap.createBitmap(v.getMeasuredWidth(), v.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(b);
            v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
            v.draw(c);
            return b;
        } else if (drawable instanceof NinePatchDrawable)
        {
            bitmap = Bitmap
                    .createBitmap(
                            drawable.getIntrinsicWidth(),
                            drawable.getIntrinsicHeight(),
                            drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                                    : Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight());
            drawable.draw(canvas);
        }
        else {
            Bitmap returnedBitmap;
            returnedBitmap = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(returnedBitmap);
            Drawable bgDrawable = v.getBackground();
            if (bgDrawable != null)
                bgDrawable.draw(canvas);
            else
                canvas.drawColor(Color.WHITE);
            v.draw(canvas);
            bitmap= returnedBitmap;
        }

        return bitmap;

    }

    private void cropImage() {
        findViewById(R.id.imgCrop).setOnClickListener(view ->
        {
            Bitmap bitmap = scannerView.getCroppedImage();
            scannerView.setImage(scannerView.getCroppedImage());

        });

    }
}