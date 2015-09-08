package com.citrus.sdk.ui.utils;

import android.os.Parcel;
import android.os.Parcelable;

import com.citrus.sdk.TransactionResponse;
import com.citrus.sdk.response.CitrusError;
import com.citrus.sdk.response.PaymentResponse;

/**
 * Created by akshaykoul on 6/12/15.
 */
public class ResultModel implements Parcelable {
    TransactionResponse transactionResponse;
    CitrusError error;
    PaymentResponse paymentResponse;
    boolean isWithdraw =false;
    public ResultModel(CitrusError error, TransactionResponse transactionResponse) {
        this.error = error;
        this.transactionResponse = transactionResponse;
    }

    public ResultModel(PaymentResponse paymentResponse,CitrusError error,boolean isWithdraw) {
        this.error = error;
        this.paymentResponse = paymentResponse;
        this.isWithdraw = isWithdraw;
    }

    public CitrusError getError() {
        return error;
    }

    public void setError(CitrusError error) {
        this.error = error;
    }

    public TransactionResponse getTransactionResponse() {
        return transactionResponse;
    }

    public void setTransactionResponse(TransactionResponse transactionResponse) {
        this.transactionResponse = transactionResponse;
    }

    public PaymentResponse getPaymentResponse() {
        return paymentResponse;
    }

    public void setPaymentResponse(PaymentResponse paymentResponse) {
        this.paymentResponse = paymentResponse;
    }

    public boolean isWithdraw() {
        return isWithdraw;
    }

    public void setIsWithdraw(boolean isWithdraw) {
        this.isWithdraw = isWithdraw;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.transactionResponse, 0);
        dest.writeParcelable(this.error, 0);
        dest.writeParcelable(this.paymentResponse, 0);
        dest.writeByte(isWithdraw ? (byte) 1 : (byte) 0);
    }

    protected ResultModel(Parcel in) {
        this.transactionResponse = in.readParcelable(TransactionResponse.class.getClassLoader());
        this.error = in.readParcelable(CitrusError.class.getClassLoader());
        this.paymentResponse = in.readParcelable(PaymentResponse.class.getClassLoader());
        this.isWithdraw = in.readByte() != 0;
    }

    public static final Creator<ResultModel> CREATOR = new Creator<ResultModel>() {
        public ResultModel createFromParcel(Parcel source) {
            return new ResultModel(source);
        }

        public ResultModel[] newArray(int size) {
            return new ResultModel[size];
        }
    };
}
