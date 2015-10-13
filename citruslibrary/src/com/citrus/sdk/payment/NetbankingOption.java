/*
   Copyright 2014 Citrus Payment Solutions Pvt. Ltd.
   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at
     http://www.apache.org/licenses/LICENSE-2.0
   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/

package com.citrus.sdk.payment;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import com.citrus.sdk.classes.PGHealth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by salil on 13/2/15.
 */
public final class NetbankingOption extends PaymentOption implements Parcelable {

    public static final Creator<NetbankingOption> CREATOR = new Creator<NetbankingOption>() {
        public NetbankingOption createFromParcel(Parcel source) {
            return new NetbankingOption(source);
        }

        public NetbankingOption[] newArray(int size) {
            return new NetbankingOption[size];
        }
    };
    private String bankName = null;
    private String bankCID = null;

    private NetbankingOption() {

    }

    public NetbankingOption(String bankName, String bankCID) {
        this.bankName = bankName;
        this.bankCID = bankCID;
    }

    /**
     * Use this constructor for tokenized payments.
     *
     * @param token Token of the bank
     */
    public NetbankingOption(String token) {
        super(null, token);
    }

    /**
     * @param name     - Human readable names for banks. e.g. Net Banking - AXIS BANK
     * @param token    - Token for netbanking payment.
     * @param bankName - Bank's name - ICICI, AXIS.
     */
    public NetbankingOption(String name, String token, String bankName) {
        super(name, token);
        this.bankName = bankName;
    }

    private NetbankingOption(Parcel in) {
        this.bankName = in.readString();
        this.bankCID = in.readString();
        this.name = in.readString();
        this.token = in.readString();
    }

    public String getBankName() {
        return bankName;
    }

    public String getBankCID() {
        return bankCID;
    }

    @Override
    public void setPgHealth(PGHealth pgHealth) {
        super.setPgHealth(pgHealth);
    }

    @Override
    public com.citrus.analytics.PaymentType getAnalyticsPaymentType() {
        com.citrus.analytics.PaymentType paymentType = com.citrus.analytics.PaymentType.NET_BANKING;
        paymentType.setName(bankName != null ? bankName : bankCID);
        return paymentType;
    }

    @Override
    public Drawable getOptionIcon(Context context) {
        // Return the icon depending upon the scheme of the card.
        Drawable drawable = null;

        int resourceId = 0;
        if ("AXIS Bank".equalsIgnoreCase(bankName)) {
            resourceId = context.getResources().getIdentifier("axis_bank", "drawable", context.getPackageName());
        }
        else if ("Andhra Bank".equalsIgnoreCase(bankName)) {
            resourceId = context.getResources().getIdentifier("andhra_bank", "drawable", context.getPackageName());
        }
        else if ("Bank of India".equalsIgnoreCase(bankName)) {
            resourceId = context.getResources().getIdentifier("bank_of_india", "drawable", context.getPackageName());
        } else if ("Bank Of Baroda".equalsIgnoreCase(bankName)) {
            resourceId = context.getResources().getIdentifier("bank_of_baroda", "drawable", context.getPackageName());
        } else if ("Bank of Maharashtra".equalsIgnoreCase(bankName)) {
            resourceId = context.getResources().getIdentifier("bank_of_maharashtra", "drawable", context.getPackageName());
        } else if ("Central Bank of India".equalsIgnoreCase(bankName)) {
            resourceId = context.getResources().getIdentifier("central_bank_of_india", "drawable", context.getPackageName());
        } else if ("Citibank".equalsIgnoreCase(bankName)) {
            resourceId = context.getResources().getIdentifier("citi_bank", "drawable", context.getPackageName());
        }
        else if ("CITI Bank".equalsIgnoreCase(bankName)) {
            resourceId = context.getResources().getIdentifier("citi_bank", "drawable", context.getPackageName());
        }
        else if ("Corporation Bank".equalsIgnoreCase(bankName)) {
            resourceId = context.getResources().getIdentifier("corporation_bank", "drawable", context.getPackageName());
        } else if ("City Union Bank".equalsIgnoreCase(bankName)) {
            resourceId = context.getResources().getIdentifier("city_union_bank", "drawable", context.getPackageName());
        } else if ("Canara Bank".equalsIgnoreCase(bankName)) {
            resourceId = context.getResources().getIdentifier("canara_bank", "drawable", context.getPackageName());
        }
        else if ("Cosmos Bank".equalsIgnoreCase(bankName)) {
            resourceId = context.getResources().getIdentifier("cosmos_bank", "drawable", context.getPackageName());
        }
        else if ("DEUTSCHE Bank".equalsIgnoreCase(bankName)) {
            resourceId = context.getResources().getIdentifier("deutsche_bank", "drawable", context.getPackageName());
        } else if ("Federal Bank".equalsIgnoreCase(bankName)) {
            resourceId = context.getResources().getIdentifier("federal_bank", "drawable", context.getPackageName());
        } else if ("HDFC Bank".equalsIgnoreCase(bankName)) {
            resourceId = context.getResources().getIdentifier("hdfc_bank", "drawable", context.getPackageName());
        } else if ("ICICI Bank".equalsIgnoreCase(bankName)) {
            resourceId = context.getResources().getIdentifier("icici_bank", "drawable", context.getPackageName());
        } else if ("IDBI Bank".equalsIgnoreCase(bankName)) {
            resourceId = context.getResources().getIdentifier("idbi_bank", "drawable", context.getPackageName());
        } else if ("Indian Bank".equalsIgnoreCase(bankName)) {
            resourceId = context.getResources().getIdentifier("indian_bank", "drawable", context.getPackageName());
        } else if ("Indian Overseas Bank".equalsIgnoreCase(bankName)) {
            resourceId = context.getResources().getIdentifier("indian_overseas_bank", "drawable", context.getPackageName());
        } else if ("IndusInd Bank".equalsIgnoreCase(bankName)) {
            resourceId = context.getResources().getIdentifier("indusind_bank", "drawable", context.getPackageName());
        } else if ("ING VYSA".equalsIgnoreCase(bankName)) {
            resourceId = context.getResources().getIdentifier("ing_vysa", "drawable", context.getPackageName());
        } else if ("Kotak Mahindra Bank".equalsIgnoreCase(bankName)) {
            resourceId = context.getResources().getIdentifier("kotak_mahindra_bank", "drawable", context.getPackageName());
        }
        else if ("ING Vysya Bank (now Kotak)".equalsIgnoreCase(bankName)) {
            resourceId = context.getResources().getIdentifier("kotak_mahindra_bank", "drawable", context.getPackageName());
        }
        else if ("Karur Vysya Bank".equalsIgnoreCase(bankName)) {
            resourceId = context.getResources().getIdentifier("karur_vyasa_bank", "drawable", context.getPackageName());
        }
        else if ("Karnataka Bank".equalsIgnoreCase(bankName)) {
            resourceId = context.getResources().getIdentifier("karnataka_bank", "drawable", context.getPackageName());
        } else if ("PNB Retail".equalsIgnoreCase(bankName)) {
            resourceId = context.getResources().getIdentifier("punjab_national", "drawable", context.getPackageName());
        } else if ("PNB Corporate".equalsIgnoreCase(bankName)) {
            resourceId = context.getResources().getIdentifier("punjab_national", "drawable", context.getPackageName());
        } else if ("SBI Bank".equalsIgnoreCase(bankName)) {
            resourceId = context.getResources().getIdentifier("sbi_bank", "drawable", context.getPackageName());
        } else if ("State Bank of Bikaner and Jaipur".equalsIgnoreCase(bankName)) {
            resourceId = context.getResources().getIdentifier("state_bank_of_bikaner_and_jaipur", "drawable", context.getPackageName());
        } else if ("State Bank of Hyderabad".equalsIgnoreCase(bankName)) {
            resourceId = context.getResources().getIdentifier("state_bank_of_hyderabad", "drawable", context.getPackageName());
        } else if ("State Bank of Mysore".equalsIgnoreCase(bankName)) {
            resourceId = context.getResources().getIdentifier("state_bank_of_mysore", "drawable", context.getPackageName());
        } else if ("State Bank of Travancore".equalsIgnoreCase(bankName)) {
            resourceId = context.getResources().getIdentifier("state_bank_of_travancore", "drawable", context.getPackageName());
        } else if ("State Bank of Patiala".equalsIgnoreCase(bankName)) {
            resourceId = context.getResources().getIdentifier("state_bank_of_patiala", "drawable", context.getPackageName());
        }
        else if ("South Indian Bank".equalsIgnoreCase(bankName)) {
            resourceId = context.getResources().getIdentifier("south_indian_bank", "drawable", context.getPackageName());
        }
        else if ("Union Bank Of India".equalsIgnoreCase(bankName)) {
            resourceId = context.getResources().getIdentifier("union_bank_of_india", "drawable", context.getPackageName());
        }
        else if ("Union Bank".equalsIgnoreCase(bankName)) {
            resourceId = context.getResources().getIdentifier("union_bank", "drawable", context.getPackageName());
        }
        else if ("UCO Bank".equalsIgnoreCase(bankName)) {
            resourceId = context.getResources().getIdentifier("uco", "drawable", context.getPackageName());
        }
        else if ("United Bank of India".equalsIgnoreCase(bankName)) {
            resourceId = context.getResources().getIdentifier("united_bank_of_india", "drawable", context.getPackageName());
        } else if ("Vijaya Bank".equalsIgnoreCase(bankName)) {
            resourceId = context.getResources().getIdentifier("vijaya_bank", "drawable", context.getPackageName());
        } else if ("YES Bank".equalsIgnoreCase(bankName)) {
            resourceId = context.getResources().getIdentifier("yes_bank", "drawable", context.getPackageName());
        } else {
            resourceId = context.getResources().getIdentifier("default_bank", "drawable", context.getPackageName());
        }

        if (resourceId == 0) {
            resourceId = context.getResources().getIdentifier("default_bank", "drawable", context.getPackageName());
        }

        if (resourceId != 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                drawable = context.getResources().getDrawable(resourceId, null);
            } else {
                drawable = context.getResources().getDrawable(resourceId);
            }
        }

        return drawable;
    }

    @Override
    public String getSavePaymentOptionObject() {
        JSONObject object = null;
        try {
            object = new JSONObject();
            JSONArray paymentOptions = new JSONArray();

            JSONObject option = new JSONObject();
            option.put("owner", "");
            option.put("bank", bankName);
            option.put("type", "netbanking");
            paymentOptions.put(option);

            object.put("paymentOptions", paymentOptions);
            object.put("type", "payment");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return object.toString();
    }

    @Override
    public String toString() {
        return bankName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.bankName);
        dest.writeString(this.bankCID);
        dest.writeString(this.name);
        dest.writeString(this.token);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NetbankingOption that = (NetbankingOption) o;

        return bankName.equals(that.bankName);

    }

    @Override
    public int hashCode() {
        return bankName.hashCode();
    }
}
