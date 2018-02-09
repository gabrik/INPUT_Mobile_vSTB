package eu.input.cnit.ct.inputvstb.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.IOException;
import java.net.URL;

/**
 * Created by gabriele on 06/06/17.
 */

public class Utility {


    public static Bitmap getBitmapFromURL(String str_url){
        Bitmap image=null;
        try {
            URL url = new URL(str_url);
            image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
        } catch(IOException e) {
            Log.e(Utility.class.getCanonicalName(), "Error on obtaining logo");
            Log.e(Utility.class.getCanonicalName(), e.getMessage());
            e.printStackTrace();
            return null;
        }

        return image;
    }
}
