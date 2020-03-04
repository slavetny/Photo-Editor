package com.slavetny.photoeditor;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.slavetny.photoeditor.dialog.SelectFilterDialog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;

/**
 *
 * Powered by Vladislav Slavetny
 *
 */

public class MainActivity extends AppCompatActivity implements
        View.OnClickListener {

    private String access_key = "9eDe0oYQLHAAAAAAAAAADf5Qdo_iumSA8jUTAxm49f1g52aeguIZUJThRhOdF-bs";
    private DbxClientV2 client;

    private Button selectButton;
    private ImageButton saveButton;
    private ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DbxRequestConfig config = DbxRequestConfig.newBuilder("dropbox/PhotoEditor-by-Slavetny").build();
        client = new DbxClientV2(config, access_key);

        selectButton = findViewById(R.id.select_button);
        saveButton = findViewById(R.id.savePhoto);
        image = findViewById(R.id.photo);

        selectButton.setOnClickListener(this);
        saveButton.setOnClickListener(this);
        image.setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                image.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void saveImage(Bitmap image) {
        String path = Environment.getExternalStorageDirectory().toString();
        OutputStream fOutputStream;
        File file = new File(path + "/Captures/", "screen.jpg");
        if (!file.exists()) {
            file.mkdirs();
        }

        try {
            fOutputStream = new FileOutputStream(file);

            image.compress(Bitmap.CompressFormat.JPEG, 100, fOutputStream);

            fOutputStream.flush();
            fOutputStream.close();

            MediaStore.Images.Media.insertImage(getContentResolver(), file.getAbsolutePath(), file.getName(), file.getName());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(this, "Save Failed", Toast.LENGTH_SHORT).show();
            return;
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Save Failed", Toast.LENGTH_SHORT).show();
            return;
        }

        loadPhotoDropbox(file.getPath());
    }

    private void loadPhotoDropbox(String filename) {
        try (InputStream in = new FileInputStream(filename)) {
            FileMetadata metadata = client.files().uploadBuilder("/" + filename)
                    .uploadAndFinish(in);
        } catch (Exception e) {
            e.fillInStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.select_button:

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);

                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);

                break;
            case R.id.photo:

                Dialog selectFilter = new SelectFilterDialog(MainActivity.this, new SelectFilterDialog.OnDialogClickListener() {
                    @Override
                    public void onDialogImageRunClick(int filterColor) {
                        image.setColorFilter(filterColor, PorterDuff.Mode.LIGHTEN);
                    }
                });

                selectFilter.show();

                break;
            case R.id.savePhoto:

                Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
                saveImage(bitmap);

                break;
            default:
                break;
        }
    }
}

