package com.pdf.and.image.cropper;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.labters.documentscanner.DocumentScannerView;
import com.pdf.and.image.cropper.helper.DatabaseHelper;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.pdmodel.PDPage;
import com.tom_roush.pdfbox.pdmodel.PDPageContentStream;
import com.tom_roush.pdfbox.pdmodel.common.PDRectangle;
import com.tom_roush.pdfbox.pdmodel.graphics.image.LosslessFactory;
import com.tom_roush.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;


public class PdfActivity extends AppCompatActivity {
    private Context context;

    DocumentScannerView scannerView;

    CropImageView cropImageView;
    private Dialog myDialog1;
    ProgressDialog progressBar;
    private ArrayList<byte[]> allImagesByteList;
    private String path;
    private ImageView preview, preview2, preview3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15;
    int PICK_IMAGE_MULTIPLE = 1;

    ArrayList<Uri> mArrayUri;
    int position = 0;

    private AppCompatButton btnClear, btnPrint, btnCropNew;

    private LinearLayout layOptions, layBottom;
    ArrayList<ImageView> allImgList;
    private boolean isImagePick = false;

    public static File dir_;

    ScrollView scrollView;

    private static final int PICK_IMAGE = 99;

    private final int CAMERA = 2;

    private AppCompatButton btnMore, btnCrop, doneBtn;

    private int cameraCnt = 0;
    private int addMoreValue = 1;
    private int selectedCount = 0;
    int mIndex = -1;
    int cout = 1;
    Uri cropUri;
    private boolean isCamera = false;
    private boolean isCrop = false;
    private int cnt = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf);
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
        btnCropNew = findViewById(R.id.btnCrop);
        btnPrint = findViewById(R.id.btnPrint);
        doneBtn = findViewById(R.id.doneBtn);
        scrollView = findViewById(R.id.scrollView);
        scannerView = findViewById(R.id.document_scanner);
        cropImageView = findViewById(R.id.cropImageView);
        layBottom = findViewById(R.id.layBottom);
        scannerView.setVisibility(View.GONE);
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
        doneBtn.setVisibility(View.INVISIBLE);
        btnCropNew.setVisibility(View.INVISIBLE);
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

        btnCropNew.setOnClickListener(view ->
        {
            doneBtn.setVisibility(View.VISIBLE);
            btnMore.setVisibility(View.GONE);
            btnCrop.setVisibility(View.GONE);
            btnCropNew.setVisibility(View.GONE);
            btnClear.setVisibility(View.GONE);
            btnPrint.setVisibility(View.GONE);
            if (cout > 0) {

               /* if(cout==1 || addMoreValue==1)
                {
                    new ProcessImageTask().execute();

                }else
                    showImageSelectionDialog();*/
                showImageSelectionDialog();

                layOptions.setVisibility(View.GONE);
                scrollView.setVisibility(View.GONE);
                cropImageView.setVisibility(View.GONE);
                scannerView.setVisibility(View.VISIBLE);
            } else Toast.makeText(this, "No", Toast.LENGTH_SHORT).show();
        });

        doneBtn.setOnClickListener(view ->
        {
            Bitmap bitmap = scannerView.getCroppedImage();

            Log.e("TAG", "onCreate: " + cout);
            ImageView imageView = allImgList.get(selectedCount);
            imageView.setImageBitmap(bitmap);
            allImagesByteList.set(selectedCount,setImageByte(imageView));
            scrollView.setVisibility(View.VISIBLE);
            scannerView.setVisibility(View.GONE);
            doneBtn.setVisibility(View.GONE);


            if (isCamera) {
                btnMore.setVisibility(View.VISIBLE);
            }
            btnCrop.setVisibility(View.VISIBLE);
            btnPrint.setVisibility(View.VISIBLE);
            btnCropNew.setVisibility(View.VISIBLE);
            cropImageView.setVisibility(View.VISIBLE);
            btnClear.setVisibility(View.VISIBLE);

        });

        btnPrint.setOnClickListener(view -> {
            if (isImagePick) {
                //  showCustomPop();
                loadAllData();
                btnClear.setVisibility(View.VISIBLE);
                btnCropNew.setVisibility(View.VISIBLE);
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
        if (cnt > 1) {
            pickImage();
            layOptions.setVisibility(View.GONE);
        }


        findViewById(R.id.cardBack).setOnClickListener(v -> home());
    }

    private void home() {
        startActivity(new Intent(this, MenuActivity.class));
        finish();
    }

    private void changeHeight() {

    }
    private void move() {
        startActivity(new Intent(this, MenuActivity.class));
        finish();
    }

    private void showReward() {
        loadAllData();
    }


    private void showCustomPop() {
        TextView tv_close;
        Button btnView, btnClose;
        myDialog1 = new Dialog(context);
        myDialog1.setContentView(R.layout.reward_pop_up);
        myDialog1.setCancelable(false);
        btnView = myDialog1.findViewById(R.id.btnViewAd);
        btnClose = myDialog1.findViewById(R.id.btnClose);
        tv_close = myDialog1.findViewById(R.id.tv_close_popup);
        tv_close.setOnClickListener(v -> {
            myDialog1.dismiss();
        });
        btnView.setOnClickListener(v -> {
            myDialog1.dismiss();
            showReward();
        });

        btnClose.setOnClickListener(v -> {
            myDialog1.dismiss();
        });
        if (!isFinishing())
            myDialog1.show();


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
                } else {
                    pdfPrint();
                    Log.e("TAG", "doInBackground: else ");
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                //   progressBar.dismiss();
                btnClear.setVisibility(View.VISIBLE);
                btnCropNew.setVisibility(View.VISIBLE);
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        } else {
            intent.setAction(Intent.ACTION_GET_CONTENT);
        }
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_MULTIPLE);

    }

    private void showPictureDialog() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Select Action");
        // String[] pictureDialogItems = {"Select photo from Gallery", "Capture photo from Camera", "Select image to Crop"};
        String[] pictureDialogItems = {"Select photo from Gallery", "Capture photo from Camera"};


        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                openImageChooser();
                                break;
                            case 1:
                                isCamera = true;
                                takePhotoFromCamera();
                                break;
                            case 2:
                                pickImage();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    private void pickImage() {
        // Create an Intent to pick an image from the gallery
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE);
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

    private void tempFile(Bitmap bitmap) {
        try {
            scannerView.setImage(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // When an Image is picked
        if (requestCode == PICK_IMAGE_MULTIPLE && resultCode == RESULT_OK && null != data) {

            Log.e("TAG", "onActivityResult: pick_multiple");
            // Check for Android 11 and ensure two images are selected
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.R) {
                ClipData clipData = data.getClipData();
                if (clipData != null && clipData.getItemCount() >= 2) {
                    // Handle two image selection for Android 11
                    // Process the two images using MyCustomAsyncTask or other methods
                    if (data.getClipData() != null) {
                        cout = data.getClipData().getItemCount();
                        if (cout <= 15) {
                            new MyCustomAsyncTask(context, data, cout).execute();
                            isImagePick = true;
                            btnVisibility();
                        } else {
                            Toast.makeText(context, "max 15 images allowed", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Uri imageUri = data.getData();

                        if (imageUri != null) {
                            isImagePick = true;
                            allImgList.get(0).setImageURI(imageUri);

                            new AsyncTask<ImageView, Void, byte[]>() {
                                @Override
                                protected byte[] doInBackground(ImageView... imageViews) {
                                    return setImageByte(imageViews[0]);
                                }

                                @Override
                                protected void onPostExecute(byte[] imageBytes) {
                                    allImagesByteList.add(imageBytes);
                                    allImgList.get(0).setVisibility(View.VISIBLE);
                                    btnVisibility();
                                    int newHeight = 1400;
                                    int wt = 1200;
                                    ViewGroup.LayoutParams layoutParams = allImgList.get(0).getLayoutParams();
                                    layoutParams.height = newHeight;
                                    layoutParams.width = wt;
                                    allImgList.get(0).setLayoutParams(layoutParams);
                                }
                            }.execute(allImgList.get(0));
                        }
                    }
                } else if (data.getData() != null) {
                    // Handle single image selection here
                    Uri imageUri = data.getData();

                    if (imageUri != null) {
                        isImagePick = true;
                        allImgList.get(0).setImageURI(imageUri);
                        btnVisibility();
                        new AsyncTask<ImageView, Void, byte[]>() {
                            @Override
                            protected byte[] doInBackground(ImageView... imageViews) {
                                return setImageByte(imageViews[0]);
                            }

                            @Override
                            protected void onPostExecute(byte[] imageBytes) {
                                allImagesByteList.clear();
                                allImagesByteList.add(imageBytes);
                                allImgList.get(0).setVisibility(View.VISIBLE);

                                int newHeight = 1400;
                                int wt = 1200;
                                ViewGroup.LayoutParams layoutParams = allImgList.get(0).getLayoutParams();
                                layoutParams.height = newHeight;
                                layoutParams.width = wt;
                                allImgList.get(0).setLayoutParams(layoutParams);
                            }
                        }.execute(allImgList.get(0));
                    }

                }else {
                    Toast.makeText(context, "error", Toast.LENGTH_SHORT).show();
                    return;
                }
            } else {
                // Handle multiple image selection for pre-Android 11 devices
                if (data.getClipData() != null) {
                    cout = data.getClipData().getItemCount();
                    if (cout <= 15) {
                        new MyCustomAsyncTask(context, data, cout).execute();
                        isImagePick = true;
                        btnVisibility();
                    } else {
                        Toast.makeText(context, "max 15 images allowed", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Uri imageUri = data.getData();

                    if (imageUri != null) {
                        isImagePick = true;
                        allImgList.get(0).setImageURI(imageUri);

                        new AsyncTask<ImageView, Void, byte[]>() {
                            @Override
                            protected byte[] doInBackground(ImageView... imageViews) {
                                return setImageByte(imageViews[0]);
                            }

                            @Override
                            protected void onPostExecute(byte[] imageBytes) {
                                allImagesByteList.add(imageBytes);
                                allImgList.get(0).setVisibility(View.VISIBLE);
                                btnVisibility();
                                int newHeight = 1400;
                                int wt = 1200;
                                ViewGroup.LayoutParams layoutParams = allImgList.get(0).getLayoutParams();
                                layoutParams.height = newHeight;
                                layoutParams.width = wt;
                                allImgList.get(0).setLayoutParams(layoutParams);
                            }
                        }.execute(allImgList.get(0));
                    }
                }
            }

            position = 0;
        } else if (requestCode == CAMERA) {

            Log.e("TAG", "onActivityResult: camera");
            if (resultCode == RESULT_OK) {
                isImagePick = true;
                mIndex++;
                new MyCustomAsyncTask1(context, data, 0).execute();
            }

        } else if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
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

        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            Log.e("TAG", "onActivityResult: crop_image_activity");
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                isCrop = true;
                isImagePick = true;
                Uri imageurl = result.getUri();
                if (isCamera) {
                    allImgList.get(mIndex).setImageURI(imageurl);
                    allImagesByteList.add(setImageByte(allImgList.get(mIndex)));
                } else {
                    allImgList.get(0).setImageURI(imageurl);
                    allImagesByteList.add(setImageByte(allImgList.get(0)));
                }

                btnVisibility();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                error.printStackTrace();
            }
            position = 0;
        } else {
            // show this if no image is selected
            Toast.makeText(context, "You haven't picked Image", Toast.LENGTH_LONG).show();
        }
    }
    private void setCropper(Uri uri) {
        CropImage.activity(uri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setBorderLineColor(R.color.black)
                .setBorderCornerColor(R.color.black)
                .setBorderLineThickness(1)
                .setBorderLineThickness(1)
                .start(this);
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
        btnCropNew.setVisibility(View.VISIBLE);
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
            btnCropNew.setVisibility(View.VISIBLE);

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
            allImagesByteList.remove(mIndex-1);

        }
    }


    @Override
    public void onBackPressed() {
        move();
    }


}