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
public interface BitmapPool {

    int getMaxSize();

    void setSizeMultiplier(float sizeMultiplier);

    void put(Bitmap bitmap);

    Bitmap get(int width, int height, Bitmap.Config config);

    Bitmap getDirty(int width, int height, Bitmap.Config config);

    void clearMemory();

    void trimMemory(int level);
}

