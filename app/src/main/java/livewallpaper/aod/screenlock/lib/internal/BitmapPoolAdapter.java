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

package livewallpaper.aod.screenlock.lib.internal;

import android.graphics.Bitmap;

/**
 * Created by amitshekhar on 17/06/16.
 */
public class BitmapPoolAdapter implements BitmapPool {
    @Override
    public int getMaxSize() {
        return 0;
    }

    @Override
    public void setSizeMultiplier(float sizeMultiplier) {
        // Do nothing.
    }

    @Override
    public void put(Bitmap bitmap) {
        bitmap.recycle();
    }

    @Override
    public Bitmap get(int width, int height, Bitmap.Config config) {
        return Bitmap.createBitmap(width, height, config);
    }

    @Override
    public Bitmap getDirty(int width, int height, Bitmap.Config config) {
        return get(width, height, config);
    }

    @Override
    public void clearMemory() {
        // Do nothing.
    }

    @Override
    public void trimMemory(int level) {
        // Do nothing.
    }
}