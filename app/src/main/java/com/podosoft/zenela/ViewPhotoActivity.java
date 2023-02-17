package com.podosoft.zenela;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.squareup.picasso.Picasso;

public class ViewPhotoActivity extends AppCompatActivity {

    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_photo);

        imageView = findViewById(R.id.iv_view_photo);

        Intent intent = getIntent();
        String photoLink = intent.getStringExtra("photo_link");


        Picasso.get().load(photoLink).placeholder(R.drawable.placeholder_image).error(R.drawable.placeholder_image).into(imageView);

    }
}