package com.gs.open.temp;

import android.net.Uri;
import android.os.Parcel;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public final class LocationMessage extends MessageContent {
    double mLat;
    double mLng;
    String mPoi;
    String mBase64;
    Uri mImgUri;
    protected String extra;
    public static final Creator<LocationMessage> CREATOR = new Creator<LocationMessage>() {
        public LocationMessage createFromParcel(Parcel source) {
            return new LocationMessage(source);
        }

        public LocationMessage[] newArray(int size) {
            return new LocationMessage[size];
        }
    };

    public String getExtra() {
        return this.extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public byte[] encode() {
        JSONObject jsonObj = new JSONObject();

        try {
            if (!TextUtils.isEmpty(this.mBase64)) {
                jsonObj.put("content", this.mBase64);
            } else if (this.mImgUri != null) {
                jsonObj.put("content", this.mImgUri);
            }

            jsonObj.put("latitude", this.mLat);
            jsonObj.put("longitude", this.mLng);
            if (!TextUtils.isEmpty(this.getExtra())) {
                jsonObj.put("extra", this.getExtra());
            }

            if (!TextUtils.isEmpty(this.mPoi)) {
                jsonObj.put("poi", this.mPoi);
            }

            if (this.getJSONUserInfo() != null) {
                jsonObj.putOpt("user", this.getJSONUserInfo());
            }
        } catch (JSONException var3) {
            Log.e("JSONException", var3.getMessage());
        }

        this.mBase64 = null;
        return jsonObj.toString().getBytes();
    }

    public LocationMessage(byte[] data) {
        String jsonStr = new String(data);

        try {
            JSONObject jsonObj = new JSONObject(jsonStr);
            this.setLat(jsonObj.getDouble("latitude"));
            this.setLng(jsonObj.getDouble("longitude"));
            if (jsonObj.has("content")) {
                this.setBase64(jsonObj.optString("content"));
            }

            if (jsonObj.has("extra")) {
                this.setExtra(jsonObj.optString("extra"));
            }

            this.setPoi(jsonObj.optString("poi"));
            if (jsonObj.has("user")) {
                this.setUserInfo(this.parseJsonToUserInfo(jsonObj.getJSONObject("user")));
            }
        } catch (JSONException var4) {
            Log.e("JSONException", var4.getMessage());
        }

    }

    public static LocationMessage obtain(double lat, double lng, String poi, Uri imgUri) {
        return new LocationMessage(lat, lng, poi, imgUri);
    }

    private LocationMessage(double lat, double lng, String poi, Uri imgUri) {
        this.mLat = lat;
        this.mLng = lng;
        this.mPoi = poi;
        this.mImgUri = imgUri;
    }

    public double getLat() {
        return this.mLat;
    }

    public void setLat(double lat) {
        this.mLat = lat;
    }

    public double getLng() {
        return this.mLng;
    }

    public void setLng(double lng) {
        this.mLng = lng;
    }

    public String getPoi() {
        return this.mPoi;
    }

    public void setPoi(String poi) {
        this.mPoi = poi;
    }

    public String getBase64() {
        return this.mBase64;
    }

    public void setBase64(String base64) {
        this.mBase64 = base64;
    }

    public Uri getImgUri() {
        return this.mImgUri;
    }

    public void setImgUri(Uri imgUri) {
        this.mImgUri = imgUri;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        ParcelUtils.writeToParcel(dest, this.extra);
        ParcelUtils.writeToParcel(dest, this.mLat);
        ParcelUtils.writeToParcel(dest, this.mLng);
        ParcelUtils.writeToParcel(dest, this.mPoi);
        ParcelUtils.writeToParcel(dest, this.mImgUri);
        ParcelUtils.writeToParcel(dest, this.getUserInfo());
    }

    public LocationMessage(Parcel in) {
        this.extra = ParcelUtils.readFromParcel(in);
        this.mLat = ParcelUtils.readDoubleFromParcel(in);
        this.mLng = ParcelUtils.readDoubleFromParcel(in);
        this.mPoi = ParcelUtils.readFromParcel(in);
        this.mImgUri = (Uri)ParcelUtils.readFromParcel(in, Uri.class);
        this.setUserInfo((UserInfo)ParcelUtils.readFromParcel(in, UserInfo.class));
    }
}

