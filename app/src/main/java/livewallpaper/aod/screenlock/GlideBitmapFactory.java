/*
 *    Copyright (C) 2016 Amit Shekhar
 *    Copyright (C) 2011 Android Open Source Project
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package livewallpaper.aod.screenlock;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Build;
import android.util.TypedValue;


import java.io.FileDescriptor;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

import livewallpaper.aod.screenlock.internal.Util;

/**
 * Created by amitshekhar on 18/06/16.
 */
public class GlideBitmapFactory {

    public static Bitmap decodeFile(String pathName) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathName, options);
        options.inSampleSize = 1;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
            options.inMutable = true;
            Bitmap inBitmap = GlideBitmapPool.getBitmap(options.outWidth, options.outHeight, options.inPreferredConfig);
            if (inBitmap != null && Util.canUseForInBitmap(inBitmap, options)) {
                options.inBitmap = inBitmap;
            }
        }
        options.inJustDecodeBounds = false;
        try {
            return BitmapFactory.decodeFile(pathName, options);
        } catch (Exception e) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
                options.inBitmap = null;
            }
            return BitmapFactory.decodeFile(pathName, options);
        }
    }

    public static Bitmap decodeFile(String pathName, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathName, options);
        options.inSampleSize = Util.calculateInSampleSize(options, reqWidth, reqHeight);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
            options.inMutable = true;
            Bitmap inBitmap = GlideBitmapPool.getBitmap(options.outWidth, options.outHeight, options.inPreferredConfig);
            if (inBitmap != null && Util.canUseForInBitmap(inBitmap, options)) {
                options.inBitmap = inBitmap;
            }
        }
        options.inJustDecodeBounds = false;
        try {
            return BitmapFactory.decodeFile(pathName, options);
        } catch (Exception e) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
                options.inBitmap = null;
            }
            return BitmapFactory.decodeFile(pathName, options);
        }
    }
//
//    public static Bitmap decodeResource(Resources res, int id) {
//        final BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inJustDecodeBounds = true;
//        BitmapFactory.decodeResource(res, id, options);
//        options.inSampleSize = 1;
//        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
//            options.inMutable = true;
//            Bitmap inBitmap = GlideBitmapPool.getBitmap(options.outWidth, options.outHeight, options.inPreferredConfig);
//            if (inBitmap != null && Util.canUseForInBitmap(inBitmap, options)) {
//                options.inBitmap = inBitmap;
//            }
//        }
//        options.inJustDecodeBounds = false;
//        try {
//            return BitmapFactory.decodeResource(res, id, options);
//        } catch (Exception e) {
//            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
//                options.inBitmap = null;
//            }
//            return BitmapFactory.decodeResource(res, id, options);
//        }
//    }

    public static Bitmap decodeResource(final Resources res, final int id) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        // Get image dimensions without decoding the full bitmap
        BitmapFactory.decodeResource(res, id, options);

        options.inSampleSize = calculateInSampleSize(options, 1080, 1920); // Adjust to your target dimensions

        options.inMutable = true;

        // Reuse existing Bitmap if possible
        Bitmap inBitmap = GlideBitmapPool.getBitmap(options.outWidth, options.outHeight, options.inPreferredConfig);
        if (inBitmap != null && Util.canUseForInBitmap(inBitmap, options)) {
            options.inBitmap = inBitmap;
        }

        options.inJustDecodeBounds = false;

        try {
            // Decode bitmap in the background
            return decodeBitmapInBackground(res, id, options);
        } catch (Exception e) {
            // Handle fallback in case of failure
            options.inBitmap = null;
            return decodeBitmapInBackground(res, id, options);
        }
    }

    private static Bitmap decodeBitmapInBackground(Resources res, int id, BitmapFactory.Options options) {
        try {
            return Executors.newSingleThreadExecutor().submit(() -> BitmapFactory.decodeResource(res, id, options)).get();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    // Calculate the appropriate inSampleSize
    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
    public static Bitmap decodeResource(Resources res, int id, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, id, options);
        options.inSampleSize = Util.calculateInSampleSize(options, reqWidth, reqHeight);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
            options.inMutable = true;
            Bitmap inBitmap = GlideBitmapPool.getBitmap(options.outWidth, options.outHeight, options.inPreferredConfig);
            if (inBitmap != null && Util.canUseForInBitmap(inBitmap, options)) {
                options.inBitmap = inBitmap;
            }
        }
        options.inJustDecodeBounds = false;
        try {
            return BitmapFactory.decodeResource(res, id, options);
        } catch (Exception e) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
                options.inBitmap = null;
            }
            return BitmapFactory.decodeResource(res, id, options);
        }
    }

    public static Bitmap decodeByteArray(byte[] data, int offset, int length) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data, offset, length, options);
        options.inSampleSize = 1;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
            options.inMutable = true;
            Bitmap inBitmap = GlideBitmapPool.getBitmap(options.outWidth, options.outHeight, options.inPreferredConfig);
            if (inBitmap != null && Util.canUseForInBitmap(inBitmap, options)) {
                options.inBitmap = inBitmap;
            }
        }
        options.inJustDecodeBounds = false;
        try {
            return BitmapFactory.decodeByteArray(data, offset, length, options);
        } catch (Exception e) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
                options.inBitmap = null;
            }
            return BitmapFactory.decodeByteArray(data, offset, length, options);
        }
    }

    public static Bitmap decodeByteArray(byte[] data, int offset, int length, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data, offset, length, options);
        options.inSampleSize = Util.calculateInSampleSize(options, reqWidth, reqHeight);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
            options.inMutable = true;
            Bitmap inBitmap = GlideBitmapPool.getBitmap(options.outWidth, options.outHeight, options.inPreferredConfig);
            if (inBitmap != null && Util.canUseForInBitmap(inBitmap, options)) {
                options.inBitmap = inBitmap;
            }
        }
        options.inJustDecodeBounds = false;
        try {
            return BitmapFactory.decodeByteArray(data, offset, length, options);
        } catch (Exception e) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
                options.inBitmap = null;
            }
            return BitmapFactory.decodeByteArray(data, offset, length, options);
        }
    }

    public static Bitmap decodeResourceStream(Resources res, TypedValue value, InputStream is, Rect pad) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResourceStream(res, value, is, pad, options);
        options.inSampleSize = 1;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
            options.inMutable = true;
            Bitmap inBitmap = GlideBitmapPool.getBitmap(options.outWidth, options.outHeight, options.inPreferredConfig);
            if (inBitmap != null && Util.canUseForInBitmap(inBitmap, options)) {
                options.inBitmap = inBitmap;
            }
        }
        options.inJustDecodeBounds = false;
        try {
            return BitmapFactory.decodeResourceStream(res, value, is, pad, options);
        } catch (Exception e) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
                options.inBitmap = null;
            }
            return BitmapFactory.decodeResourceStream(res, value, is, pad, options);
        }
    }

    public static Bitmap decodeResourceStream(Resources res, TypedValue value, InputStream is, Rect pad, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResourceStream(res, value, is, pad, options);
        options.inSampleSize = Util.calculateInSampleSize(options, reqWidth, reqHeight);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
            options.inMutable = true;
            Bitmap inBitmap = GlideBitmapPool.getBitmap(options.outWidth, options.outHeight, options.inPreferredConfig);
            if (inBitmap != null && Util.canUseForInBitmap(inBitmap, options)) {
                options.inBitmap = inBitmap;
            }
        }
        options.inJustDecodeBounds = false;
        try {
            return BitmapFactory.decodeResourceStream(res, value, is, pad, options);
        } catch (Exception e) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
                options.inBitmap = null;
            }
            return BitmapFactory.decodeResourceStream(res, value, is, pad, options);
        }
    }

    public static Bitmap decodeStream(InputStream is) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(is, null, options);
        options.inSampleSize = 1;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
            options.inMutable = true;
            Bitmap inBitmap = GlideBitmapPool.getBitmap(options.outWidth, options.outHeight, options.inPreferredConfig);
            if (inBitmap != null && Util.canUseForInBitmap(inBitmap, options)) {
                options.inBitmap = inBitmap;
            }
        }
        options.inJustDecodeBounds = false;
        try {
            return BitmapFactory.decodeStream(is, null, options);
        } catch (Exception e) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
                options.inBitmap = null;
            }
            return BitmapFactory.decodeStream(is, null, options);
        }
    }

    public static Bitmap decodeStream(InputStream is, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(is, null, options);
        options.inSampleSize = Util.calculateInSampleSize(options, reqWidth, reqHeight);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
            options.inMutable = true;
            Bitmap inBitmap = GlideBitmapPool.getBitmap(options.outWidth, options.outHeight, options.inPreferredConfig);
            if (inBitmap != null && Util.canUseForInBitmap(inBitmap, options)) {
                options.inBitmap = inBitmap;
            }
        }
        options.inJustDecodeBounds = false;
        try {
            return BitmapFactory.decodeStream(is, null, options);
        } catch (Exception e) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
                options.inBitmap = null;
            }
            return BitmapFactory.decodeStream(is, null, options);
        }
    }

    public static Bitmap decodeStream(InputStream is, Rect outPadding) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(is, outPadding, options);
        options.inSampleSize = 1;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
            options.inMutable = true;
            Bitmap inBitmap = GlideBitmapPool.getBitmap(options.outWidth, options.outHeight, options.inPreferredConfig);
            if (inBitmap != null && Util.canUseForInBitmap(inBitmap, options)) {
                options.inBitmap = inBitmap;
            }
        }
        options.inJustDecodeBounds = false;
        try {
            return BitmapFactory.decodeStream(is, outPadding, options);
        } catch (Exception e) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
                options.inBitmap = null;
            }
            return BitmapFactory.decodeStream(is, outPadding, options);
        }
    }

    public static Bitmap decodeStream(InputStream is, Rect outPadding, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(is, outPadding, options);
        options.inSampleSize = Util.calculateInSampleSize(options, reqWidth, reqHeight);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
            options.inMutable = true;
            Bitmap inBitmap = GlideBitmapPool.getBitmap(options.outWidth, options.outHeight, options.inPreferredConfig);
            if (inBitmap != null && Util.canUseForInBitmap(inBitmap, options)) {
                options.inBitmap = inBitmap;
            }
        }
        options.inJustDecodeBounds = false;
        try {
            return BitmapFactory.decodeStream(is, outPadding, options);
        } catch (Exception e) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
                options.inBitmap = null;
            }
            return BitmapFactory.decodeStream(is, outPadding, options);
        }
    }

    public static Bitmap decodeFileDescriptor(FileDescriptor fd) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFileDescriptor(fd, null, options);
        options.inSampleSize = 1;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
            options.inMutable = true;
            Bitmap inBitmap = GlideBitmapPool.getBitmap(options.outWidth, options.outHeight, options.inPreferredConfig);
            if (inBitmap != null && Util.canUseForInBitmap(inBitmap, options)) {
                options.inBitmap = inBitmap;
            }
        }
        options.inJustDecodeBounds = false;
        try {
            return BitmapFactory.decodeFileDescriptor(fd, null, options);
        } catch (Exception e) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
                options.inBitmap = null;
            }
            return BitmapFactory.decodeFileDescriptor(fd, null, options);
        }
    }

    public static Bitmap decodeFileDescriptor(FileDescriptor fd, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFileDescriptor(fd, null, options);
        options.inSampleSize = Util.calculateInSampleSize(options, reqWidth, reqHeight);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
            options.inMutable = true;
            Bitmap inBitmap = GlideBitmapPool.getBitmap(options.outWidth, options.outHeight, options.inPreferredConfig);
            if (inBitmap != null && Util.canUseForInBitmap(inBitmap, options)) {
                options.inBitmap = inBitmap;
            }
        }
        options.inJustDecodeBounds = false;
        try {
            return BitmapFactory.decodeFileDescriptor(fd, null, options);
        } catch (Exception e) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
                options.inBitmap = null;
            }
            return BitmapFactory.decodeFileDescriptor(fd, null, options);
        }
    }

    public static Bitmap decodeFileDescriptor(FileDescriptor fd, Rect outPadding) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFileDescriptor(fd, outPadding, options);
        options.inSampleSize = 1;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
            options.inMutable = true;
            Bitmap inBitmap = GlideBitmapPool.getBitmap(options.outWidth, options.outHeight, options.inPreferredConfig);
            if (inBitmap != null && Util.canUseForInBitmap(inBitmap, options)) {
                options.inBitmap = inBitmap;
            }
        }
        options.inJustDecodeBounds = false;
        try {
            return BitmapFactory.decodeFileDescriptor(fd, outPadding, options);
        } catch (Exception e) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
                options.inBitmap = null;
            }
            return BitmapFactory.decodeFileDescriptor(fd, outPadding, options);
        }
    }

    public static Bitmap decodeFileDescriptor(FileDescriptor fd, Rect outPadding, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFileDescriptor(fd, outPadding, options);
        options.inSampleSize = Util.calculateInSampleSize(options, reqWidth, reqHeight);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
            options.inMutable = true;
            Bitmap inBitmap = GlideBitmapPool.getBitmap(options.outWidth, options.outHeight, options.inPreferredConfig);
            if (inBitmap != null && Util.canUseForInBitmap(inBitmap, options)) {
                options.inBitmap = inBitmap;
            }
        }
        options.inJustDecodeBounds = false;
        try {
            return BitmapFactory.decodeFileDescriptor(fd, outPadding, options);
        } catch (Exception e) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
                options.inBitmap = null;
            }
            return BitmapFactory.decodeFileDescriptor(fd, outPadding, options);
        }
    }


}