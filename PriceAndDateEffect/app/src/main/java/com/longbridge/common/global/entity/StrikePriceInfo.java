package com.longbridge.common.global.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * <pre>
 * @author : wgh
 * desc   :
 * version:
 * </pre>
 */
public class StrikePriceInfo implements Parcelable {
    private String price;
    private String[] priceList;
    private String call_counter_id;
    private String put_counter_id;

    private int call_support_extend;
    private int put_support_extend;
    // todo 序列化
    private boolean isSameYear = false;

    public boolean isSameYear() {
        return isSameYear;
    }

    public void setSameYear(boolean sameYear) {
        this.isSameYear = sameYear;
    }


    public int getPut_support_extend() {
        return put_support_extend;
    }

    public void setPut_support_extend(int put_support_extend) {
        this.put_support_extend = put_support_extend;
    }

    public int getCall_support_extend() {
        return call_support_extend;
    }

    public boolean supportCallExtend() {
        return call_support_extend == 1;
    }

    public boolean supportPutExtend() {
        return put_support_extend == 1;
    }

    public void setCall_support_extend(int call_support_extend) {
        this.call_support_extend = call_support_extend;
    }

    private String standard_attr;//N==old  :   S

    public String[] getPriceList() {
        return priceList;
    }

    public void setPriceList(String[] priceList) {
        this.priceList = priceList;
    }

    public String getStandard_attr() {
        return standard_attr;
    }

    public void setStandard_attr(String standard_attr) {
        this.standard_attr = standard_attr;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCall_counter_id() {
        return call_counter_id;
    }

    public void setCall_counter_id(String call_counter_id) {
        this.call_counter_id = call_counter_id;
    }

    public String getPut_counter_id() {
        return put_counter_id;
    }

    public void setPut_counter_id(String put_counter_id) {
        this.put_counter_id = put_counter_id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.price);
        dest.writeStringArray(this.priceList);
        dest.writeString(this.call_counter_id);
        dest.writeString(this.put_counter_id);
        dest.writeString(this.standard_attr);
        dest.writeInt(this.call_support_extend);
        dest.writeInt(this.put_support_extend);
        dest.writeInt(this.isSameYear ? 1 : 0);
    }

    public void readFromParcel(Parcel source) {
        this.price = source.readString();
        this.priceList = source.createStringArray();
        this.call_counter_id = source.readString();
        this.put_counter_id = source.readString();
        this.standard_attr = source.readString();
        this.call_support_extend = source.readInt();
        this.put_support_extend = source.readInt();
        this.isSameYear = source.readInt() == 1;
    }

    public StrikePriceInfo() {
    }

    protected StrikePriceInfo(Parcel in) {
        this.price = in.readString();
        this.priceList = in.createStringArray();
        this.call_counter_id = in.readString();
        this.put_counter_id = in.readString();
        this.standard_attr = in.readString();
        this.call_support_extend = in.readInt();
        this.put_support_extend = in.readInt();
        this.isSameYear = in.readInt() == 1;
    }

    public static final Creator<StrikePriceInfo> CREATOR = new Creator<StrikePriceInfo>() {
        @Override
        public StrikePriceInfo createFromParcel(Parcel source) {
            return new StrikePriceInfo(source);
        }

        @Override
        public StrikePriceInfo[] newArray(int size) {
            return new StrikePriceInfo[size];
        }
    };
}
