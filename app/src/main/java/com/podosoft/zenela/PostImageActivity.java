package com.podosoft.zenela;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.podosoft.zenela.Listeners.LoginResponseListener;
import com.podosoft.zenela.MyHelpers.RealPathUtil;
import com.podosoft.zenela.Requests.RequestManagerProfile;
import com.podosoft.zenela.Responses.LoginResponse;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class PostImageActivity extends AppCompatActivity {

    final int REQUEST_CODE_PROFILE = 5;
    final int REQUEST_CODE_POST = 10;

    TextView title;
    Button btn_upload_image, btn_pick_image;
    ImageView iv_image_to_upload;
    EditText poster_comment;
    ProgressBar progressBar;

    String path;
    File fileThumb;
    Bitmap bitmapThumb;

    RequestManagerProfile managerProfile;
    String type;
    String requestType = "";

    SharedPreferences sharedPreferences = null;
    long principalId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_image);

        //
        sharedPreferences = PostImageActivity.this.getSharedPreferences("login", 0);
        principalId = sharedPreferences.getLong("principalId", 0);

        type = getIntent().getStringExtra("type");
        if (type.equals(getString(R.string.profile))){
            requestType = "profile";
        }
        else if (type.equals(getString(R.string.cover))){
            requestType = "cover";
        }
        else{
            requestType = "post_image";
        }


        btn_upload_image = findViewById(R.id.btn_upload_image);
        btn_pick_image = findViewById(R.id.btn_pick_image);
        iv_image_to_upload = findViewById(R.id.iv_image_to_upload);
        poster_comment = findViewById(R.id.poster_comment);
        progressBar = findViewById(R.id.progressBar);
        title = findViewById(R.id.tv_title);

        title.setText(type);

        clickListeners();
    }

    private void clickListeners() {

        btn_pick_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){

                    Intent intent;

                    if (requestType.equals("post_image")){
                        intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                        startActivityForResult(intent, REQUEST_CODE_POST);
                    }else {
                        intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_PICK);
                        startActivityForResult(intent, REQUEST_CODE_PROFILE);
                    }

                }else{
                    ActivityCompat.requestPermissions(PostImageActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                }
            }
        });

        btn_upload_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((path != null) && (!path.isEmpty())) {
                    showProgress();

                    managerProfile = new RequestManagerProfile(getApplicationContext());
                    if (requestType.equals("post_image") && bitmapThumb != null) {

                        managerProfile.createVideo(createPostResponseListener, new File(path), fileThumb, poster_comment.getText().toString(), principalId);

                    }else{
                        managerProfile.createPost(createPostResponseListener, new File(path), poster_comment.getText().toString(), principalId, requestType);
                    }
                }
                else{
                    Toast.makeText(PostImageActivity.this, "⚠️ Pick an image", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PROFILE && resultCode == Activity.RESULT_OK){
            if (data != null){
                Uri uri = data.getData();
                Context context = PostImageActivity.this;
                path = RealPathUtil.getRealPath(context, uri);


                Bitmap bitmap = BitmapFactory.decodeFile(path);
                iv_image_to_upload.setImageBitmap(bitmap);
                btn_upload_image.setEnabled(true);


            }
        }
        else if (requestCode == REQUEST_CODE_POST && resultCode == Activity.RESULT_OK){
            if (data != null) {
                Uri uri = data.getData();
                Context context = PostImageActivity.this;
                path = RealPathUtil.getRealPath(context, uri);


                try {
                    bitmapThumb = ThumbnailUtils.createVideoThumbnail(path, 1);
                }catch (Exception ignored){

                }
                if (bitmapThumb != null) {
                    iv_image_to_upload.setImageBitmap(bitmapThumb);
                    btn_upload_image.setEnabled(true);

                    fileThumb = new File(context.getCacheDir(), "toupload");
                    try {
                        fileThumb.createNewFile();
                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        bitmapThumb.compress(Bitmap.CompressFormat.PNG, 0, bos);
                        byte[] bitmapdata = bos.toByteArray();

                        //write the bytes in file
                        FileOutputStream fos = new FileOutputStream(fileThumb);
                        fos.write(bitmapdata);
                        fos.flush();
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else{
                    Bitmap bitmap = BitmapFactory.decodeFile(path);
                    iv_image_to_upload.setImageBitmap(bitmap);
                    btn_upload_image.setEnabled(true);
                }

            }
        }
    }

    // hide ProgressBar
    private void hideProgress(){
        progressBar.setVisibility(View.INVISIBLE);
        ViewGroup.LayoutParams layoutParams = progressBar.getLayoutParams();
        layoutParams.height = 0;
        progressBar.setLayoutParams(layoutParams);
        btn_upload_image.setEnabled(false);
    }

    // show Progress
    private void showProgress(){
        progressBar.setVisibility(View.VISIBLE);
        ViewGroup.LayoutParams layoutParams = progressBar.getLayoutParams();
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        progressBar.setLayoutParams(layoutParams);
        btn_upload_image.setEnabled(false);
    }

    LoginResponseListener createPostResponseListener = new LoginResponseListener() {
        @Override
        public void didError(String message) {
            hideProgress();
            Toast.makeText(PostImageActivity.this, "❌ " + message, Toast.LENGTH_LONG).show();
            btn_upload_image.setEnabled(true);
        }

        @Override
        public void didLogin(LoginResponse body, String message) {
            hideProgress();
            Toast.makeText(PostImageActivity.this, "✅ success" + message, Toast.LENGTH_LONG).show();
        }
    };


    
}