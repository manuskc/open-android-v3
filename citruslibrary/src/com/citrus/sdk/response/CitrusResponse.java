package com.citrus.sdk.response;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by salil on 29/4/15.
 */
public class CitrusResponse implements Parcelable {
    public enum Status { SUCCESSFUL, FAILED, CANCELLED, PG_REJECTED }

    @SerializedName("reason")
    protected String message = null;
    @SerializedName("status")
    protected Status status = null;

    CitrusResponse() {}

    public CitrusResponse(String message, Status status) {
        this.message = message;
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public Status getStatus() {
        return status;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.message);
        dest.writeInt(this.status == null ? -1 : this.status.ordinal());
    }

    protected CitrusResponse(Parcel in) {
        this.message = in.readString();
        int tmpStatus = in.readInt();
        this.status = tmpStatus == -1 ? null : Status.values()[tmpStatus];
    }

}
