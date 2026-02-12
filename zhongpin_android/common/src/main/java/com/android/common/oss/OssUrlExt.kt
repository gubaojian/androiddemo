package com.android.common.oss

// https://help.aliyun.com/zh/oss/resize-images-4?spm=a2c4g.11186623.0.i2

fun String?.imgMaxWith(maxWidth:Int):String? {
    if (this == null) {
        return null;
    }
    if (indexOf('?') >= 0) {
        return this;
    }
    return "${this}?x-oss-process=image/resize,w_${maxWidth}"
}

fun String?.webpMaxWith(maxWidth:Int):String? {
    if (this == null) {
        return null;
    }
    if (indexOf('?') >= 0) {
        return this;
    }
    return "${this}?x-oss-process=image/resize,w_${maxWidth},format,webp"
}

fun String?.jpgMaxWith(maxWidth:Int):String? {
    if (this == null) {
        return null;
    }
    if (indexOf('?') >= 0) {
        return this;
    }
    return "${this}?x-oss-process=image/resize,w_${maxWidth},format,jpg"
}

fun String?.gifMaxWith(maxWidth:Int):String? {
    if (this == null) {
        return null;
    }
    if (indexOf('?') >= 0) {
        return this;
    }
    return "${this}?x-oss-process=image/resize,w_${maxWidth},format,gif"
}