package com.citrus.sdk.classes;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

/**
 * Created by salil on 24/4/15.
 */
public class Amount implements Parcelable {
    private final String value;
    private final String currency;

    /**
     * @param value Amount value
     */
    public Amount(String value) {
        this.value = value;
        this.currency = "INR";
    }

    public Amount(String value, String currency) {
        this.value = value;
        this.currency = currency;
    }

    public String getValue() {
        if (!TextUtils.isEmpty(value)) {
            return value.replaceFirst("^0+(?!$)", "");
        }

        return value;
    }

    public String getCurrency() {
        return currency;
    }

    /**
     * Returns the value in given format. If you want the value to be 1.00 pass format as #.00
     *
     * @param format
     * @return the amount in given format
     * @throws NumberFormatException if unable to convert the amount value to given format.
     */
    public String getValueAsFormattedDouble(String format) throws NumberFormatException {
        DecimalFormat df = new DecimalFormat(format);

        return df.format(getValueAsDouble());
    }

    /**
     * @return the amount value in integer.
     * @throws NumberFormatException if unable to convert the amount value to integer.
     */
    public double getValueAsDouble() throws NumberFormatException {
        double value = 0;

        if (!TextUtils.isEmpty(this.value)) {
            value = Double.parseDouble(this.value);
        }

        return value;
    }

    public static Amount fromJSON(String response) {
        Amount amount = null;
        JSONObject jsonObject = null;

        if (!TextUtils.isEmpty(response)) {
            try {
                jsonObject = new JSONObject(response);

                amount = fromJSONObject(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return amount;
    }

    public static Amount fromJSONObject(JSONObject amountObject) {
        Amount amount = null;

        if (amountObject != null) {
            String value = amountObject.optString("value");
            String currency = amountObject.optString("currency");

            if (!TextUtils.isEmpty(value) && !TextUtils.isEmpty(currency)) {
                amount = new Amount(value, currency);
            }
        }

        return amount;
    }

    public static String toJSON(Amount amount) {
        JSONObject billObject = toJSONObject(amount);
        if (billObject != null) {
            return billObject.toString();
        } else {
            return null;
        }
    }

    public static JSONObject toJSONObject(Amount amount) {
        JSONObject billObject = null;

        if (amount != null) {
            try {
                billObject = new JSONObject();
                billObject.put("value", amount.value);
                billObject.put("currency", amount.currency);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return billObject;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.value);
        dest.writeString(this.currency);
    }

    private Amount(Parcel in) {
        this.value = in.readString();
        this.currency = in.readString();
    }

    public static final Parcelable.Creator<Amount> CREATOR = new Parcelable.Creator<Amount>() {
        public Amount createFromParcel(Parcel source) {
            return new Amount(source);
        }

        public Amount[] newArray(int size) {
            return new Amount[size];
        }
    };

    @Override
    public String toString() {
        return "Amount{" +
                "value='" + value + '\'' +
                ", currency='" + currency + '\'' +
                '}';
    }
}
