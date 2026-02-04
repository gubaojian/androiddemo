package com.longbridge.core.multilingual;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.LocaleList;
import android.text.TextUtils;
import android.view.ContextThemeWrapper;

import androidx.annotation.NonNull;


import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class LanguageUtils {
    public final static int AUTO = 0;
    public final static int EN = 1;
    public final static int ZH_CN = 2;
    public final static int ZH_HK = 4;

    private static Locale systemCurrentLocal = Locale.US;
    private static Locale appCurrentLocal = Locale.US;
    public static Map<Object, OnSystemLanguageChangeListener> languageChangeListenerMap = new HashMap<>();

    public interface OnSystemLanguageChangeListener {
        void onLanguageChangeListener();
    }

    public interface LangPreference {
        final String AUTO = "auto";
        final String zhCN = "zh-CN";
        final String zhHK = "zh-HK";
        final String EN = "en";
    }

    public static void setLanguage(Application application, int type) {
        if (application == null) {
            return;
        }
//        LanguageSetting.setLanguage(type);
        //  RustEngine.Companion.getInstance().setLanguage(LanguageUtils.getCurLanguage());
        setAppLanguage(application);
    }

    public static String getLanguageByMap(Map<String, String> stringMap) {
        if (null == stringMap) {
            return "";
        }

        if (LanguageUtils.isCn() && stringMap.containsKey(LangPreference.zhCN)) {
            return stringMap.get(LangPreference.zhCN);
        }

        if (LanguageUtils.isEN() && stringMap.containsKey(LangPreference.EN)) {
            return stringMap.get(LangPreference.EN);
        }

        if (LanguageUtils.isHK() && stringMap.containsKey(LangPreference.zhHK)) {
            return stringMap.get(LangPreference.zhHK);
        }

        return stringMap.get(LangPreference.EN);
    }

    public static String getLanguageByJSONObject(JSONObject jsonObject) {
        if (null == jsonObject) {
            return "";
        }
        return "";

    }

    /**
     * 让lang在app内生效
     *
     * @param lang
     */
    public static void switchLanguage(Application application, String lang) {
        if (application == null) {
            return;
        }
        if (LangPreference.EN.equals(lang)) {
            LanguageUtils.setLanguage(application, LanguageUtils.EN);
        } else if (LangPreference.zhCN.equals(lang)) {
            LanguageUtils.setLanguage(application, LanguageUtils.ZH_CN);
        } else if (LangPreference.zhHK.equals(lang)) {
            LanguageUtils.setLanguage(application, LanguageUtils.ZH_HK);
        } else {
            LanguageUtils.setLanguage(application, LanguageUtils.AUTO);
        }
    }


    /**
     * 设置语种对象
     */
    public static void setLocale(Configuration config, Locale locale) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            LocaleList localeList = new LocaleList(locale);
            config.setLocales(localeList);
        } else {
            config.setLocale(locale);
        }
    }

    /**
     * 设置默认的语种环境（日期格式化会用到）
     */
    private static void setDefaultLocale(Resources resources) {
        Configuration configuration = resources.getConfiguration();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            LocaleList.setDefault(configuration.getLocales());
        } else {
            Locale.setDefault(configuration.locale);
        }
    }

    private static Context attachLanguages(Context ctx, Locale locale) {
        if (ctx == null) {
            return null;
        }
        Resources resources = ctx.getResources();
        Configuration config = new Configuration(resources.getConfiguration());
        setLocale(config, locale);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            ctx = ctx.createConfigurationContext(config);
        }
        resources.updateConfiguration(config, resources.getDisplayMetrics());
        return ctx;
    }

    /**
     * 在上下文的子类中重写 attachBaseContext 方法（用于更新 Context 的语种）
     */
    public static Context attach(Context context) {
        Locale locale = getBaseLocale();
        context = attachLanguages(context, locale);
        Configuration configuration = context.getResources().getConfiguration();
        //兼容appcompat 1.2.0后切换语言失效问题
        return null;

    }

    public static void updateAppLanguages(Resources resources) {
        updateLanguages(resources, getBaseLocale());
    }

    private static void updateLanguages(Resources resources, Locale locale) {
        if (getLocale(resources).equals(locale)) {
            return;
        }
        Configuration config = resources.getConfiguration();
        setLocale(config, locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }

    static Locale getLocale(Resources resources) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return resources.getConfiguration().getLocales().get(0);
        } else {
            return resources.getConfiguration().locale;
        }
    }

    public static void setAppLanguage(Context context) {
        Locale locale = getBaseLocale();
        updateLanguages(context.getResources(), locale);
        //在非application切换语言同时切换掉Application
        if (!(context instanceof Application)) {
            updateLanguages(context.getApplicationContext().getResources(), locale);
        }
        setDefaultLocale(context.getResources());
    }

    public static Locale getBaseLocale() {

        return Locale.TRADITIONAL_CHINESE;
    }

    public static String getCurLanguage() {
        return LangPreference.EN;
    }

    /**
     * 获取设置的语言。包括设置的 auto
     */
    public static String getSettingLanguage() {
        return LangPreference.EN;
    }

    /**
     * 获取当前期望语言
     */
    public static String getPreferLanguage() {

        return getCurLanguage();
    }

    public static boolean isTranslateToHK() {
        return LangPreference.zhHK.equals(getCurLanguage());
    }

    /**
     * app的语言环境
     *
     * @return
     */
    public static boolean isCn() {
        return LangPreference.zhCN.equals(getCurLanguage());
    }

    public static boolean isZh() {
        return LangPreference.zhCN.equals(getCurLanguage()) || isHK();
    }

    public static boolean isZh(String language) {
        return LangPreference.zhCN.equals(language) || LangPreference.zhHK.equals(language);
    }

    public static boolean isEN() {
        return LangPreference.EN.equals(getCurLanguage());
    }

    public static boolean isHK() {
        return LangPreference.zhHK.equals(getCurLanguage());
    }

    /**
     * 是否是简体中文
     */
    private static boolean isSystemZhCN() {
        Locale locale = getSystemLocale();
        return Locale.SIMPLIFIED_CHINESE.getLanguage().equals(locale.getLanguage()) && !isSystemHant();
    }

    private static boolean isSystemEN() {
        return Locale.ENGLISH.getLanguage().equals(getSystemLocale().getLanguage());
    }

    /**
     * 是否是繁体中文
     */
    private static boolean isSystemHant() {
        Locale locale = getSystemLocale();
        return Locale.TRADITIONAL_CHINESE.getLanguage().equals(locale.getLanguage()) && isHant(locale.getCountry(), locale.toLanguageTag(), locale.getScript());
    }

    //是否是繁体
    private static boolean isHant(String region, String languageTag, String script) {
        return "TW".equals(region) || "MO".equals(region) || "HK".equals(region) || TextUtils.equals("Hant", script) || TextUtils.equals("zh-Hant-CN", languageTag);
    }

    /**
     * 第一次进入app时保存系统选择语言
     */
    public static void saveSystemCurrentLanguage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            systemCurrentLocal = LocaleList.getDefault().get(0);
        } else {
            systemCurrentLocal = Locale.getDefault();
        }
    }

    /**
     * 用户在系统设置页面切换语言时再保存系统选择语言
     *
     * @param newConfig
     */
    public static void saveSystemCurrentLanguage(Application application, Configuration newConfig) {
        if (application == null || newConfig == null) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            systemCurrentLocal = newConfig.getLocales().get(0);
        } else {
            systemCurrentLocal = newConfig.locale;
        }
        if (!isDefaultProcess(application)) {
            return;
        }
        if (systemCurrentLocal == null || TextUtils.isEmpty(systemCurrentLocal.getLanguage())
                || appCurrentLocal == null || TextUtils.isEmpty(appCurrentLocal.getLanguage())) {
            return;
        }
        if (!systemCurrentLocal.getLanguage().equals(appCurrentLocal.getLanguage()) || !systemCurrentLocal.toLanguageTag().equals(appCurrentLocal.toLanguageTag())) {
            for (Object key : languageChangeListenerMap.keySet()) {
                OnSystemLanguageChangeListener listener = languageChangeListenerMap.get(key);
                if (listener != null) {
                    listener.onLanguageChangeListener();
                }
            }
        }
    }

    public static Locale getAppCurrentLocal() {
        if (appCurrentLocal == null) {
            return Locale.US;
        }
        return appCurrentLocal;
    }

    /**
     * 获取系统的locale
     *
     * @return
     */
    public static Locale getSystemLocale() {
        return systemCurrentLocal;
    }

    public static boolean isSettingCNLanguage() {
        return LangPreference.zhCN.equals(LanguageUtils.getCurLanguage()) || isHK();
    }

    /**
     * 获取极验用的语言设置
     *
     * @return
     */
    public static String getGeeCheckSettingLanguage() {
        if (LanguageUtils.isHK()) {
            return "zh-hk";
        } else if (LanguageUtils.isCn()) {
            return "zh";
        } else {
            return "en";
        }
    }

    public static void registerSystemLanguageChangeListener(Object o, OnSystemLanguageChangeListener changeListener) {
        if (languageChangeListenerMap.containsKey(o)) {
            return;
        }
        languageChangeListenerMap.put(o, changeListener);
    }

    public static void unRegisterSystemLanguageChangeListener(Object o) {
        if (o == null) {
            return;
        }
        languageChangeListenerMap.remove(o);
    }

    private static boolean isDefaultProcess(Application application) {
        if (application == null) {
            return false;
        }
        return false;
    }

    @NonNull
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static String getStringByLocal(Context context, int id, String locale) {
        Configuration configuration = new Configuration(context.getResources().getConfiguration());
        configuration.setLocale(new Locale(locale));
        return context.createConfigurationContext(configuration).getResources().getString(id);
    }
}

