package com.citrus.sdk.classes;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by salil on 23/6/15.
 */
public class CashoutInfo implements Parcelable {

    private Amount amount = null;
    @SerializedName("account")
    private
    String accountNo = null;
    @SerializedName("owner")
    private
    String accountHolderName = null;
    @SerializedName("ifsc")
    private
    String ifscCode = null;

    /**
     * @param amount            Amount to be withdrawn.
     * @param accountNo         Account Number
     * @param accountHolderName Account Holder Name
     * @param ifscCode          IFSC Code
     */
    public CashoutInfo(@NonNull Amount amount, @NonNull String accountNo, @NonNull String accountHolderName, @NonNull String ifscCode) {
        this.amount = amount;
        this.accountNo = accountNo;
        this.accountHolderName = accountHolderName;
        this.ifscCode = ifscCode;
    }

    /**
     * This constructor is used internally for parsing purpose.
     *
     * @param accountNo
     * @param accountHolderName
     * @param ifscCode
     */
    private CashoutInfo(String accountNo, String accountHolderName, String ifscCode) {
        this.accountNo = accountNo;
        this.accountHolderName = accountHolderName;
        this.ifscCode = ifscCode;
    }

    public Amount getAmount() {
        return amount;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public String getAccountHolderName() {
        return accountHolderName;
    }

    public String getIfscCode() {
        return ifscCode;
    }

    public boolean validate() {
        return amount != null && !TextUtils.isEmpty(accountNo) && !TextUtils.isEmpty(accountHolderName) && !TextUtils.isEmpty(ifscCode);

    }

    public static CashoutInfo fromJSON(String json) {
        CashoutInfo cashoutInfo = null;

        if (json != null) {
            try {
                JSONObject cashoutObject = new JSONObject(json);
                JSONObject cashoutAccount = cashoutObject.getJSONObject("cashoutAccount");
                String accountNo = cashoutAccount.getString("number");
                String accountHolderName = cashoutAccount.getString("owner");
                String branch = cashoutAccount.getString("branch");

                cashoutInfo = new CashoutInfo(accountNo, accountHolderName, branch);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return cashoutInfo;
    }

    public static String toJSON(final CashoutInfo cashoutInfo) {
        String json = null;
        if (cashoutInfo != null && cashoutInfo.validate()) {
            try {
                JSONObject cashoutObject = new JSONObject();
                JSONObject cashoutAccount = new JSONObject();
                String accountNo = cashoutInfo.getAccountNo();
                String accountHolderName = cashoutInfo.getAccountHolderName();
                String branch = cashoutInfo.getIfscCode();

                cashoutAccount.put("number", accountNo);
                cashoutAccount.put("owner", accountHolderName);
                cashoutAccount.put("branch", branch);

                cashoutObject.put("cashoutAccount", cashoutAccount);
                cashoutObject.put("type", "prepaid");
                cashoutObject.put("currency", cashoutInfo.getAmount().getCurrency());

                json = cashoutObject.toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return json;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.amount, 0);
        dest.writeString(this.accountNo);
        dest.writeString(this.accountHolderName);
        dest.writeString(this.ifscCode);
    }

    private CashoutInfo(Parcel in) {
        this.amount = in.readParcelable(Amount.class.getClassLoader());
        this.accountNo = in.readString();
        this.accountHolderName = in.readString();
        this.ifscCode = in.readString();
    }

    public static final Parcelable.Creator<CashoutInfo> CREATOR = new Parcelable.Creator<CashoutInfo>() {
        public CashoutInfo createFromParcel(Parcel source) {
            return new CashoutInfo(source);
        }

        public CashoutInfo[] newArray(int size) {
            return new CashoutInfo[size];
        }
    };

    @Override
    public String toString() {
        return "CashoutInfo{" +
                "amount=" + amount +
                ", accountNo='" + accountNo + '\'' +
                ", accountHolderName='" + accountHolderName + '\'' +
                ", ifscCode='" + ifscCode + '\'' +
                '}';
    }
}
