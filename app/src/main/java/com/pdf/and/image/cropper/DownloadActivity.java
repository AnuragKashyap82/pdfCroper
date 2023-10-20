package com.pdf.and.image.cropper;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        recyclerView = findViewById(R.id.recyclerView);

        DatabaseHelper myDb=new DatabaseHelper(this);
        int cnt=myDb.getCount();
        if(cnt>0) {
            loadAllData();
        }

        setInterstitialAds();
        TextView tv=findViewById(R.id.tvTitle);
        tv.setText("Your Documents");
        findViewById(R.id.cardBack).setOnClickListener(v -> home());
    }
    private void home()
    {
        startActivity(new Intent(this,MenuActivity.class));
        finish();
    }
    private void move() {
        startActivity(new Intent(this,MenuActivity.class));
        finish();
    }

    public void displayPDF()
    {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        pdfAdapter = new AllPdfRecyclerAdapter(pdfList,this);
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