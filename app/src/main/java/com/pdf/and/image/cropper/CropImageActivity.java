package com.pdf.and.image.cropper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.Toast;

import com.labters.documentscanner.DocumentScannerView;

public class CropImageActivity extends AppCompatActivity {
    DocumentScannerView scannerView;
    String path="";
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_image);
        Bundle extra=getIntent().getExtras();
        if(extra!=null)
        {
            path=extra.getString("path");
        }
      //  scannerView=findViewById(R.id.document_scanner);
       /* Bitmap bitmap = BitmapFactory.decodeFile(path);
      //  Bitmap bitmap=drawableToBitmap(this,R.drawable.img);
        scannerView.setImage(bitmap);
        cropImage();*/

        Toast.makeText(this, "path : "+path, Toast.LENGTH_LONG).show();
    }
    private void cropImage()
    {
        findViewById(R.id.imgCrop).setOnClickListener(view ->
        {
            Bitmap bitmap=scannerView.getCroppedImage();
            scannerView.setImage(scannerView.getCroppedImage());

        });

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
}