package com.pdf.and.image.cropper;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.pdf.and.image.cropper.helper.DatabaseHelper;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageActivity;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.pdmodel.PDPage;
import com.tom_roush.pdfbox.pdmodel.PDPageContentStream;
import com.tom_roush.pdfbox.pdmodel.common.PDRectangle;
import com.tom_roush.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class OptionImageCropActivity extends AppCompatActivity {
    private Context context;
    ProgressDialog progressBar;
    private ArrayList<byte[]> allImagesByteList;
    private String path;
    private ImageView preview, preview2, preview3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15;
    int PICK_IMAGE_MULTIPLE = 1;

    ArrayList<Uri> mArrayUri;
    int position = 0;

    private AppCompatButton btnClear, btnPrint;

    private LinearLayout layOptions;
    ArrayList<ImageView> allImgList;
    private boolean isImagePick = false;


    public static File dir_;


    private static final int PICK_IMAGE = 99;

    private final int CAMERA = 2;

    private AppCompatButton btnMore, btnCrop;

    private int cameraCnt = 0;
    int mIndex = -1;
    Uri cropUri;
    private boolean isCamera = false;
    private boolean isCrop = false;
    private int cnt = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option_image_crop);
        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            cnt = extra.getInt("cnt");
        }
        context = this;
        dir_ = new File(this.getFilesDir(), "TestPdfApp");
        createDir(dir_);
        init();
        findViewById(R.id.cardBack).setOnClickListener(v -> move());


    }

    public boolean createDir(File sdIconStorageDir) {
        if (!sdIconStorageDir.exists()) {
            return sdIconStorageDir.mkdirs();
        } else {
            return true;
        }
    }

    private void init() {

        btnClear = findViewById(R.id.btnClear);
        btnPrint = findViewById(R.id.btnPrint);
        btnCrop = findViewById(R.id.btnCropImage);
        preview = findViewById(R.id.imgView);
        preview2 = findViewById(R.id.imgView2);
        preview3 = findViewById(R.id.imgView3);
        p4 = findViewById(R.id.imgView4);
        p5 = findViewById(R.id.imgView5);
        p6 = findViewById(R.id.imgView6);
        p7 = findViewById(R.id.imgView7);
        p8 = findViewById(R.id.imgView8);
        p9 = findViewById(R.id.imgView9);
        p10 = findViewById(R.id.imgView10);
        p11 = findViewById(R.id.imgView11);
        p12 = findViewById(R.id.imgView12);
        p13 = findViewById(R.id.imgView13);
        p14 = findViewById(R.id.imgView14);
        p15 = findViewById(R.id.imgView15);
        allImagesByteList = new ArrayList<>();
        mArrayUri = new ArrayList<>();
        allImgList = new ArrayList<>();
        allImgList.add(preview);
        allImgList.add(preview2);
        allImgList.add(preview3);
        allImgList.add(p4);
        allImgList.add(p5);
        allImgList.add(p6);
        allImgList.add(p7);
        allImgList.add(p8);
        allImgList.add(p9);
        allImgList.add(p10);
        allImgList.add(p11);
        allImgList.add(p12);
        allImgList.add(p13);
        allImgList.add(p14);
        allImgList.add(p15);

        for (int i = 0; i < allImgList.size(); i++) {
            allImgList.get(i).setVisibility(View.GONE);
        }

        btnPrint.setVisibility(View.INVISIBLE);
        btnClear.setVisibility(View.INVISIBLE);
        layOptions = findViewById(R.id.layOptions);
        btnClear.setOnClickListener(view ->
        {
            startActivity(new Intent(context, MenuActivity.class));
            finish();
        });

        CardView c1, c2;
        c1 = findViewById(R.id.card1);
        c2 = findViewById(R.id.card2);

        c1.setOnClickListener(v ->
        {
            openImageChooser();
        });
        c2.setOnClickListener(v ->
        {
            takePhotoFromCamera();
        });

        btnPrint.setOnClickListener(view -> {
            if (isImagePick) {
                //  showCustomPop();
                loadAllData();
                btnClear.setVisibility(View.VISIBLE);
            } else {
                Toast.makeText(context, "please select image", Toast.LENGTH_SHORT).show();
                btnPrint.setVisibility(View.INVISIBLE);
                layOptions.setVisibility(View.VISIBLE);
            }
        });
        btnMore = findViewById(R.id.btnMore);

        btnMore.setOnClickListener(v ->
        {
            if (cameraCnt < 10) {
                takePhotoFromCamera();
            } else Toast.makeText(context, "Max 10 photo allowed", Toast.LENGTH_SHORT).show();
        });
        TextView tv = findViewById(R.id.tvTitle);
        tv.setText("Create Pdf");
        btnCrop.setOnClickListener(v ->
        {
            if (cropUri != null) {
                isCrop = true;
                tv.setText("Crop Image");
                new MyCustomAsyncTask2(context, null, 1).execute();
            } else Toast.makeText(context, "No Image Present", Toast.LENGTH_SHORT).show();

        });

        btnMore.setVisibility(View.GONE);
        btnCrop.setVisibility(View.GONE);
        if (cnt > 1)
        {
            pickImage();
            layOptions.setVisibility(View.GONE);
        }


        findViewById(R.id.cardBack).setOnClickListener(v -> home());
    }

    private void home() {
        startActivity(new Intent(this, MenuActivity.class));
        finish();
    }


    private void move() {
        startActivity(new Intent(this, MenuActivity.class));
        finish();
    }



    private void loadAllData() {
        //  progressBar = new ProgressDialog(context);
        //  progressBar.setCancelable(false);
        class SaveTask extends AsyncTask<Void, Void, Void> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                //  progressBar.setTitle("please wait app is generating card ..");
                //  progressBar.show();
            }

            @Override
            protected Void doInBackground(Void... voids) {
                if (isCrop) {
                    pdfPrintCrop();
                    Log.e("TAG", "doInBackground: if ");
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                //   progressBar.dismiss();
                btnClear.setVisibility(View.VISIBLE);
                btnPrint.setVisibility(View.INVISIBLE);
                Toast.makeText(context, "Pdf is Successfully created ...", Toast.LENGTH_SHORT).show();
                insertPdfPath();
                moveToFile();
            }
        }
        SaveTask saveTask = new SaveTask();
        saveTask.execute();

    }

    private void moveToFile() {

        startActivity(new Intent(this, DownloadActivity.class));
        finish();
    }


    private void insertPdfPath() {
        DatabaseHelper myDb = new DatabaseHelper(context);
        myDb.insertPdfPath(path);
        myDb.close();
    }





    private void pdfPrintCrop() {
        PDFBoxResourceLoader.init(context);
        PDDocument document = new PDDocument();
        PDRectangle rec;
        try {
            for (int i = 0; i < allImagesByteList.size(); i++) {
                PDPage page;
                int ht = allImgList.get(i).getHeight();
                int wt = allImgList.get(i).getWidth();
                PDRectangle pageSize = new PDRectangle(allImgList.get(i).getWidth(), allImgList.get(i).getHeight());
                //   PDRectangle pageSize = new PDRectangle(300, 400);

                page = new PDPage(pageSize);

                document.addPage(page);
                PDPageContentStream contentStream = new PDPageContentStream(document, page);

                byte[] imageData = allImagesByteList.get(i);

                // Create a PDImageXObject from the image data
                PDImageXObject pdImage = PDImageXObject.createFromByteArray(document, imageData, "image");

                // Get the image width and height from the PDImageXObject
                float imageWidth = pdImage.getWidth();
                float imageHeight = pdImage.getHeight();

                // Calculate a scaling factor to fit the image within the page while maintaining aspect ratio

                // Draw the scaled image on the page
                contentStream.drawImage(pdImage, 0, 0, wt, ht);
                contentStream.close();

            }

            String mPath = dir_ + "" + getRandomNumber() + ".pdf";
            path = mPath;
            document.save(path);
            document.close();
        } catch (IOException e) {
            Log.e("TAG", "PdfBoxPrint: " + e);
        }
    }


    private int getRandomNumber() {
        final int min = 10;
        final int max = 800;
        return new Random().nextInt((max - min) + 1) + min;
    }


    private byte[] setImageByte(ImageView imageView) {
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream op = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, op);
        return op.toByteArray();
    }


    void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_MULTIPLE);

    }


    private void pickImage() {
        // Create an Intent to pick an image from the gallery
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE);
    }


    private void takePhotoFromCamera()
    {
        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        path = dir_ + "/" + "MyImageToPdf" + getRandomNumber() + ".jpg";
        File output = new File(path);
        i.putExtra(MediaStore.EXTRA_OUTPUT,
                FileProvider.getUriForFile(this, getPackageName() + ".fileprovider",
                        new File(path)));
        startActivityForResult(i, CAMERA);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // When an Image is picked
        if (requestCode == PICK_IMAGE_MULTIPLE && resultCode == RESULT_OK && null != data)
        {

            Log.e("TAG", "onActivityResult: pick_multiple");
            // Get the Image from data
            if (data.getClipData() != null) {
                int cout = data.getClipData().getItemCount();
                if (cout <= 15) {
                    new MyCustomAsyncTask(context, data, cout).execute();
                    isImagePick = true;
                    btnVisibility();
                } else Toast.makeText(context, "max 15 images allowed", Toast.LENGTH_SHORT).show();
            } else {
                isImagePick = true;
                Uri imageurl = data.getData();
                allImgList.get(0).setImageURI(imageurl);
                allImagesByteList.add(setImageByte(allImgList.get(0)));
                allImgList.get(0).setVisibility(View.VISIBLE);
                btnVisibility();
            }
            position = 0;

        } else if (requestCode == CAMERA)
        {

            Log.e("TAG", "onActivityResult: camera");
            if (resultCode == RESULT_OK) {
                isImagePick = true;
                mIndex++;
                new MyCustomAsyncTask1(context, data, 0).execute();
            }

        } else if (requestCode == PICK_IMAGE && resultCode == RESULT_OK)
        {
            Log.e("TAG", "onActivityResult: pick_image");
            Uri selectedImageUri = data.getData();
            // Pass the selected image URI to the cropping library
            try {
                cropUri = selectedImageUri;
                setCropper(selectedImageUri);
                int newHeight = 1400;
                int wt = 1200;
                ViewGroup.LayoutParams layoutParams = allImgList.get(0).getLayoutParams();
                layoutParams.height = newHeight;
                layoutParams.width = wt;
                allImgList.get(0).setVisibility(View.VISIBLE);
                allImgList.get(0).setLayoutParams(layoutParams);
                Log.e("TAG", "onActivityResult: result ok" + selectedImageUri.toString());
            } catch (Exception e) {
                Log.e("TAG", "onActivityResult: " + e.getMessage());
            }

        } else {
            // show this if no image is selected
            Toast.makeText(context, "You haven't picked Image", Toast.LENGTH_LONG).show();
        }
    }

    private void setCropper(Uri uri) {
        isCrop = true;
        String path = uri.toString();
        Intent intent=new Intent(this,TestActivity.class);
        intent.putExtra("path",path);
        intent.putExtra("myUri",uri);
        startActivity(intent);
       // Toast.makeText(context, "okay", Toast.LENGTH_LONG).show();

    }

    public class MyCustomAsyncTask1 extends AsyncTask<Void, Void, Void> {
        private Context context;
        private Intent intent;
        private Integer cout;

        private Uri myUri;
        byte[] inputData;

        public MyCustomAsyncTask1(Context context, Intent intent, Integer cout) {
            this.context = context;
            this.intent = intent;
            this.cout = cout;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                myUri = Uri.fromFile(new File(path));
                cropUri = myUri;
                InputStream iStream = getContentResolver().openInputStream(myUri);
                inputData = getUriBytes(iStream);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            //  progressBar.cancel();
            btnMore.setVisibility(View.VISIBLE);
            btnCrop.setVisibility(View.VISIBLE);
            allImgList.get(cameraCnt).setImageURI(myUri);
            allImgList.get(cameraCnt).setVisibility(View.VISIBLE);
            allImagesByteList.add(inputData);
            int newHeight = 1400;
            int wt = 1200;
            // Set your desired height here
            for (ImageView imageView : allImgList) {
                ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
                layoutParams.height = newHeight;
                layoutParams.width = wt;
                imageView.setLayoutParams(layoutParams);
            }

            btnVisibility();
            cameraCnt++;

        }
    }

    public byte[] getUriBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }


    private void btnVisibility() {
        btnPrint.setVisibility(View.VISIBLE);
        btnClear.setVisibility(View.VISIBLE);

        layOptions.setVisibility(View.GONE);
    }

    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }


    private void addImages(Intent data, int cout) {
        for (int i = 0; i < cout; i++) {
            // adding imageuri in array
            Uri imageurl = data.getClipData().getItemAt(i).getUri();
            mArrayUri.add(imageurl);
            try {
                InputStream iStream = context.getContentResolver().openInputStream(imageurl);
                allImagesByteList.add(getBytes(iStream));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public class MyCustomAsyncTask extends AsyncTask<Void, Void, Void> {
        private Context context;
        private Intent intent;
        private Integer cout;

        public MyCustomAsyncTask(Context context, Intent intent, Integer cout) {
            this.context = context;
            this.intent = intent;
            this.cout = cout;

        }

        @Override
        protected void onPreExecute() {
            //  progressBar = new ProgressDialog(context);
            //  progressBar.setCancelable(false);
            //  progressBar.show();
            // write show progress Dialog code here
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            addImages(intent, cout);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            for (int i = 0; i < mArrayUri.size(); i++) {
                allImgList.get(i).setImageURI(mArrayUri.get(i));
                allImgList.get(i).setVisibility(View.VISIBLE);
            }
            int newHeight = 1400;
            int wt = 1200;
            // Set your desired height here
            for (ImageView imageView : allImgList) {
                ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
                layoutParams.height = newHeight;
                layoutParams.width = wt;
                imageView.setLayoutParams(layoutParams);
            }
            //   progressBar.cancel();
            btnPrint.setVisibility(View.VISIBLE);
            btnClear.setVisibility(View.VISIBLE);

            layOptions.setVisibility(View.GONE);
            //   btnCrop.setVisibility(View.VISIBLE);

        }
    }


    public class MyCustomAsyncTask2 extends AsyncTask<Void, Void, Void> {
        private Context context;
        private Intent intent;
        private Integer cout;

        public MyCustomAsyncTask2(Context context, Intent intent, Integer cout) {
            this.context = context;
            this.intent = intent;
            this.cout = cout;

        }

        @Override
        protected void onPreExecute() {
            progressBar = new ProgressDialog(context);
            progressBar.setCancelable(false);
            progressBar.setTitle("please wait app is importing images ..");
            progressBar.show();
            // write show progress Dialog code here
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            setCropper(cropUri);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressBar.dismiss();
           // allImagesByteList.remove(mIndex-1);

        }
    }


    @Override
    public void onBackPressed() {
        move();
    }


}