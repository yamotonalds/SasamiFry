package com.cocofla.sasamifry.timeline;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.InputStream;
import java.util.Map;

/**
 * インターネット上の画像をImageViewに読み込む
 * Created by yamotonalds on 2014/07/18.
 */
public class ExternalImageLoader extends AsyncTask<String, Void, Bitmap> {
    private final ImageView bmImage;
    private String url;
    private final Map<String, Bitmap> imageCache;

    public ExternalImageLoader(ImageView bmImage, Map<String, Bitmap> imageCache) {
        this.bmImage = bmImage;
        this.imageCache = imageCache;
    }

    protected Bitmap doInBackground(String... urls) {
        this.url = urls[0];
        Log.d("loader", "load: " + this.url);
        Bitmap mIcon11 = null;
        try {
            InputStream in = new java.net.URL(this.url).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return mIcon11;
    }

    protected void onPostExecute(Bitmap result) {
        result = ExternalImageLoader.getCroppedBitmap(result);
        this.imageCache.put(this.url, result);
        this.bmImage.setImageBitmap(result);
    }

    private static Bitmap getCroppedBitmap(Bitmap bitmap) {

        int width  = bitmap.getWidth();
        int height = bitmap.getHeight();

        final Rect rect   = new Rect(0, 0, width, height);
        final RectF rectf = new RectF(0, 0, width, height);

        Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final Paint paint = new Paint();
        paint.setAntiAlias(true);

        canvas.drawRoundRect(rectf, width / 5, height / 5, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }
}
