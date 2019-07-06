package com.module.profilelib.crop_image;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.isseiaoki.simplecropview.CropImageView;
import com.isseiaoki.simplecropview.callback.CropCallback;
import com.isseiaoki.simplecropview.callback.LoadCallback;
import com.isseiaoki.simplecropview.callback.SaveCallback;
import com.isseiaoki.simplecropview.util.Utils;
import com.module.profilelib.CameraIntent;
import com.module.profilelib.R;

import java.io.File;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;

public class MainFragment extends FragmentActivity
{
    private static final int REQUEST_PICK_IMAGE = 10011;
    private static final int REQUEST_SAF_PICK_IMAGE = 10012;
    private static final String PROGRESS_DIALOG = "ProgressDialog";

    Uri myUri;
    int requestCode;
    LinearLayout tab_layout,llRotation;

    int cropStyle = 0 ;  //CropStyle 0 = show all setting
    boolean isRotation = true; //isRotation = true indicate rotation options

    private CropImageView mCropView;
    private LinearLayout mRootLayout;

    public MainFragment()
    {}

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_main);

        myUri = Uri.parse(getIntent().getExtras().getString("imageUri"));
        requestCode = getIntent().getExtras().getInt("requestCode");
        cropStyle = getIntent().getExtras().getInt("crop_style");
        isRotation = getIntent().getExtras().getBoolean("isRotation");

        // bind Views
        bindViews();
        // apply custom font
        FontUtils.setFont(mRootLayout);
        mCropView.startLoad(myUri, mLoadCallback);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent result)
    {
        super.onActivityResult(requestCode, resultCode, result);

        if (requestCode == REQUEST_PICK_IMAGE && resultCode == Activity.RESULT_OK)
        {
            showProgress();
            String urij = result.getData().toString();
            mCropView.startLoad(result.getData(), mLoadCallback);
        }
        else if (requestCode == REQUEST_SAF_PICK_IMAGE && resultCode == Activity.RESULT_OK)
        {
            showProgress();
            String uri = Utils.ensureUriPermission(this, result).toString();
            mCropView.startLoad(Utils.ensureUriPermission(this, result), mLoadCallback);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MainFragmentPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    // Bind views //////////////////////////////////////////////////////////////////////////////////
    private void bindViews()
    {
        mCropView = findViewById(R.id.cropImageView);
        findViewById(R.id.buttonDone).setOnClickListener(btnListener);
        findViewById(R.id.buttonFitImage).setOnClickListener(btnListener);
        findViewById(R.id.button1_1).setOnClickListener(btnListener);
        findViewById(R.id.button3_4).setOnClickListener(btnListener);
        findViewById(R.id.button4_3).setOnClickListener(btnListener);
        findViewById(R.id.button9_16).setOnClickListener(btnListener);
        findViewById(R.id.button16_9).setOnClickListener(btnListener);
        findViewById(R.id.buttonFree).setOnClickListener(btnListener);
        findViewById(R.id.buttonPickImage).setOnClickListener(btnListener);
        findViewById(R.id.buttonRotateLeft).setOnClickListener(btnListener);
        findViewById(R.id.buttonRotateRight).setOnClickListener(btnListener);
        findViewById(R.id.buttonCustom).setOnClickListener(btnListener);
        findViewById(R.id.buttonCircle).setOnClickListener(btnListener);
        findViewById(R.id.buttonShowCircleButCropAsSquare).setOnClickListener(btnListener);
        mRootLayout = findViewById(R.id.layout_root);
        tab_layout = findViewById(R.id.tab_layout);
        llRotation = findViewById(R.id.llRotation);

        SetCropStyle(); //Set crop style

        setRotationOption(isRotation);
    }

    private void setRotationOption(boolean isRotation)
    {
        if(isRotation)
        {
            llRotation.setVisibility(View.VISIBLE);
        }
        else
        {
            llRotation.setVisibility(View.INVISIBLE);
        }
    }

    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    public void pickImage()
    {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT)
        {
            startActivityForResult(new Intent(Intent.ACTION_GET_CONTENT).setType("image/*"), REQUEST_PICK_IMAGE);
        }
        else
        {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            startActivityForResult(intent, REQUEST_SAF_PICK_IMAGE);
        }
    }

    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    public void cropImage()
    {
        showProgress();
        Log.e("cropImage", "called");
        mCropView.startCrop(createSaveUri(), mCropCallback, mSaveCallback);
    }

    @OnShowRationale(Manifest.permission.READ_EXTERNAL_STORAGE)
    public void showRationaleForPick(PermissionRequest request)
    {
        showRationaleDialog(R.string.permission_pick_rationale, request);
    }

    @OnShowRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    public void showRationaleForCrop(PermissionRequest request)
    {
        showRationaleDialog(R.string.permission_crop_rationale, request);
    }

    public void showProgress()
    {
        ProgressDialogFragment f = ProgressDialogFragment.getInstance();
        getSupportFragmentManager()
                .beginTransaction()
                .add(f, PROGRESS_DIALOG)
                .commitAllowingStateLoss();
    }

    public void dismissProgress()
    {
        //if (!isAdded()) return;
        FragmentManager manager = getSupportFragmentManager();
        if (manager == null) return;
        ProgressDialogFragment f = (ProgressDialogFragment) manager.findFragmentByTag(PROGRESS_DIALOG);
        if (f != null) {
            getSupportFragmentManager().beginTransaction().remove(f).commitAllowingStateLoss();
        }
    }

    public Uri createSaveUri()
    {
        return Uri.fromFile(new File(this.getCacheDir(), "cropped"));
    }

    private void showRationaleDialog(@StringRes int messageResId, final PermissionRequest request)
    {
        new AlertDialog.Builder(this)
                .setPositiveButton(R.string.button_allow, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(@NonNull DialogInterface dialog, int which)
                    {
                        request.proceed();
                    }
                })
                .setNegativeButton(R.string.button_deny, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(@NonNull DialogInterface dialog, int which)
                    {
                        request.cancel();
                    }
                })
                .setCancelable(false)
                .setMessage(messageResId)
                .show();
    }

    // Handle button event /////////////////////////////////////////////////////////////////////////

    private final View.OnClickListener btnListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            if(R.id.buttonDone == v.getId())
            {
                MainFragmentPermissionsDispatcher.cropImageWithCheck(MainFragment.this);
            }
            else if(R.id.buttonFitImage == v.getId())
            {
                mCropView.setCropMode(CropImageView.CropMode.FIT_IMAGE);
            }
            else if(R.id.button1_1 == v.getId())
            {
                mCropView.setCropMode(CropImageView.CropMode.SQUARE);
            }
            else if(R.id.button3_4 == v.getId())
            {
                mCropView.setCropMode(CropImageView.CropMode.RATIO_3_4);
            }
            else if(R.id.button4_3 == v.getId())
            {
                mCropView.setCropMode(CropImageView.CropMode.RATIO_4_3);
            }
            else if(R.id.button9_16 == v.getId())
            {
                mCropView.setCropMode(CropImageView.CropMode.RATIO_9_16);
            }
            else if(R.id.button16_9 == v.getId())
            {
                mCropView.setCropMode(CropImageView.CropMode.RATIO_16_9);
            }
            else if(R.id.buttonCustom == v.getId())
            {
                mCropView.setCustomRatio(7, 5);
            }
            else if(R.id.buttonFree == v.getId())
            {
                mCropView.setCropMode(CropImageView.CropMode.FREE);
            }
            else if(R.id.buttonCircle == v.getId())
            {
                mCropView.setCropMode(CropImageView.CropMode.CIRCLE);
            }
            else if(R.id.buttonShowCircleButCropAsSquare == v.getId())
            {
                mCropView.setCropMode(CropImageView.CropMode.CIRCLE_SQUARE);
            }
            else if(R.id.buttonRotateLeft == v.getId())
            {
                mCropView.rotateImage(CropImageView.RotateDegrees.ROTATE_M90D);
            }
            else if(R.id.buttonRotateRight == v.getId())
            {
                mCropView.rotateImage(CropImageView.RotateDegrees.ROTATE_90D);
            }
            else if(R.id.buttonPickImage == v.getId())
            {
                MainFragmentPermissionsDispatcher.pickImageWithCheck(MainFragment.this);
            }
        }
    };

    private void SetCropStyle()
    {
        tab_layout.setVisibility(View.GONE);

        if (cropStyle == CameraIntent.CropStyle.CropStyleFitImage)
        {
            mCropView.setCropMode(CropImageView.CropMode.FIT_IMAGE);

        } else if (cropStyle == CameraIntent.CropStyle.CropStyleImage1_1)
        {
            mCropView.setCropMode(CropImageView.CropMode.SQUARE);

        } else if (cropStyle == CameraIntent.CropStyle.CropStyleImage3_4)
        {
            mCropView.setCropMode(CropImageView.CropMode.RATIO_3_4);

        } else if (cropStyle == CameraIntent.CropStyle.CropStyleImage4_3)
        {
            mCropView.setCropMode(CropImageView.CropMode.RATIO_4_3);

        } else if (cropStyle == CameraIntent.CropStyle.CropStyleImage9_16)
        {
            mCropView.setCropMode(CropImageView.CropMode.RATIO_9_16);

        } else if (cropStyle == CameraIntent.CropStyle.CropStyleImage16_9)
        {
            mCropView.setCropMode(CropImageView.CropMode.RATIO_16_9);

        } else if (cropStyle == CameraIntent.CropStyle.CropStyleImage7_5)
        {
            mCropView.setCustomRatio(7, 5);

        } else if (cropStyle == CameraIntent.CropStyle.CropStyleFree)
        {
            mCropView.setCropMode(CropImageView.CropMode.FREE);

        } else if (cropStyle == CameraIntent.CropStyle.CropStyleCircle)
        {
            mCropView.setCropMode(CropImageView.CropMode.CIRCLE);

        } else if (cropStyle == CameraIntent.CropStyle.CropStyleCropAsSquare)
        {
            mCropView.setCropMode(CropImageView.CropMode.CIRCLE_SQUARE);
        }
        else
        {
            tab_layout.setVisibility(View.VISIBLE);
        }
    }

    // Callbacks ///////////////////////////////////////////////////////////////////////////////////

    private final LoadCallback mLoadCallback = new LoadCallback()
    {
        @Override
        public void onError(Throwable e)
        {
            dismissProgress();
        }
        @Override
        public void onSuccess()
        {
            dismissProgress();
            Log.e("", "success");
        }

        public void onError()
        {
            dismissProgress();
            Log.e("", "error");
        }
    };

    private final CropCallback mCropCallback = new CropCallback()
    {
        @Override
        public void onSuccess(Bitmap cropped)
        {}

        public void onError()
        {}

        @Override
        public void onError(Throwable e)
        {
            dismissProgress();
        }
    };

    private final SaveCallback mSaveCallback = new SaveCallback()
    {
        public void onSuccess(Uri outputUri)
        {
            dismissProgress();
            //addPicture.startResultActivity(outputUri);

            Intent i = getIntent();
            i.putExtra("resultUri", outputUri.toString());
            setResult(requestCode, i);
            finish();
        }

        @Override
        public void onError(Throwable e)
        {
            dismissProgress();
        }

        public void onError()
        {
            dismissProgress();
        }
    };
}
