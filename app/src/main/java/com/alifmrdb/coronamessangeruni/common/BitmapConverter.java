package com.alifmrdb.coronamessangeruni.common;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;

public class BitmapConverter {

    public BitmapConverter(){ }

    public static Bitmap compressImage(Bitmap bitmapImage) {
        int nh = (int) ( bitmapImage.getHeight() * (512.0 / bitmapImage.getWidth()) );
        Bitmap scaled = Bitmap.createScaledBitmap(bitmapImage, 512, nh, true);
        return scaled;
    }
}
