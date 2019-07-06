## Simple android camera to capture, take from gallery and get compressed imagepath in few simple steps:

## 1. Build Camera
// Create global camera reference in an activity or fragment
    CameraIntent cameraIntent;

// Initialize and customise

    1. Can create folder where captured image saved.
    2. Can create image name and image format.
    3. Can set cropping style.
    4. Can set rotation.

    cameraIntent = new CameraIntent.Builder()
                    .setDirectory("Profile Picture")
                    .setName("abhi_" + System.currentTimeMillis())
                    .setImageFormat(CameraIntent.IMAGE_JPEG)
                    .setCropStyle(CameraIntent.CropStyle.CropStyleAll)
                    .setRotationEnable(true)
                    .build(this);

## 2. Capture Image
// Call the camera takePicture method to open the existing camera             

    try
    {
        cameraIntent.takePicture();
    }
    catch (Exception e)
    {
        e.printStackTrace();
    }

## 3. Gallery Image
// Call the camera takePicture method to open the existing camera

    try
    {
        cameraIntent.chooseFromGallery();
    }
    catch (Exception e)
    {
        e.printStackTrace();
    }

## 4. Get Camera Uri and crop
// Get the bitmap and image path onActivityResult of an activity or fragment

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
     {
        super.onActivityResult(requestCode, resultCode, data);

        //For fragment use Activity.RESULT_OK
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
    }   

## 5. Get Gallery Uri and crop
// Get the bitmap and image path onActivityResult of an activity or fragment

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
     {
        super.onActivityResult(requestCode, resultCode, data);

        //For fragment use Activity.RESULT_OK
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


## 6. Delete captured image
//  If the saved imagepath is not required use following code
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        camera.deleteCaptureImage();
    }

## 7. Delete image folder
//  If the saved images is not required use following code
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        camera.deleteImageFolder();
    }


## Permissions is required
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.CAMERA" />
