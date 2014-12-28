/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package lb.listviewvariants.utils;

import android.graphics.Bitmap;
import android.os.Build;
import android.support.v4.util.LruCache;
import android.util.Log;

import lb.listviewvariants.BuildConfig;

/**
 * This class holds our bitmap caches (memory and disk).
 */
public class ImageCache
  {
  private static final float CACHE_PERCENTAGE=0.1f;
  private static final String TAG="ImageCache";
  private LruCache<String,Bitmap> mMemoryCache;
  public static final ImageCache INSTANCE=new ImageCache(CACHE_PERCENTAGE);

  private ImageCache(float memCacheSizePercent)
    {
    init(memCacheSizePercent);
    }

  private void init(float memCacheSizePercent)
    {
    int memCacheSize=calculateMemCacheSize(memCacheSizePercent);
    // Set up memory cache
    if(BuildConfig.DEBUG)
      Log.d(TAG,"Memory cache created (size = "+memCacheSize+")");
    mMemoryCache=new LruCache<String,Bitmap>(memCacheSize)
    {
    @Override
    protected int sizeOf(String key,Bitmap bitmap)
      {
      final int bitmapSize=getBitmapSize(bitmap)/1024;
      return bitmapSize==0?1:bitmapSize;
      }
    };
    }

  public void addBitmapToCache(String data,Bitmap bitmap)
    {
    if(data==null||bitmap==null)
      return;
    // Add to memory cache
    if(mMemoryCache!=null&&mMemoryCache.get(data)==null)
      mMemoryCache.put(data,bitmap);
    }

  public Bitmap getBitmapFromMemCache(String data)
    {
    if(mMemoryCache!=null)
      {
      final Bitmap memBitmap=mMemoryCache.get(data);
      if(memBitmap!=null)
        {
        if(BuildConfig.DEBUG)
          Log.d(TAG,"Memory cache hit");
        return memBitmap;
        }
      }
    return null;
    }

  public static int getBitmapSize(Bitmap bitmap)
    {
    if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB_MR1)
      return bitmap.getByteCount();
    // Pre HC-MR1
    return bitmap.getRowBytes()*bitmap.getHeight();
    }

  public static int calculateMemCacheSize(float percent)
    {
    if(percent<0.05f||percent>0.8f)
      {
      throw new IllegalArgumentException("setMemCacheSizePercent - percent must be "
          +"between 0.05 and 0.8 (inclusive)");
      }
    return Math.round(percent*Runtime.getRuntime().maxMemory()/1024);
    }

  }
