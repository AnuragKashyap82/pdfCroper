package com.pdf.and.image.cropper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.pdf.and.image.cropper.helper.DatabaseHelper;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class ImagePdfActivity extends AppCompatActivity {

    private static final int PICK_IMAGE = 555;
    private Context context;
    ProgressDialog progressBar;
    private ArrayList<Uri> mArrayUri;
    private ArrayList<ImageView> imageViewArrayList;

    private ArrayList<String> arrayListPath;
    private ArrayList<Bitmap> arrayListBitmap;

    private ArrayList<byte[]> allImagesByteList;
    private RecyclerView recyclerView;
    private ImageAdapter1 adapter;
    private List<ImageItem> imageItems;

    PdfRenderer renderer;
    int total_pages = 0;
    int display_page = 0;
    public static final int PICK_FILE = 99;
    String screenshotPath, path="";
    public static File dir_;
    boolean isOk=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_pdf);
        context = getApplicationContext();
        progressBar = new ProgressDialog(this);
        mArrayUri = new ArrayList<>();
        imageViewArrayList = new ArrayList<>();
        arrayListPath = new ArrayList<>();
        arrayListBitmap = new ArrayList<>();
        openImageChooser();
        dir_ = new File(this.getFilesDir(), "TestPdfApp");
        createDir(dir_);
        allImagesByteList = new ArrayList<>();
        findViewById(R.id.btnPrint).setOnClickListener(v ->
        {
            if(arrayListBitmap.size()>0)
            loadAllData();
            else {
                Toast.makeText(context, "Select Pdf First", Toast.LENGTH_SHORT).show();
                openImageChooser();
            }
        });
        findViewById(R.id.cardBack).setOnClickListener(v -> home());
        dir_= new File(getApplicationContext().getFilesDir(), "TestPdfApp");
        File mFolder = new File(String.valueOf(dir_));
        if (!mFolder.exists()) {
            mFolder.mkdir();
        }
        TextView tv=findViewById(R.id.tvTitle);
        tv.setText("Pdf To Image");
    }
    private void home()
    {
        startActivity(new Intent(this,MenuActivity.class));
        finish();
    }
    public boolean createDir(File sdIconStorageDir) {
        if (!sdIconStorageDir.exists()) {
            return sdIconStorageDir.mkdirs();
        } else {
            return true;
        }
    }

    private void setRecycler() {
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        imageItems = new ArrayList<>();
        ArrayList<ImageItem1> a = new ArrayList<>();
        /*for (int i = 0; i < mArrayUri.size(); i++) {
            imageItems.add(new ImageItem(mArrayUri.get(i)));
        }*/
        for (int i = 0; i < arrayListBitmap.size(); i++) {
            a.add(new ImageItem1(arrayListBitmap.get(i)));
        }

        for (int i = 0; i < imageViewArrayList.size(); i++) {
            Log.e("TAG", "setRecycler: "+imageViewArrayList.get(i));
        }

        adapter = new ImageAdapter1(this, a);
        recyclerView.setAdapter(adapter);

    }


    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/pdf");
        startActivityForResult(intent, PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // When an Image is picked
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && null != data) {

            Log.e("TAG", "addImages: if ");
            Uri uri = data.getData();
            try {
                if (uri != null) {
                    ParcelFileDescriptor parcelFileDescriptor = getContentResolver()
                            .openFileDescriptor(uri, "r");
                    renderer = new PdfRenderer(parcelFileDescriptor);
                    // displayPDF(new File(uri.toString()));
                    total_pages = renderer.getPageCount();
                    display_page = 0;
                    _display(display_page);
                } else Log.e("TAG", "addImages: null");
            } catch (Exception e) {
                Log.e("TAG", "addImages: " + e.getMessage());
            }

        }

    }

    private void _display(int _n) {

        try {
            PdfRenderer.Page page;
            Log.e("TAG", "addImages: if ");
            if (renderer != null) {
                for (int i = 0; i < total_pages; i++)
                {
                    page = renderer.openPage(i);
                    Bitmap bitmap = Bitmap.createBitmap(page.getWidth(), page.getHeight(), Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(bitmap);
                    canvas.drawColor(Color.WHITE);
                    page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_PRINT);
                    arrayListBitmap.add(bitmap);
                    page.close();
                }
                setRecycler();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("TAG", "addImages: catch " + e.getMessage());
        }

    }


    private void saveBitmapToFile(Bitmap bitmap, String filename) {
        try {
            File imageFile = new File(getFilesDir(), filename);
            FileOutputStream outputStream = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
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

            // write show progress Dialog code here
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            Log.e("TAG", "addImages: in background ");
            addImages(intent, cout);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.e("TAG", "addImages: in post ");
            setRecycler();

        }
    }

    private void addImages(Intent data, int cout) {
        ParcelFileDescriptor parcelFileDescriptor;
        for (int i = 0; i < cout; i++) {
            try {
                Uri imageurl = data.getClipData().getItemAt(i).getUri();
                mArrayUri.add(imageurl);
                parcelFileDescriptor = getContentResolver()
                        .openFileDescriptor(imageurl, "r");
                renderer = new PdfRenderer(parcelFileDescriptor);
                total_pages = renderer.getPageCount();
                display_page = 0;
                imageViewArrayList.get(i).setImageURI(imageurl);
                _display(i);

            } catch (Exception e) {
                e.printStackTrace();
                Log.e("TAG", "addImages: " + e.getMessage());
            }
        }
    }
    public byte[] getUriBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (renderer != null) {
            renderer.close();
        }
    }

    public  String takeScreenshotAndSave(View view) {
        // Create a bitmap of the view
        view.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);
        // Create a directory for saving the screenshot
        // Generate a unique filename for the screenshot
      String  path = "screenshot_" + new Date().getTime() + ".png";
        File file = new File(dir_, path);

        try {
            // Save the screenshot to the file
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
            // Add the screenshot to the device's media gallery
            MediaStore.Images.Media.insertImage(view.getContext().getContentResolver(), file.getAbsolutePath(), path, null);
            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
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
                saveImage();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                //   progressBar.dismiss();
                     if(!path.isEmpty()) {
                        moveToFile();
                         Toast.makeText(context, "Image is Successfully saved ...", Toast.LENGTH_SHORT).show();
                     }else   Toast.makeText(context, "Error ...", Toast.LENGTH_SHORT).show();


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


    private void saveImage() {
        saveImageToPictures();
        Log.e("TAG", "saveImage: save image");
        dir_ = new File(this.getFilesDir(), "TestPdfApp");
        createDir(dir_);
        OutputStream fOut;

        // the File to save , append increasing numeric counter to prevent files from getting overwritten.
        try {
            for(int i=0;i<arrayListBitmap.size();i++)
            {
                String mPath = dir_ + "" + getRandomNumber() + ".png";
                path = mPath;
                File file = new File(path);
                Log.e("TAG", "saveImage: try");
                fOut = new FileOutputStream(file);
                Bitmap pictureBitmap = arrayListBitmap.get(i);
                pictureBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut); // saving the Bitmap to a file compressed as a JPEG with 85% compression rate
                fOut.flush(); // Not really required
                fOut.close(); // do not forget to close the stream
                MediaStore.Images.Media.insertImage(getContentResolver(), file.getAbsolutePath(), file.getName(), file.getName());
                insertPdfPath();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("TAG", "saveImage: "+e.getMessage());
        }

    }
    public void saveImageToPictures() {
        for(int i=0;i<arrayListBitmap.size();i++)
        {
            Bitmap bitmap = arrayListBitmap.get(i);
            String fileName = "image_" + new Date().getTime() + ".jpg";

            // Get the Pictures directory
            File picturesDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

            // Create a new file in the Pictures directory
            File imageFile = new File(picturesDirectory, fileName);

            try {
                // Create an output stream to write the bitmap data to the file
                OutputStream outputStream = new FileOutputStream(imageFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                outputStream.close();

                // Add the image to the MediaStore so that it appears in the device's gallery
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.TITLE, fileName);
                values.put(MediaStore.Images.Media.DESCRIPTION, "Image saved from the MyPdf");
                values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
                values.put(MediaStore.Images.ImageColumns.BUCKET_ID, imageFile.toString().toLowerCase().hashCode());
                values.put(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME, imageFile.getName().toLowerCase());
                values.put("_data", imageFile.getAbsolutePath());

                getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }







    private int getRandomNumber() {
        final int min = 10;
        final int max = 800;
        return new Random().nextInt((max - min) + 1) + min;
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        home();
    }
}