package com.levspb666.alphabet.util.settings;

import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import static com.levspb666.alphabet.Settings.USER_FON_NAME;
import static com.levspb666.alphabet.Settings.USER_FON_PATH;
import static com.levspb666.alphabet.Settings.fon;

public class FileManager {

    public static void deleteFile(String inputPath) throws Exception {
        try {
            // delete the original file
            new File(inputPath).delete();
        } catch (Exception e) {
            Log.e("file", e.getMessage());
            throw new Exception("delete");
        }
    }

    public static void copyFile(String inputPath, String outputPath) throws Exception {
        File dir = new File(outputPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        try (InputStream in = new FileInputStream(inputPath);
             OutputStream out = new FileOutputStream(outputPath + USER_FON_NAME)) {
            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            out.flush();
        } catch (Exception e) {
            Log.e("file", e.getMessage());
            throw new Exception("copy");
        }
    }

    public static void copyImg(Uri uri, ContentResolver resolver) throws Exception {
        String selectedImagePath = getPath(uri, resolver);
        if (fon) {
            deleteFile(USER_FON_PATH + USER_FON_NAME);
        }

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(selectedImagePath, options);
        int imageHeight = options.outHeight;
        int imageWidth = options.outWidth;
        int simpleSize = 2;
        if (imageHeight>1080||imageWidth>1080){
            int maxSize=imageHeight>imageWidth?imageHeight:imageWidth;
            while ((maxSize/simpleSize)>=1080){
                simpleSize*=2;
            }
            options.inSampleSize = simpleSize;
            options.inJustDecodeBounds = false;
            Bitmap bitmap =  BitmapFactory.decodeFile(selectedImagePath,options);
            File dir = new File(USER_FON_PATH);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            try(FileOutputStream outputStream = new FileOutputStream(USER_FON_PATH + USER_FON_NAME)) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);
            }catch (Exception e){
                throw new Exception("outputStream\n"+ e.getMessage());
            }
        }else {
            copyFile(selectedImagePath, USER_FON_PATH);
        }
    }

    public static String getPath(Uri uri, ContentResolver resolver) {
        String[] projection = {MediaStore.MediaColumns.DATA};
        Cursor cursor = resolver.query(uri, projection, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            String filePath = cursor.getString(columnIndex);
            cursor.close();
            return filePath;
        } else
            return uri.getPath();
    }
}
