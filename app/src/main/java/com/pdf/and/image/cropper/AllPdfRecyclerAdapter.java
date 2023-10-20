package com.pdf.and.image.cropper;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;
import java.io.File;
import java.io.IOException;
import java.util.List;


public class AllPdfRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<String> recyclerItem;
    private Context ctx;
    PdfRenderer pdfRenderer;
    PdfRenderer.Page currentPage;

    public AllPdfRecyclerAdapter(List<String> recyclerItem, Context ctx) {
        this.recyclerItem = recyclerItem;
        this.ctx = ctx;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lay_pdf1, parent, false);
        return new QuoteViewHolder(view);


    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position)
    {
        if (isImage(recyclerItem.get(position)))
        {
            Bitmap bmImg = BitmapFactory.decodeFile(recyclerItem.get(position));
            ((QuoteViewHolder) holder).view.setImageBitmap(bmImg);
            ((QuoteViewHolder) holder).cardView.setBackgroundResource(R.drawable.ic_one);
        }else {
            File pdfFile = new File(recyclerItem.get(position));
            try {
                ((QuoteViewHolder) holder).cardView.setBackgroundResource(R.drawable.ic_two);
                pdfRenderer = new PdfRenderer(ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY));
                currentPage = pdfRenderer.openPage(0);
                Bitmap bitmap = Bitmap.createBitmap(currentPage.getWidth(), currentPage.getHeight(), Bitmap.Config.ARGB_8888);
                currentPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
                ((QuoteViewHolder) holder).view.setImageBitmap(bitmap);
                // You can use the PDFView for additional features like zooming and scrolling.


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ((QuoteViewHolder) holder).cardView.setOnClickListener(view -> {

            if (isImage(recyclerItem.get(position))) {
                Intent intent = new Intent(ctx, ViewImageActivity.class);
                intent.putExtra("path", recyclerItem.get(position));
                ctx.startActivity(intent);
            }else  {
                openPDF(recyclerItem.get(position));
            }

        });

       /* ((QuoteViewHolder) holder).btnShare.setOnClickListener(view -> {
            if (isImage(recyclerItem.get(position))) {
                shareImage(recyclerItem.get(position));
            } else shareFile(recyclerItem.get(position));
        });*/
    }


    public void shareFile(String path) {
        File outputPath = new File(path);
        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("application/pdf");
            Uri fileUri = FileProvider.getUriForFile(ctx.getApplicationContext(),
                    ctx.getPackageName() + ".fileprovider", outputPath);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
            ctx.startActivity(Intent.createChooser(shareIntent, "Share it"));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(ctx, "No Application Available to Share PDF", Toast.LENGTH_LONG).show();
        }
    }

    private boolean isImage(String filePath) {
        String extension = filePath.substring(filePath.lastIndexOf(".")).toLowerCase();
        return extension.equals(".jpg") || extension.equals(".jpeg") || extension.equals(".png") || extension.equals(".gif") || extension.equals(".bmp");
    }

    private void shareImage(String filePath) {
        File outputPath = new File(filePath);
        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("image/*");
            Uri fileUri = FileProvider.getUriForFile(ctx.getApplicationContext(),
                    ctx.getPackageName() + ".fileprovider", outputPath);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
            ctx.startActivity(Intent.createChooser(shareIntent, "Share it"));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(ctx, "Error While Opening Image", Toast.LENGTH_LONG).show();
        }
    }

    private void openPDF(String path)
    {
        File file = new File(path);
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(FileProvider.getUriForFile(ctx, ctx.getPackageName() + ".fileprovider", file), "application/pdf");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            ctx.startActivity(intent);
        } catch (ActivityNotFoundException e)
        {
            //  Toast.makeText(ctx, "No Application Available to View PDF", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(ctx, View.class);
            intent.putExtra("path", path);
            ctx.startActivity(intent);

        }

    }


    @Override
    public int getItemCount() {
        return recyclerItem.size();
    }


    public static class QuoteViewHolder extends RecyclerView.ViewHolder {
       // public TextView tvName;
      //  public Button btnShare, btnOpen;
      //  public ImageView imgView;
        ImageView view;
        CardView cardView;

        public QuoteViewHolder(@NonNull View itemView) {
            super(itemView);
          //  tvName = itemView.findViewById(R.id.tvName);
          //  btnOpen = itemView.findViewById(R.id.btnOpen);
          //  btnShare = itemView.findViewById(R.id.btnShare);
          //  imgView = itemView.findViewById(R.id.imgViewPdf);
            view=itemView.findViewById(R.id.imgViewPdf);
            cardView=itemView.findViewById(R.id.layPdf);
        }
    }


}
