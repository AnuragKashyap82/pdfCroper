package com.pdf.and.image.cropper;


import androidx.appcompat.app.AppCompatActivity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        findViewById(R.id.cardRateUs).setOnClickListener(v -> appAddress());
        findViewById(R.id.cardShare).setOnClickListener(v -> share());

        findViewById(R.id.cardAbout).setOnClickListener(v -> startActivity(new Intent(this,LicenseActivity.class)));
        findViewById(R.id.cardBack).setOnClickListener(v -> home());
    }


    private void appAddress() {
        Uri uri = Uri.parse("market://details?id=" + getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
        }
    }


    private void share() {
        String appLink = "http://play.google.com/store/apps/details?id=" + getApplication().getPackageName();
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT,
                "Download this app : " + appLink);
        intent.setType("text/plain");
        startActivity(Intent.createChooser(intent, "choose to open"));
    }

    private void home()
    {
        startActivity(new Intent(this,MenuActivity.class));
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        home();
    }
}