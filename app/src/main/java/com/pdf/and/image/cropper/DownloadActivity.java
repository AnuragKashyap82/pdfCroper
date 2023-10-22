package com.pdf.and.image.cropper;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.pdf.and.image.cropper.helper.DatabaseHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class DownloadActivity extends AppCompatActivity {
    private AllPdfRecyclerAdapter pdfAdapter;
    private List<String> pdfList;
    private RecyclerView recyclerView;
    private ProgressDialog progressBar;
    private ImageView btnShare;
    private ImageView imgBck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        recyclerView = findViewById(R.id.recyclerView);
        btnShare = findViewById(R.id.btnShare);
        imgBck = findViewById(R.id.imgBck);

        DatabaseHelper myDb=new DatabaseHelper(this);
        int cnt=myDb.getCount();
        if(cnt>0) {
            loadAllData();
        }

        btnShare.setVisibility(View.GONE);
        imgBck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DownloadActivity.this, MenuActivity.class);

                // Apply the fade-in animation to the new activity's layout
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION); // Prevent the default animation
                startActivity(intent);

                // OverridePendingTransition to apply the custom fade-in animation
                overridePendingTransition(R.anim.back, 0);
                finish();
            }
        });

        setInterstitialAds();
        TextView tv=findViewById(R.id.tvTitle);
        tv.setText("Your Documents");
        findViewById(R.id.cardBack).setOnClickListener(v -> home());
    }
    private void home()
    {
        Intent intent = new Intent(DownloadActivity.this, MenuActivity.class);
        startActivity(intent);
        finish();
    }
    private void move() {
        Intent intent = new Intent(DownloadActivity.this, MenuActivity.class);

        // Apply the fade-in animation to the new activity's layout
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION); // Prevent the default animation
        startActivity(intent);

        // OverridePendingTransition to apply the custom fade-in animation
        overridePendingTransition(R.anim.back, 0);
        finish();
    }

    public void displayPDF()
    {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        pdfAdapter = new AllPdfRecyclerAdapter(pdfList,this, "all");
        recyclerView.setAdapter(pdfAdapter);
    }
    private void loadAllData() {
        progressBar = new ProgressDialog(this);
        progressBar.setCancelable(false);
        class SaveTask extends AsyncTask<Void, Void, Void> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressBar.setTitle("please wait  ..");
                progressBar.show();
            }

            @Override
            protected Void doInBackground(Void... voids) {

                getAllPdfPath();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                progressBar.dismiss();
                displayPDF();
            }
        }
        SaveTask saveTask = new SaveTask();
        saveTask.execute();

    }

    private void  getAllPdfPath()
    {
        DatabaseHelper myDb=new DatabaseHelper(this);
        Cursor res=myDb.getAllPdf();
        pdfList = new ArrayList<>();
        while (res.moveToNext())
        {
            pdfList.add(res.getString(1));
            Log.e("TAG", "getAllPdfPath: "+res.getString(1));
        }
        Collections.reverse(pdfList);
    }

    private void setInterstitialAds() {

    }




    @Override
    public void onBackPressed() {
        super.onBackPressed();
        move();
    }
}