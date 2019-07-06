package com.module.profilelib;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.widget.Toast;

import com.module.profilelib.crop_image.MainFragment;
import com.module.profilelib.prefs.SharedPrefrence;
import com.module.profilelib.utils.Consts;
import com.module.profilelib.utils.FilePath;
import com.module.profilelib.utils.ImageCompression;
import com.module.profilelib.utils.ProjectUtils;
import com.module.profilelib.utils.Utils;
import java.io.File;
import java.io.IOException;

/**
 * Created by Abhishek on 01/07/19.
 */

public class CameraIntent
{
    public static final String IMAGE_JPG = "jpg";
    public static final String IMAGE_JPEG = "jpeg";
    public static final String IMAGE_PNG = "png";
    private static final String TAG = "Camera";

    /**
     * default values used by camera
     */
    private static final String IMAGE_FORMAT_JPG = ".jpg";
    private static final String IMAGE_FORMAT_JPEG = ".jpeg";
    private static final String IMAGE_FORMAT_PNG = ".png";
    private static final String IMAGE_DEFAULT_DIR = "Profile Picture";
    private static final String IMAGE_DEFAULT_NAME = "img_";
    private static final int IMAGE_CROP_STYLE = 0;
    private static final boolean IMAGE_ROTATION_OPTION = true;

    /**
     * public variables to be used in the builder
     */

    public static int REQUEST_TAKE_PHOTO = 1001;
    public static int REQUEST_GALLERY_PHOTO = 1002;
    public static int REQUEST_CROP_TAKE_PHOTO = 1003;
    public static int REQUEST_CROP_GALLERY_PHOTO = 1004;

    /**
     * Private variables
     */
    private Context context;
    private Activity activity;
    private Fragment fragment;
    private String dirName;
    private String imageName;
    private String imageType;
    private String authority;
    private String imageCameraPath;
    private int crop_style;
    private boolean isRotation;

    String imagePathNew;
    Uri picUri;
    Uri galleryPicUri;

    private SharedPrefrence prefrence;
    private MODE mode;


    /**
     * - For compressed image path
     * - Overrride method getCompressedImagePath(path)
     */
    public interface CompressedImagePath
    {
        void getCompressedImagePath(String path);
    }

    /**
     * @param builder to copy all the values from.
     */
    private CameraIntent(Builder builder)
    {
        activity = builder.activity;
        context = builder.context;
        mode = builder.mode;
        fragment = builder.fragment;
        dirName = builder.dirName;
        REQUEST_TAKE_PHOTO = builder.REQUEST_TAKE_PHOTO;
        REQUEST_GALLERY_PHOTO = builder.REQUEST_GALLERY_PHOTO;
        REQUEST_CROP_TAKE_PHOTO = builder.REQUEST_CROP_TAKE_PHOTO;
        REQUEST_CROP_GALLERY_PHOTO = builder.REQUEST_CROP_GALLERY_PHOTO;
        imageName = builder.imageName;
        imageType = builder.imageType;
        crop_style = builder.crop_style;
        isRotation = builder.isRotate;
        authority = context.getApplicationContext().getPackageName() + ".imageprovider";
        prefrence = SharedPrefrence.getInstance(context);
    }

    /**
     * Initiate the existing camera apps
     * choose photo from gallery
     * @throws NullPointerException
     */
    public void chooseFromGallery() throws NullPointerException
    {
        switch (mode)
        {
            case ACTIVITY:
                try
                {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    activity.startActivityForResult(Intent.createChooser(intent, context.getString(R.string.select_picture)), REQUEST_GALLERY_PHOTO);

                }
                catch (Exception e)
                {
                    Toast.makeText(activity, context.getString(R.string.please_give_permission), Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", context.getPackageName(), null);
                    intent.setData(uri);
                    activity.startActivity(intent);
                }
                break;

            case FRAGMENT:
                try
                {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    fragment.startActivityForResult(Intent.createChooser(intent, context.getString(R.string.select_picture)), REQUEST_GALLERY_PHOTO);
                }
                catch (Exception e)
                {
                    Toast.makeText(context, context.getString(R.string.please_give_permission), Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", context.getPackageName(), null);
                    intent.setData(uri);
                    fragment.startActivity(intent);
                }
                break;
        }
    }

    /**
     * Called after launching the picker with the same values of Activity.getImageFromResult
     * in order to resolve the result and get the image path.
     * @param imageReturnedIntent returned intent where is the image data.
     * @return path to the saved image.
     */
    public Uri getGalleryUriPath(Intent imageReturnedIntent)
    {
        if (imageReturnedIntent != null)
        {
            galleryPicUri = imageReturnedIntent.getData();
        }

        return galleryPicUri;
    }

    /**
     * Initiate the existing camera apps
     * take photo from camera intent
     * @throws NullPointerException
     */
    public void takePicture() throws NullPointerException, IllegalAccessException
    {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            switch (mode)
            {
                case ACTIVITY:
                    if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null)
                    {
                        //Here get folder and image file name
                        File file = Utils.getOutputMediaFile(dirName, imageName, imageType);

                        if(file != null)
                        {
                            //Here check android version camera
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                            {
                                picUri = FileProvider.getUriForFile(context, authority, file);
                            } else
                            {
                                picUri = Uri.fromFile(file);
                            }

                            prefrence.setValue(Consts.IMAGE_URI_CAMERA, picUri.toString());
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, picUri);
                            activity.startActivityForResult(takePictureIntent,REQUEST_TAKE_PHOTO);
                        }
                    }
                    else
                    {
                        throw new IllegalAccessException("Unable to open camera");
                    }
                    break;

                case FRAGMENT:
                    if (takePictureIntent.resolveActivity(fragment.getActivity().getPackageManager()) != null)
                    {
                        //Here get folder and image file name
                        File file = Utils.getOutputMediaFile(dirName, imageName, imageType);

                        if(file != null)
                        {
                            //Here check android version camera
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                            {
                                picUri = FileProvider.getUriForFile(context, authority, file);
                            } else
                            {
                                picUri = Uri.fromFile(file);
                            }

                            prefrence.setValue(Consts.IMAGE_URI_CAMERA, picUri.toString());
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, picUri);
                            fragment.startActivityForResult(takePictureIntent,REQUEST_TAKE_PHOTO);
                        }
                    }
                    else
                    {
                        throw new IllegalAccessException("Unable to open camera");
                    }
                    break;
            }
    }

    /**
     * @return the uri image path
     */
    public Uri getCameraUriPath()
    {
        String imagePath = prefrence.getValue(Consts.IMAGE_URI_CAMERA);
        Uri newPicUri = Uri.parse(imagePath);
        getCameraPath(newPicUri);
        return newPicUri;
    }

    /**
     * @return the string image path
     * @param uri
     */
    public String getCameraPath(Uri uri)
    {
        // Check if we're running on Android 7.0 or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
        {
            imageCameraPath = FilePath.getFilePathForN(uri,context);
        }
        else
        {
            imageCameraPath = FilePath.getPath(context,uri);
        }

        return imageCameraPath;
    }

    /**
     * getCompressedImagePath
     * @param picUri
     * @param compressedImagePath
     */
    public void getCompressedImagePath(Uri picUri,final CompressedImagePath compressedImagePath)
    {
        if(picUri != null)
        {
            String pathOfImage = picUri.getPath();

            ImageCompression imageCompression = new ImageCompression(context,dirName,imageType);
            imageCompression.execute(pathOfImage);
            imageCompression.setOnTaskFinishedEvent(new ImageCompression.AsyncResponse()
            {
                @Override
                public void processFinish(String imagePath)
                {
                    imagePathNew = imagePath;
                    compressedImagePath.getCompressedImagePath(imagePathNew);
                }
            });
        }
    }

    /**
     * Start Cropping
     * @param uri
     * @param requestCode
     */
    public void startCropping(Uri uri, int requestCode)
    {
        switch (mode)
        {
            case ACTIVITY:
                Intent intent = new Intent(context, MainFragment.class);
                intent.putExtra("imageUri", uri.toString());
                intent.putExtra("requestCode", requestCode);
                intent.putExtra("crop_style", crop_style);
                intent.putExtra("isRotation", isRotation);
                activity.startActivityForResult(intent, requestCode);
                break;

            case FRAGMENT:
                Intent intent1 = new Intent(context, MainFragment.class);
                intent1.putExtra("imageUri", uri.toString());
                intent1.putExtra("requestCode", requestCode);
                intent1.putExtra("crop_style", crop_style);
                intent1.putExtra("isRotation", isRotation);
                fragment.startActivityForResult(intent1, requestCode);
                break;
        }
    }

    /**
     * Deletes the saved camera image
     */
    public void deleteCaptureImage()
    {
        if (imageCameraPath != null)
        {
            File image = new File(imageCameraPath);
            if (image.exists())
            {
                image.delete();
            }
        }
    }

    /**
     * Deletes the saved camera image
     */
    public void deleteImageFolder()
    {
        File myDir = new File(Environment.getExternalStorageDirectory() + File.separator + dirName);

        if (myDir.exists() && myDir.isDirectory())
        {
            String[] children = myDir.list();
            for (int i = 0; i < children.length; i++)
            {
                File fileDirChild =  new File(myDir, children[i]);
                String[] subchildren = fileDirChild.list();
                if (fileDirChild.exists() && fileDirChild.isDirectory())
                {
                    /*for (int j = 0 ; j<subchildren.length;j++)
                    {
                        new File(fileDirChild, subchildren[j]).delete();
                    }*/
                }
                else
                {
                    fileDirChild.delete();
                }
            }
        }
        else
        {
            myDir.delete();
        }
    }

    private enum MODE {ACTIVITY, FRAGMENT}

    /**
     * Camera builder declaration
     */
    public static class Builder
    {
        private Context context;
        private Activity activity;
        private Fragment fragment;
        private String dirName;
        private String imageName;
        private String imageType;
        private MODE mode;
        private int crop_style;
        private boolean isRotate;
        private int REQUEST_TAKE_PHOTO;
        private int REQUEST_GALLERY_PHOTO;
        private int REQUEST_CROP_TAKE_PHOTO;
        private int REQUEST_CROP_GALLERY_PHOTO;

        public Builder()
        {
            dirName = CameraIntent.IMAGE_DEFAULT_DIR;
            imageName = CameraIntent.IMAGE_DEFAULT_NAME + System.currentTimeMillis();
            imageType = CameraIntent.IMAGE_FORMAT_JPG;
            crop_style = CameraIntent.IMAGE_CROP_STYLE;
            isRotate = CameraIntent.IMAGE_ROTATION_OPTION;
            REQUEST_TAKE_PHOTO = CameraIntent.REQUEST_TAKE_PHOTO;
            REQUEST_GALLERY_PHOTO = CameraIntent.REQUEST_GALLERY_PHOTO;
            REQUEST_CROP_TAKE_PHOTO = CameraIntent.REQUEST_CROP_TAKE_PHOTO;
            REQUEST_CROP_GALLERY_PHOTO = CameraIntent.REQUEST_CROP_GALLERY_PHOTO;
        }

        public Builder setDirectory(String dirName)
        {
            if (dirName != null)
                this.dirName = dirName;
            return this;
        }

        public Builder setCropStyle(int crop_style)
        {
            this.crop_style = crop_style;
            return this;
        }

        public Builder setRotationEnable(boolean isRotate)
        {
            this.isRotate = isRotate;
            return this;
        }

        public Builder setTakePhotoRequestCode(int requestCode)
        {
            this.REQUEST_TAKE_PHOTO = requestCode;
            return this;
        }

        public Builder setGalleryPhotoRequestCode(int requestCode)
        {
            this.REQUEST_GALLERY_PHOTO = requestCode;
            return this;
        }

        public Builder setName(String imageName)
        {
            if (imageName != null)
                this.imageName = imageName;
            return this;
        }

        public Builder setImageFormat(String imageFormat)
        {
            if (TextUtils.isEmpty(imageFormat))
            {
                return this;
            }

            switch (imageFormat)
            {
                case "png":
                case "PNG":
                case ".png":
                    this.imageType = IMAGE_FORMAT_PNG;
                    break;
                case "jpg":
                case "JPG":
                case ".jpg":
                    this.imageType = IMAGE_FORMAT_JPG;
                    break;
                case "jpeg":
                case "JPEG":
                case ".jpeg":
                    this.imageType = IMAGE_FORMAT_JPEG;
                    break;
                default:
                    this.imageType = IMAGE_FORMAT_JPG;
            }
            return this;
        }

        public CameraIntent build(Activity activity)
        {
            this.activity = activity;
            context = activity.getApplicationContext();
            mode = MODE.ACTIVITY;
            return new CameraIntent(this);
        }

        public CameraIntent build(Fragment fragment)
        {
            this.fragment = fragment;
            context = fragment.getActivity().getApplicationContext();
            mode = MODE.FRAGMENT;
            return new CameraIntent(this);
        }
    }

    public static interface CropStyle
    {
        int CropStyleAll = 0;
        int CropStyleFitImage = 1;
        int CropStyleImage1_1 = 2;
        int CropStyleImage3_4 = 3;
        int CropStyleImage4_3 = 4;
        int CropStyleImage9_16 = 5;
        int CropStyleImage16_9 = 6;
        int CropStyleImage7_5 = 7;
        int CropStyleFree = 8;
        int CropStyleCircle = 9;
        int CropStyleCropAsSquare = 10;
    }
}
