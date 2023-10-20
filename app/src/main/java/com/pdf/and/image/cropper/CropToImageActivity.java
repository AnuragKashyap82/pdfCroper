package com.pdf.and.image.cropper;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.pdf.and.image.cropper.helper.DatabaseHelper;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.Random;

public class CropToImageActivity extends AppCompatActivity {
    TextView textview1;
    ImageView imageview1;

    PdfRenderer renderer;
    int total_pages = 0;
    int display_page = 0,currentPage=0;
    public static final int PICK_FILE = 99;

    String screenshotPath, path;
    public static File dir_;
    private PDFView pdfView;
    Uri imgUri;
    TextView tvPage;
    LinearLayout layCounter;
    AppCompatButton btnImageSave;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_to_image);
        Button button1 = findViewById(R.id.button1);
        imageview1 = findViewById(R.id.imageView);
        layCounter=findViewById(R.id.layCounter);
        button1.setOnClickListener(v -> {
            if(total_pages>0)
            {
                setImage();
                setCropper(screenshotPath);
            }else
            {
                pickFile();
                Toast.makeText(this, "Select Pdf First", Toast.LENGTH_SHORT).show();
            }
        });

        btnImageSave=findViewById(R.id.btnImageSave);
        btnImageSave.setVisibility(View.GONE);
        btnImageSave.setOnClickListener(v -> {
            if(total_pages>0) {
                loadAllData();
            }else {
                pickFile();
                Toast.makeText(this, "Select Pdf First", Toast.LENGTH_SHORT).show();
            }
        });
        pdfView = findViewById(R.id.pdfView);
        tvPage=findViewById(R.id.pageNumberText);
        pickFile();
        TextView tv=findViewById(R.id.tvTitle);
        tv.setText("Crop Pdf To Image");
        findViewById(R.id.cardBack).setOnClickListener(v -> home());

        AppCompatButton forwardButton = findViewById(R.id.forwardButton);
        AppCompatButton  backButton = findViewById(R.id.backButton);



        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int  totalPages = pdfView.getPageCount();
                if (currentPage > 1) {
                    currentPage--;
                    pdfView.jumpTo(currentPage - 1); // Jump to the previous page.
                    tvPage.setText(currentPage+" / "+totalPages);
                }
            }
        });



        forwardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                  int  totalPages = pdfView.getPageCount();
                if (currentPage < totalPages) {
                    currentPage++;
                    pdfView.jumpTo(currentPage - 1); // Jump to the next page.
                    tvPage.setText(currentPage+" / "+totalPages);
                }
            }
        });

        imageview1.setVisibility(View.GONE);
        layCounter.setVisibility(View.GONE);


    }
    private void setPdf(Uri uri)
    {
        layCounter.setVisibility(View.VISIBLE);
        pdfView.fromUri(uri)
                .defaultPage(0) // PDF pages are 0-based index.
                .swipeHorizontal(true) // Display pages horizontally.
                .onPageChange(new OnPageChangeListener() {
                    @Override
                    public void onPageChanged(int page, int pageCount) {
                        currentPage = page + 1;
                        tvPage.setText(currentPage+" / "+pageCount);
                    }
                })
                .load();
    }
    private void home()
    {
        startActivity(new Intent(this,MenuActivity.class));
        finish();
    }
    private void pickFile()
    {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/pdf");
        startActivityForResult(intent, PICK_FILE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILE && resultCode == RESULT_OK)
        {
            if (data != null){
                Uri uri = data.getData();
                try {
                    imgUri=uri;
                    ParcelFileDescriptor parcelFileDescriptor = getContentResolver()
                            .openFileDescriptor(uri, "r");
                    renderer = new PdfRenderer(parcelFileDescriptor);
                    total_pages = renderer.getPageCount();
                    display_page = 0;
                    setPdf(uri);
                    /*_display(display_page);
                     screenshotPath = takeScreenshotAndSave(imageview1);
                    if (screenshotPath != null) {
                        Bitmap myBitmap = BitmapFactory.decodeFile(screenshotPath);
                        imageview1.setImageBitmap(myBitmap);
                    } else {
                        // Handle the case where saving the screenshot failed
                        Toast.makeText(CropToImageActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }*/


                } catch (IOException ignored){
                }
            }
        }else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            Log.e("TAG", "onActivityResult: crop_image_activity");
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                Uri imageurl = result.getUri();
                if (imageurl!=null) {
                    imageview1.setImageURI(imageurl);
                }

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                error.printStackTrace();
            }

        }

    }

    private void setImage()
    {
                 pdfView.setVisibility(View.GONE);
                 layCounter.setVisibility(View.GONE);
                 imageview1.setVisibility(View.VISIBLE);
                  btnImageSave.setVisibility(View.VISIBLE);
                 Toast.makeText(this, "current page"+currentPage, Toast.LENGTH_SHORT).show();
                 _display(currentPage-1);
                     screenshotPath = takeScreenshotAndSave(imageview1);
                    if (screenshotPath != null) {
                        Bitmap myBitmap = BitmapFactory.decodeFile(screenshotPath);
                        imageview1.setImageBitmap(myBitmap);
                    }
    }
    private void setCropper(String imagePath) {
        CropImage.activity(Uri.fromFile(new File(imagePath)))
                .setGuidelines(CropImageView.Guidelines.ON)
                .setBorderLineColor(R.color.black)
                .setBorderCornerColor(R.color.black)
                .setBorderLineThickness(1)
                .setBorderLineThickness(1)
                .start(this);

    }


    private void _display(int _n) {
        if (renderer != null) {
            PdfRenderer.Page page = renderer.openPage(_n);
             Bitmap mBitmap = Bitmap.createBitmap(page.getWidth(), page.getHeight(), Bitmap.Config.ARGB_8888);
            page.render(mBitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
            imageview1.setImageBitmap(mBitmap);
            page.close();
           /* page = renderer.openPage(_n);
            Bitmap bitmap = Bitmap.createBitmap(page.getWidth(), page.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            canvas.drawColor(Color.WHITE);
            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_PRINT);
            imageview1.setImageBitmap(bitmap);
            page.close();*/
        }
    }




    @Override
    public void onDestroy() {
        super.onDestroy();
        if (renderer != null){
            renderer.close();
        }
    }

    public  String takeScreenshotAndSave(View view) {
        // Create a bitmap of the view
        Bitmap bitmap=loadBitmapFromView(view);

        // Create a directory for saving the screenshot
        String fileName = "image_" + new Date().getTime() + ".jpg";

        // Get the app's cache directory
        File cacheDirectory = getCacheDir();

        // Create a new file in the cache directory
        File file = new File(cacheDirectory, fileName);
        try {
            // Save the screenshot to the file
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
            // Add the screenshot to the device's media gallery
            MediaStore.Images.Media.insertImage(view.getContext().getContentResolver(), file.getAbsolutePath(), fileName, null);
            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    public  Bitmap loadBitmapFromView(View v) {
        if (v.getMeasuredHeight() <= 0) {
            v.measure(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            Bitmap b = Bitmap.createBitmap(v.getMeasuredWidth(), v.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(b);
            v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
            v.draw(c);
            return b;
        } else {
            Bitmap returnedBitmap;
            returnedBitmap = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(returnedBitmap);
            Drawable bgDrawable = v.getBackground();
            if (bgDrawable != null)
                bgDrawable.draw(canvas);
            else
                canvas.drawColor(Color.WHITE);
            v.draw(canvas);
            return returnedBitmap;
        }

    }


    public boolean createDir(File sdIconStorageDir) {

        if (!sdIconStorageDir.exists()) {
            return sdIconStorageDir.mkdirs();
        } else {
            return true;
        }
    }
    private int getRandomNumber() {
        final int min = 10;
        final int max = 800;
        return new Random().nextInt((max - min) + 1) + min;
    }
    private void saveImage() {
        saveImageToPictures();
        Log.e("TAG", "saveImage: save image");
        dir_ = new File(this.getFilesDir(), "TestPdfApp");
        createDir(dir_);
        OutputStream fOut;
        String mPath = dir_ + "" + getRandomNumber()+".png";
        path = mPath;
        File file = new File(path); // the File to save , append increasing numeric counter to prevent files from getting overwritten.
        try {
            Log.e("TAG", "saveImage: try");
            fOut = new FileOutputStream(file);
            Bitmap pictureBitmap = setViewToBitmapImage1(imageview1);
            pictureBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut); // saving the Bitmap to a file compressed as a JPEG with 85% compression rate
            fOut.flush(); // Not really required
            fOut.close(); // do not forget to close the stream
            MediaStore.Images.Media.insertImage(getContentResolver(), file.getAbsolutePath(), file.getName(), file.getName());

        } catch (IOException e) {
            e.printStackTrace();
            Log.e("TAG", "saveImage: "+e.getMessage());
        }

    }
    public void saveImageToPictures() {
        Bitmap bitmap= setViewToBitmapImage1(imageview1);
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
                Toast.makeText(CropToImageActivity.this, "Image is Successfully created ...", Toast.LENGTH_SHORT).show();
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
        DatabaseHelper myDb = new DatabaseHelper(this);
        myDb.insertPdfPath(path);
        myDb.close();
    }


    public static Bitmap setViewToBitmapImage1(View view) {

        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null)
            bgDrawable.draw(canvas);
        else
            canvas.drawColor(Color.WHITE);
        view.draw(canvas);
        return returnedBitmap;
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        home();
    }
}