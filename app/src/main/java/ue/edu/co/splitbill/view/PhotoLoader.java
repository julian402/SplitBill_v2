package ue.edu.co.splitbill.view;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;

// Carga la foto de un recibo reducida: la de la camara es enorme y llenaria la memoria
public final class PhotoLoader {

    private PhotoLoader() {
        //impide crear objetos de esta clase
    }

    // maxSize: lado mas largo, en pixeles, que se necesita mostrar
    public static Bitmap load(String path, int maxSize) {
        if (path == null || !new File(path).exists()) {
            return null;
        }
        // Primero solo se leen las medidas, sin cargar la imagen
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        // Se reduce a la mitad tantas veces como haga falta
        int sampleSize = 1;
        while (Math.max(options.outWidth, options.outHeight) / (sampleSize * 2) >= maxSize) {
            sampleSize *= 2;
        }
        options.inJustDecodeBounds = false;
        options.inSampleSize = sampleSize;
        return BitmapFactory.decodeFile(path, options);
    }
}
