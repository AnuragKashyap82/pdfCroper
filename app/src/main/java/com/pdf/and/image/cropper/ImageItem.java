package com.pdf.and.image.cropper;

import android.net.Uri;

// ImageItem.java
public class ImageItem {
    private Uri uri;

    public ImageItem(Uri uri) {
        this.uri = uri;
    }

    public Uri getUri() {
        return uri;
    }
}

