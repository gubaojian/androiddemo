package com.zhongpin.mvvm_android.bugfix;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;

import com.github.gzuliyujiang.wheelpicker.contract.AddressParser;
import com.github.gzuliyujiang.wheelpicker.contract.AddressReceiver;
import com.github.gzuliyujiang.wheelpicker.entity.ProvinceEntity;
import com.github.gzuliyujiang.wheelpicker.impl.AssetAddressLoader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class AssetAddressLoaderV2 extends AssetAddressLoader {
    private final Context context;
    private final String path;

    public AssetAddressLoaderV2(@NonNull Context context, @NonNull String path) {
        super(context, path);
        this.context = context;
        this.path = path;
    }

    @Override
    public void loadJson(@NonNull AddressReceiver receiver, @NonNull AddressParser parser) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String text = loadFromAssets();
                final List<ProvinceEntity> data;
                if (TextUtils.isEmpty(text)) {
                    data = new ArrayList<>();
                } else {
                    data = parser.parseData(text);
                }
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        receiver.onAddressReceived(data);
                    }
                });
            }
        }).start();
    }

    @WorkerThread
    private String loadFromAssets() {
        StringBuilder stringBuilder = new StringBuilder();
        AssetManager am = context.getAssets();
        try (BufferedReader bf = new BufferedReader(new InputStreamReader(am.open(path)))) {
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            return "";
        }
        return stringBuilder.toString();
    }
}
