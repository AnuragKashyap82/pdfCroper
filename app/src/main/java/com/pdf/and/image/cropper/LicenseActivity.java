package com.pdf.and.image.cropper;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

public class LicenseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_license);
        TextView tv=findViewById(R.id.tv);

        String t1 = "<b>Licensed under the Apache License, Version 2.0</b><br><br>" +
                "Originally forked from edmodo/cropper.<br>" +
                "Copyright 2016, Arthur Teplitzki, 2013, Edmodo, Inc.";

        String t2 = "<br><br><br><b>PaDaF PDF/A preflight (http://sourceforge.net/projects/padaf)</b><br>" +
                "Copyright 2010 Atos Worldline SAS";

        String t3 = "<br><br><b>Copyright 2017 Bartosz Schiller</b>";

        tv.setText(Html.fromHtml(t1 + t2 + t3));

    }
}