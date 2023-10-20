package com.pdf.and.image.cropper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;

public class ViewActivity extends AppCompatActivity {
    String pdfFilePath="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        Bundle extras=getIntent().getExtras();

        if(extras!=null)
        {
            pdfFilePath=extras.getString("path");
        }
        PDFView pdfView = findViewById(R.id.pdfView);
        // Load a PDF file from a file path
        pdfView.fromFile(new File(pdfFilePath))
                .defaultPage(0) // Set the default page to display (optional)
                .load();

        findViewById(R.id.btnShare).setOnClickListener(v ->
        {
            shareFile();
        });

    }
    public void shareFile() {
        File outputPath = new File(pdfFilePath);
        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("application/pdf");
            Uri fileUri = FileProvider.getUriForFile(getApplicationContext(),
                    getPackageName() + ".fileprovider", outputPath);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
            startActivity(Intent.createChooser(shareIntent, "Share it"));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No Application Available to Share PDF", Toast.LENGTH_LONG).show();
        }
    }

}