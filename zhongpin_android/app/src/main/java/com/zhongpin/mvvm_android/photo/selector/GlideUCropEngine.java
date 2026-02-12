package com.zhongpin.mvvm_android.photo.selector;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.luck.picture.lib.engine.ImageEngine;
import com.luck.picture.lib.utils.ActivityCompatHelper;
import com.yalantis.ucrop.UCropImageEngine;
import com.zhilianshidai.pindan.app.R;

/**
 * @author：gubaojian
 * @date：2025-05-11 17:02
 * @describe：Glide加载引擎
 */
public class GlideUCropEngine implements UCropImageEngine {

    @Override
    public void loadImage(Context context, String url, ImageView imageView) {
        if (!ActivityCompatHelper.assertValidRequest(context)) {
            return;
        }
        Glide.with(context).load(url).override(180, 180).into(imageView);
    }

    @Override
    public void loadImage(Context context, Uri url, int maxWidth, int maxHeight, OnCallbackListener<Bitmap> call) {
        Glide.with(context).asBitmap().load(url).override(maxWidth, maxHeight).into(new CustomTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                if (call != null) {
                    call.onCall(resource);
                }
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {
                if (call != null) {
                    call.onCall(null);
                }
            }
        });
    }

    private GlideUCropEngine() {
    }

    private static final class InstanceHolder {
        static final GlideUCropEngine instance = new GlideUCropEngine();
    }

    public static GlideUCropEngine createGlideEngine() {
        return InstanceHolder.instance;
    }
}