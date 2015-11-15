package com.kiewic.nanochat;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

/**
 * Created by gilberto on 11/14/15.
 */
public class BitmapConverter {
    public static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    public static String getBase64(Bitmap bitmap) {
        byte[] byteArray = getBytes(bitmap);
        String base64String = Base64.encodeToString(byteArray, Base64.DEFAULT);
        return base64String;
    }

    public static Bitmap getBitmap(byte[] byteArray) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        return bitmap;
    }

    public static Bitmap getBitmap(String base64String) {
        byte[] byteArray = Base64.decode(base64String, Base64.DEFAULT);
        Bitmap bitmap = getBitmap(byteArray);
        return bitmap;
    }
}
