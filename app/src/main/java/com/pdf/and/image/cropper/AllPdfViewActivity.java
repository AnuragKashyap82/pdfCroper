package com.pdf.and.image.cropper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;
import java.io.FileNotFoundException;

public class AllPdfViewActivity extends AppCompatActivity {
    Uri pdfUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_pdf_view);
        pdfUri = getIntent().getData();
        PDFView pdfView = findViewById(R.id.pdfView);
        // Load a PDF file from a file path

        pdfView.fromUri(pdfUri)
                .defaultPage(0) // Set the default page to display (optional)
                .load();

        findViewById(R.id.btnShare).setOnClickListener(v ->
        {
            shareFile();
        });

        TextView tv=findViewById(R.id.tvTitle);
        tv.setText("Your Document");
        findViewById(R.id.cardBack).setOnClickListener(v -> home());
    }
    private void home()
    {
        startActivity(new Intent(this,MenuActivity.class));
        finish();
    }
    public void shareFile() {
        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("application/pdf");
            shareIntent.putExtra(Intent.EXTRA_STREAM, pdfUri);

            startActivity(Intent.createChooser(shareIntent, "Share PDF using"));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No Application Available to Share PDF", Toast.LENGTH_LONG).show();
        }

    }
}