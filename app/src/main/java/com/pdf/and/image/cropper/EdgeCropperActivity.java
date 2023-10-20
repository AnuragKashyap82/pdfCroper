package com.pdf.and.image.cropper;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.labters.documentscanner.DocumentScannerView;
import com.pdf.and.image.cropper.helper.DatabaseHelper;
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
import java.util.ArrayList;
import java.util.Random;

public class EdgeCropperActivity extends AppCompatActivity {
    DocumentScannerView scannerView;
    int PICK_IMAGE = 101;
    private Context context;

    private Dialog myDialog1;
    ProgressDialog progressBar;
    private ArrayList<byte[]> allImagesByteList;
    private String path;
    private ImageView preview, preview2, preview3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15;
    int PICK_IMAGE_MULTIPLE = 1;

    ArrayList<Uri> mArrayUri;
    int position = 0;


    ArrayList<ImageView> allImgList;
    private boolean isImagePick = false;


    public static File dir_;


    private final int CAMERA = 2;

    private AppCompatButton btnMore, btnCrop, btnPrint, btnDone;

    private int cameraCnt = 0;
    int mIndex = -1;
    Uri cropUri;
    private boolean isCamera = false;
    private boolean isCrop = false;
    private int cnt = 1;
    int cout = 1;

    private boolean isMainPdf = true;
    ScrollView scrollView;

    private int selectedCount = 0;
    private int addMoreValue = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edge_cropper);
        scannerView = findViewById(R.id.document_scanner);
        scrollView = findViewById(R.id.scrollView);
        scannerView.setVisibility(View.GONE);
        context = getApplicationContext();
        dir_ = new File(this.getFilesDir(), "TestPdfApp");
        createDir(dir_);
        btnCrop = findViewById(R.id.btnCrop);
        btnMore = findViewById(R.id.btnMore);
        btnPrint = findViewById(R.id.btnPrint);
        btnDone = findViewById(R.id.btnDone);

        btnDone.setVisibility(View.GONE);
        btnMore.setVisibility(View.GONE);


        init();


        btnCrop.setOnClickListener(view ->
        {
            btnDone.setVisibility(View.VISIBLE);
            btnMore.setVisibility(View.GONE);
            btnCrop.setVisibility(View.GONE);
            btnPrint.setVisibility(View.GONE);
            isCrop = true;
            if (cout > 0) {

               /* if(cout==1 || addMoreValue==1)
                {
                    new ProcessImageTask().execute();

                }else
                    showImageSelectionDialog();*/
                showImageSelectionDialog();
                scrollView.setVisibility(View.GONE);
                scannerView.setVisibility(View.VISIBLE);
            } else Toast.makeText(this, "No", Toast.LENGTH_SHORT).show();
        });

        btnDone.setOnClickListener(view ->
        {
            Bitmap bitmap = scannerView.getCroppedImage();

            Log.e("TAG", "onCreate: " + cout);
            ImageView imageView = allImgList.get(selectedCount);
            imageView.setImageBitmap(bitmap);
            allImagesByteList.set(selectedCount,setImageByte(imageView));
            scrollView.setVisibility(View.VISIBLE);
            scannerView.setVisibility(View.GONE);
            btnDone.setVisibility(View.GONE);
            if (isCamera) {
                btnMore.setVisibility(View.VISIBLE);
            }
            btnCrop.setVisibility(View.VISIBLE);
            btnPrint.setVisibility(View.VISIBLE);

        });
        btnMore.setOnClickListener(view ->
        {
            if (cameraCnt < 10)
            {
                btnDone.setVisibility(View.GONE);
                addMoreValue++;
                takePhotoFromCamera();
            } else Toast.makeText(context, "Max 10 photo allowed", Toast.LENGTH_SHORT).show();
        });

        btnPrint.setOnClickListener(v -> {
            loadAllData();
        });
        LinearLayout layout = findViewById(R.id.layOptions);

        findViewById(R.id.card1).setOnClickListener(v ->
        {
            openImageChooser();
            layout.setVisibility(View.GONE);
        });
        findViewById(R.id.card2).setOnClickListener(v ->
        {
            isCamera = true;
            takePhotoFromCamera();
            layout.setVisibility(View.GONE);
            btnMore.setVisibility(View.VISIBLE);
        });
    }

    private void init() {
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


    }

    private class ProcessImageTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            ImageView selectedImage = allImgList.get(0);
            Bitmap bitmap = getBitmapFromImageView(selectedImage);
            tempFile(bitmap);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // This method will be called on the main thread
            // You can update the UI here if needed
        }
    }

    public boolean createDir(File sdIconStorageDir) {
        if (!sdIconStorageDir.exists()) {
            return sdIconStorageDir.mkdirs();
        } else {
            return true;
        }
    }

    private void tempFile1(Bitmap bitmap) {
        try {
            File tempFile = File.createTempFile("temp_image", ".png", getCacheDir());
            FileOutputStream fos = new FileOutputStream(tempFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
            Intent intent = new Intent(this, TestActivity.class);
            intent.putExtra("image_path", tempFile.getAbsolutePath());
            startActivity(intent);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void tempFile(Bitmap bitmap) {
        try {
            scannerView.setImage(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void takePhotoFromCamera() {
        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        path = dir_ + "/" + "MyImageToPdf" + getRandomNumber() + ".jpg";
        File output = new File(path);
        i.putExtra(MediaStore.EXTRA_OUTPUT,
                FileProvider.getUriForFile(this, getPackageName() + ".fileprovider",
                        new File(path)));
        startActivityForResult(i, CAMERA);
    }

    private int getRandomNumber() {
        final int min = 10;
        final int max = 800;
        return new Random().nextInt((max - min) + 1) + min;
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

    private void showImageSelectionDialog() {
        // Create a dialog to allow the user to select an image
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);

        int cnt;
        if (isCamera) {
            cnt = addMoreValue;
        } else {
            cnt = cout;
        }
        for (int i = 0; i < cnt; i++) {
            adapter.add("Image " + (i + 1));
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Image to Crop");
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Handle the selected image here
                selectedCount = which;
                dialog.dismiss();
                ImageView selectedImage = allImgList.get(selectedCount);
                Bitmap bitmap = getBitmapFromImageView(selectedImage);
                tempFile(bitmap);
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public Bitmap getBitmapFromImageView(ImageView imageView) {
        // Enable drawing cache for the ImageView
        imageView.setDrawingCacheEnabled(true);

        // Get the drawing cache as a Bitmap
        Bitmap bitmap = Bitmap.createBitmap(imageView.getDrawingCache());

        // Disable drawing cache to release resources
        imageView.setDrawingCacheEnabled(false);

        // Return the obtained bitmap
        return bitmap;
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
                scannerView.setImage(bitmap);
            } catch (Exception e) {
                Log.e("TAG", "onActivityResult: " + e.getMessage());
            }
        }
        if (requestCode == PICK_IMAGE_MULTIPLE && resultCode == RESULT_OK && null != data) {

            Log.e("TAG", "onActivityResult: pick_multiple");
            // Get the Image from data
            if (data.getClipData() != null) {
                cout = data.getClipData().getItemCount();
                if (cout <= 15) {
                    new imageAsyncTask(context, data, cout).execute();
                    isImagePick = true;
                } else Toast.makeText(context, "max 15 images allowed", Toast.LENGTH_SHORT).show();
            } else {
                isImagePick = true;
                Uri imageurl = data.getData();
                allImgList.get(0).setImageURI(imageurl);
                allImagesByteList.add(setImageByte(allImgList.get(0)));
                allImgList.get(0).setVisibility(View.VISIBLE);
            }
            position = 0;

        } else if (requestCode == CAMERA) {
            Log.e("TAG", "onActivityResult: camera");
            if (resultCode == RESULT_OK) {
                isImagePick = true;
                mIndex++;
                new cameraAsyncTask(context, data, 0).execute();
            }

        } else if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            Log.e("TAG", "onActivityResult: pick_image");
            Uri selectedImageUri = data.getData();
            try {
                cropUri = selectedImageUri;
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

        }
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
                if (isCamera)
                    pdfPrintCrop();
                else pdfPrint();
               // pdfPrint();
                //createHelloWorldPDF();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                //   progressBar.dismiss();
                insertPdfPath();
                moveToFile();
                // openPDF(path);
            }
        }
        SaveTask saveTask = new SaveTask();
        saveTask.execute();

    }


    private void openPDF(String path) {
        Context ctx = getApplicationContext();
        File file = new File(path);
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(FileProvider.getUriForFile(ctx, ctx.getPackageName() + ".fileprovider", file), "application/pdf");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            //  Toast.makeText(ctx, "No Application Available to View PDF", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(ctx, View.class);
            intent.putExtra("path", path);
            startActivity(intent);

        }

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

        context = getApplicationContext();
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

                contentStream.drawImage(pdImage, 5, 5, wt, ht);
                //    contentStream.drawImage(pdImage, 5, 5, imageWidth, imageHeight);
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

    private void pdfPrint() {
        PDFBoxResourceLoader.init(context);
        PDDocument document = new PDDocument();
        PDRectangle rec;
        try {
            for (int i = 0; i < allImagesByteList.size(); i++) {
                PDPage page;
                page = new PDPage(PDRectangle.A4);
                document.addPage(page);
                PDPageContentStream contentStream = new PDPageContentStream(document, page);

                byte[] imageData = allImagesByteList.get(i);

                // Create a PDImageXObject from the image data
                PDImageXObject pdImage = PDImageXObject.createFromByteArray(document, imageData, "image");

                // Get the image width and height from the PDImageXObject
                float imageWidth = pdImage.getWidth();
                float imageHeight = pdImage.getHeight();

                // Calculate a scaling factor to fit the image within the page while maintaining aspect ratio
                float pageWidth = page.getMediaBox().getWidth();
                float pageHeight = page.getMediaBox().getHeight();
                float scaleX = pageWidth / imageWidth;
                float scaleY = pageHeight / imageHeight;
                float scale = Math.min(scaleX, scaleY);

                // Calculate the position to center the scaled image on the page
                float scaledWidth = imageWidth * scale;
                float scaledHeight = imageHeight * scale;
                float x = (pageWidth - scaledWidth) / 2;
                float y = (pageHeight - scaledHeight) / 2;

                // Set a white background
                contentStream.setNonStrokingColor(255);
                contentStream.addRect(0, 0, pageWidth, pageHeight);
                contentStream.fill();

                // Draw the scaled image on the page
                contentStream.drawImage(pdImage, x, y, scaledWidth, scaledHeight);

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

    private byte[] setImageByte(ImageView imageView) {
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream op = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, op);
        return op.toByteArray();
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
            Uri imageurl = data.getClipData().getItemAt(i).getUri();
            mArrayUri.add(imageurl);
            try {
                InputStream iStream = context.getContentResolver().openInputStream(imageurl);
                allImagesByteList.add(getBytes(iStream));
                // allImgList.get(i).setImageURI(imageurl);
                //  allImgList.get(i).setVisibility(View.VISIBLE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public class imageAsyncTask extends AsyncTask<Void, Void, Void> {
        private Context context;
        private Intent intent;
        private Integer cout;

        public imageAsyncTask(Context context, Intent intent, Integer cout) {
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
            addImages(intent, cout);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            setImg();

        }
    }

    public class cameraAsyncTask extends AsyncTask<Void, Void, Void> {
        private Context context;
        private Intent intent;
        private Integer cout;

        private Uri myUri;
        byte[] inputData;

        public cameraAsyncTask(Context context, Intent intent, Integer cout) {
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
            // btnMore.setVisibility(View.VISIBLE);
//            btnCrop.setVisibility(View.VISIBLE);
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

            //btnVisibility();
            cameraCnt++;

        }
    }

    private void setImg() {

        for (int i = 0; i < mArrayUri.size(); i++) {

            allImgList.get(i).setImageURI(mArrayUri.get(i));
            allImgList.get(i).setVisibility(View.VISIBLE);

            /*if(isCrop)
            {
                if(i==0)
                {
                    Bitmap bitmap=scannerView.getCroppedImage();
                    // allImgList.remove(cout);
                    allImgList.get(cout).setImageBitmap(bitmap);
                }
            } else {
                allImgList.get(i).setImageURI(mArrayUri.get(i));
                allImgList.get(i).setVisibility(View.VISIBLE);
            }*/

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
            // setCropper(cropUri);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressBar.dismiss();
            allImagesByteList.remove(mIndex - 1);

        }
    }
}