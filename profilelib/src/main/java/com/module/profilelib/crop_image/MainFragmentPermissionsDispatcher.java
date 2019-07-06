package com.module.profilelib.crop_image;

import android.support.v4.app.ActivityCompat;

import java.lang.ref.WeakReference;

import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.PermissionUtils;

public class MainFragmentPermissionsDispatcher 
{
    private static final int REQUEST_PICKIMAGE = 0;

    private static final String[] PERMISSION_PICKIMAGE = new String[] {"android.permission.READ_EXTERNAL_STORAGE"};

    private static final int REQUEST_CROPIMAGE = 1;

    private static final String[] PERMISSION_CROPIMAGE = new String[] {"android.permission.WRITE_EXTERNAL_STORAGE"};

    private MainFragmentPermissionsDispatcher() {
    }

    static void pickImageWithCheck(MainFragment target) {
        if (PermissionUtils.hasSelfPermissions(target, PERMISSION_PICKIMAGE)) {
            target.pickImage();
        } else {
            if (PermissionUtils.shouldShowRequestPermissionRationale(target, PERMISSION_PICKIMAGE)) {
                target.showRationaleForPick(new PickImagePermissionRequest(target));
            } else {
                ActivityCompat.requestPermissions(target, PERMISSION_PICKIMAGE, REQUEST_PICKIMAGE);
            }
        }
    }

    static void cropImageWithCheck(MainFragment target) {
        if (PermissionUtils.hasSelfPermissions(target, PERMISSION_CROPIMAGE)) {
            target.cropImage();
        } else {
            if (PermissionUtils.shouldShowRequestPermissionRationale(target, PERMISSION_CROPIMAGE)) {
                target.showRationaleForCrop(new CropImagePermissionRequest(target));
            } else {
                ActivityCompat.requestPermissions(target, PERMISSION_CROPIMAGE, REQUEST_CROPIMAGE);
            }
        }
    }

    static void onRequestPermissionsResult(MainFragment target, int requestCode, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PICKIMAGE:
                if (PermissionUtils.getTargetSdkVersion(target) < 23 && !PermissionUtils.hasSelfPermissions(target, PERMISSION_PICKIMAGE)) {
                    return;
                }
                if (PermissionUtils.verifyPermissions(grantResults)) {
                    target.pickImage();
                }
                break;
            case REQUEST_CROPIMAGE:
                if (PermissionUtils.getTargetSdkVersion(target) < 23 && !PermissionUtils.hasSelfPermissions(target, PERMISSION_CROPIMAGE)) {
                    return;
                }
                if (PermissionUtils.verifyPermissions(grantResults)) {
                    target.cropImage();
                }
                break;
            default:
                break;
        }
    }

    private static final class PickImagePermissionRequest implements PermissionRequest {
        private final WeakReference<MainFragment> weakTarget;

        private PickImagePermissionRequest(MainFragment target) {
            this.weakTarget = new WeakReference<>(target);
        }

        @Override
        public void proceed() {
            MainFragment target = weakTarget.get();
            if (target == null) return;
            ActivityCompat.requestPermissions(target, PERMISSION_PICKIMAGE, REQUEST_PICKIMAGE);
        }

        @Override
        public void cancel() {
        }
    }

    private static final class CropImagePermissionRequest implements PermissionRequest {
        private final WeakReference<MainFragment> weakTarget;

        private CropImagePermissionRequest(MainFragment target) {
            this.weakTarget = new WeakReference<>(target);
        }

        @Override
        public void proceed() {
            MainFragment target = weakTarget.get();
            if (target == null) return;
            ActivityCompat.requestPermissions(target, PERMISSION_CROPIMAGE, REQUEST_CROPIMAGE);
        }

        @Override
        public void cancel() {
        }
    }
}
