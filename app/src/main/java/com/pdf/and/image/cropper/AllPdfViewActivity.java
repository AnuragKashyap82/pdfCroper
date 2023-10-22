package com.pdf.and.image.cropper;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.tom_roush.pdfbox.pdmodel.PDDocument;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

import com.github.barteksc.pdfviewer.PDFView;
import com.tom_roush.pdfbox.text.PDFTextStripper;

public class AllPdfViewActivity extends AppCompatActivity {
    Uri pdfUri;
    String pdfPassword; // Password entered by the user

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_pdf_view);
        pdfUri = getIntent().getData();

        // Check if the PDF is password-protected
        if (isPasswordProtected(pdfUri)) {
            // Show a dialog to input the password
            loadPdfWithPassword();
        } else {
            // Load the PDF directly if it's not password-protected
            loadPdfWithPassword();
        }

    }


    private boolean isPasswordProtected(Uri pdfUri) {
        try {
            PDDocument document = PDDocument.load(new File(pdfUri.getPath()));
            PDFTextStripper textStripper = new PDFTextStripper();
            textStripper.setStartPage(1);
            textStripper.setEndPage(1);
            String text = textStripper.getText(document);

            // Modify this pattern to match the text you expect in password-protected PDFs
            Pattern passwordPattern = Pattern.compile("Enter password:");
            boolean isPasswordProtected = passwordPattern.matcher(text).find();

            document.close();
            return isPasswordProtected;
        } catch (IOException e) {
            // An exception occurred while checking, treat it as password-protected
            return true;
        }
    }

    private void loadPdfWithPassword() {
        PDFView pdfView = findViewById(R.id.pdfView);
        pdfView.fromUri(pdfUri)
                .defaultPage(0)
                .load();

        findViewById(R.id.btnShare).setOnClickListener(v -> shareFile());

        TextView tv = findViewById(R.id.tvTitle);
        tv.setText("Your Document");

        findViewById(R.id.cardBack).setOnClickListener(v -> home());
    }

    private void home() {
        startActivity(new Intent(this, MenuActivity.class));
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
