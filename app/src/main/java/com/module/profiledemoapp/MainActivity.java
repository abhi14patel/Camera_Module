package com.module.profiledemoapp;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.module.profilelib.CameraIntent;
import com.module.profilelib.bottomsheet.BottomDialog;
import com.module.profilelib.circleimageview.CircleImageView;
import com.module.profilelib.utils.ImageCompression;
import com.module.profilelib.utils.ProjectUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;

public class MainActivity extends AppCompatActivity
{
    Context mContext;
    AppCompatImageView ivTakePicture;
    CircleImageView ivUser;
    BottomDialog bottomDialog;
    CameraIntent cameraIntent;

    public static final int PERMISSION_CODE = 100;
    String permission[] = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = MainActivity.this;

        cameraIntent = new CameraIntent.Builder()
                .setDirectory("Profile Picture")
                .setName("abhi_" + System.currentTimeMillis())
                .setImageFormat(CameraIntent.IMAGE_JPEG)
                .setCropStyle(CameraIntent.CropStyle.CropStyleAll)
                .setRotationEnable(true)
                .build(this);

        ivUser = findViewById(R.id.ivUser);
        ivTakePicture = findViewById(R.id.ivTakePicture);

        ivTakePicture.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (ProjectUtils.hasMultiplePermissionInManifest(MainActivity.this,PERMISSION_CODE,permission))
                {
                    bottomSheetMenu();
                }
            }
        });
    }

    /**
     * Here we create dynamic view of bottom sheet and
     * perform action of camera and gallery.
     * - Can add custom view
     * - Can put title of bottom sheet
     * - Can put title with color
     * - Can change background color of dialog
     */
    private void bottomSheetMenu()
    {
        bottomDialog = new BottomDialog.Builder(this)
                .setTitle("Profile Photo")
                .setTextColor(mContext.getResources().getColor(R.color.colorWhite))
                .setBackgroundColor(mContext.getResources().getColor(R.color.colorBlack))
                .onGallery(new BottomDialog.ButtonCallback()
                {
                    @Override
                    public void onClick(@NonNull BottomDialog dialog)
                    {
                        try
                        {
                            cameraIntent.chooseFromGallery();
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }

                    }
                })
                .onCamera(new BottomDialog.ButtonCallback()
                {
                    @Override
                    public void onClick(@NonNull BottomDialog dialog)
                    {

                        try
                        {
                            cameraIntent.takePicture();
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                })
                .onRemove(new BottomDialog.ButtonCallback()
                {
                    @Override
                    public void onClick(@NonNull BottomDialog dialog)
                    {
                        Toast.makeText(MainActivity.this,"Remove",Toast.LENGTH_SHORT).show();
                    }
                })
                .build();
        bottomDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CameraIntent.REQUEST_TAKE_PHOTO && resultCode == RESULT_OK)
        {
            Uri tempUri = cameraIntent.getCameraUriPath();

            cameraIntent.startCropping(tempUri, CameraIntent.REQUEST_CROP_TAKE_PHOTO);
        }

        if (requestCode == CameraIntent.REQUEST_CROP_TAKE_PHOTO)
        {
            if (data != null)
            {
                Uri picUri = Uri.parse(data.getExtras().getString("resultUri"));

                cameraIntent.getCompressedImagePath(picUri, new CameraIntent.CompressedImagePath()
                {
                    @Override
                    public void getCompressedImagePath(String path)
                    {
                        String finalPath = "file://" + path;

                        Glide.with(mContext)
                                .load(finalPath)
                                .apply(new RequestOptions().placeholder(R.drawable.ic_default_user).error(R.drawable.ic_default_user))
                                .into(ivUser);
                    }
                });
            }
        }

        if (requestCode == CameraIntent.REQUEST_GALLERY_PHOTO && resultCode == RESULT_OK)
        {
            Uri tempUri = cameraIntent.getGalleryUriPath(data);

            cameraIntent.startCropping(tempUri, CameraIntent.REQUEST_CROP_GALLERY_PHOTO);
        }

        if (requestCode == CameraIntent.REQUEST_CROP_GALLERY_PHOTO)
        {
            if (data != null)
            {
                Uri picUri = Uri.parse(data.getExtras().getString("resultUri"));

                cameraIntent.getCompressedImagePath(picUri, new CameraIntent.CompressedImagePath()
                {
                    @Override
                    public void getCompressedImagePath(String path)
                    {
                        String finalPath = "file://" + path;

                        Glide.with(mContext)
                                .load(finalPath)
                                .apply(new RequestOptions().placeholder(R.drawable.ic_default_user).error(R.drawable.ic_default_user))
                                .into(ivUser);
                    }
                });
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        boolean hasAllPermissions = false;

        switch (requestCode)
        {
            case PERMISSION_CODE:

                if (grantResults.length > 0 && (grantResults[0] == PackageManager.PERMISSION_GRANTED) && (grantResults[1] == PackageManager.PERMISSION_GRANTED))
                {
                    hasAllPermissions = true;
                }
                else
                {
                    hasAllPermissions = false;

                    ProjectUtils.showDialog(mContext, getString(R.string.app_name), getString(R.string.allow_all_permissions), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i)
                        {
                            if (ProjectUtils.hasMultiplePermissionInManifest(MainActivity.this,PERMISSION_CODE,permission))
                            {
                                bottomSheetMenu();
                            }
                        }
                    }, new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i)
                        {
                            dialogInterface.dismiss();
                        }
                    }, false);

                }

                if(hasAllPermissions)bottomSheetMenu();
                break;
        }
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();

        finish();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        cameraIntent.deleteImageFolder();
    }
}
