package com.matriapp.mobile.fragments;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.matriapp.mobile.R;

public class PhotoPreviewDialog extends Dialog {

    private String photoBitmap;
    private Context context;

    public PhotoPreviewDialog(Context context, String photoBitmap) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_photo_preview);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // Set window background to transparent
        ImageView imageViewPreview = findViewById(R.id.imageViewPreview);
        ImageView close = findViewById(R.id.close);
        Glide.with(context).load(photoBitmap).into(imageViewPreview);
        close.setOnClickListener(view -> dismiss());
    }
}

