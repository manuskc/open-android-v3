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

import android.os.Parcel;

import com.citrus.sdk.classes.Month;
import com.citrus.sdk.classes.Year;

/**
 * Created by salil on 13/2/15.
 */
public final class CreditCardOption extends CardOption implements android.os.Parcelable {

    public CreditCardOption() {
    }

    public CreditCardOption(String token, String cardCVV) {
        super(token, cardCVV);
    }


    /**
     * @param cardHolderName  - Name of the card holder.
     * @param cardNumber      - Card number.
     * @param cardCVV         - CVV of the card. We do not store CVV at our end.
     * @param cardExpiryMonth - Card Expiry Month 01 to 12 e.g. 01 for January.
     * @param cardExpiryYear  - Card Expiry Year in the form of YYYY e.g. 2015.
     */
    public CreditCardOption(String cardHolderName, String cardNumber, String cardCVV, Month cardExpiryMonth, Year cardExpiryYear) {
        super(cardHolderName, cardNumber, cardCVV, cardExpiryMonth, cardExpiryYear);
    }

    /**
     * @param cardNumber
     * @param cardScheme
     */
    public CreditCardOption(String cardNumber, CardScheme cardScheme) {
        super(cardNumber, cardScheme);
    }

    /**
     * This constructor will be used internally, mostly to display the saved card details.
     *
     * @param name           - User friendly name of the card. e.g. Debit Card (4242) or Credit Card (1234)
     * @param token          - Stored token for Card payment.
     * @param cardHolderName - Name of the card holder.
     * @param cardNumber     - Card number
     * @param cardScheme     - Card scheme e.g. VISA, MASTER etc.
     * @param cardExpiry     - Card expiry date. In MMYYYY format.
     */
    CreditCardOption(String name, String token, String cardHolderName, String cardNumber, CardScheme cardScheme, String cardExpiry) {
        super(name, token, cardHolderName, cardNumber, cardScheme, cardExpiry);
    }

    @Override
    /**
     * Returns the type of the card i.e. CREDIT OR DEBIT
     */
    public String getCardType() {
        // In case of Maestro card the card type is always debit, hence hardcoding the value.
        if (cardScheme == CardScheme.MAESTRO) {
            return CardType.DEBIT.getCardType();
        }

        return CardType.CREDIT.getCardType();
    }

    @Override
    public com.citrus.analytics.PaymentType getAnalyticsPaymentType() {
        return com.citrus.analytics.PaymentType.CREDIT_CARD;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.cardHolderName);
        dest.writeString(this.cardNumber);
        dest.writeString(this.cardCVV);
        dest.writeString(this.cardExpiry);
        dest.writeString(this.cardExpiryMonth);
        dest.writeString(this.cardExpiryYear);
        dest.writeSerializable(this.cardScheme);
        dest.writeString(this.name);
        dest.writeString(this.token);
        dest.writeByte(savePaymentOption ? (byte) 1 : (byte) 0);
    }

    private CreditCardOption(Parcel in) {
        this.cardHolderName = in.readString();
        this.cardNumber = in.readString();
        this.cardCVV = in.readString();
        this.cardExpiry = in.readString();
        this.cardExpiryMonth = in.readString();
        this.cardExpiryYear = in.readString();
        this.cardScheme = (CardScheme) in.readSerializable();
        this.name = in.readString();
        this.token = in.readString();
        this.savePaymentOption = in.readByte() != 0;
    }

    public static final Creator<CreditCardOption> CREATOR = new Creator<CreditCardOption>() {
        public CreditCardOption createFromParcel(Parcel source) {
            return new CreditCardOption(source);
        }

        public CreditCardOption[] newArray(int size) {
            return new CreditCardOption[size];
        }
    };
}
