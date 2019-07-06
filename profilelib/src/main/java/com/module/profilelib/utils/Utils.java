package com.module.profilelib.utils;

import android.os.Environment;
import java.io.File;

/**
 * Created by Abhishek on 02/07/19.
 */
public class Utils
{
    /**
     * @param dirName
     * @param fileName
     * @param fileType
     * @return
     */
    public static File getOutputMediaFile(String dirName, String fileName, String fileType)
    {
        //Here create a folder where bitmap image saved
        File dir = new File(Environment.getExternalStorageDirectory() + File.separator + dirName);

        // Create the storage directory if it does not exist
        if (!dir.exists())
        {
            dir.mkdirs();
        }

        File mediaFile = new File(dir.getAbsoluteFile() + File.separator + fileName + fileType);

        return mediaFile;
    }

}
