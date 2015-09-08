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
package com.citrus.card;


import com.citrus.mobile.CType;
import com.citrus.mobile.Month;
import com.citrus.mobile.Year;

/**
 * @deprecated in v3
 * <p/>
 * Use {@link com.citrus.sdk.payment.CreditCardOption} or {@link com.citrus.sdk.payment.DebitCardOption} instead depending upon type of the card.
 */
@Deprecated
public class Card {
    private String cardnumber;
    private String cardCVV;
    private String nameOnCard;
    private String cardType;
    private String expMonth;
    private String expYear;
    private String crdr;
    private String token;

    /**
     * @param cardNum
     * @param month
     * @param year
     * @param cvv
     * @param name
     * @param crdr
     * @deprecated use {@link #Card(String, com.citrus.mobile.Month, com.citrus.mobile.Year, String, String, com.citrus.mobile.CType)} ()} instead.
     */
    @Deprecated
    public Card(String cardNum, String month, String year, String cvv, String name, String crdr) {
        this.cardnumber = normalizeCardNumber(cardNum);
        this.cardCVV = cvv;
        if (!android.text.TextUtils.isEmpty(name)) {
            this.nameOnCard = name;
        } else {
            this.nameOnCard = "Name On Card";
        }

        this.expMonth = month;
        this.expYear = year;
        this.crdr = crdr;

        setCreditOrDebit();
    }

    public Card(String cardNum, Month month, Year year, String cvv, String name, CType cardType) {
        this.cardnumber = normalizeCardNumber(cardNum);
        this.cardCVV = cvv;

        if (!android.text.TextUtils.isEmpty(name)) {
            this.nameOnCard = name;
        } else {
            this.nameOnCard = "Name On Card";
        }

        this.expMonth = month.toString();
        this.expYear = year.toString();
        this.crdr = cardType.toString();

        setCreditOrDebit();
    }

    public Card(String token, String cvv) {
        this.token = token;
        this.cardCVV = cvv;
        this.cardnumber = null;
    }


    private void setCreditOrDebit() {

        if (getCardType() != null && "AMEX".equalsIgnoreCase(getCardType().toString())) {
            crdr = "credit";
        } else if (getCardType() != null && "MTRO".equalsIgnoreCase(getCardType().toString())) {
            crdr = "debit";
        }
    }

    public CardType getCardType() {
        return CardType.typeOf(cardnumber);
    }

    public String getCardNumber() {
        return this.cardnumber;
    }

    public String getCardHolderName() {
        return this.nameOnCard;
    }

    public String getCrdr() {
        return this.crdr;
    }

    public String getExpiryYear() {
        if (!android.text.TextUtils.isEmpty(expYear)) {
            if (expYear.length() > 2 && expYear.substring(0, 2).equalsIgnoreCase("20"))
                return String.valueOf(expYear);
            else
                return ("20" + String.valueOf(expYear));
        } else {
            return expYear;
        }
    }

    public String getExpiryMonth() {

        if (!android.text.TextUtils.isEmpty(expMonth)) {
            if (Integer.valueOf(expMonth) < 10) {
                return "0" + String.valueOf(Integer.valueOf(expMonth));
            }

            return String.valueOf(expMonth);
        } else {
            return expMonth;
        }
    }

    public String getCvvNumber() {
        return this.cardCVV;
    }

    public String getcardToken() {
        return this.token;
    }

    private String normalizeCardNumber(String number) {
        if (number == null) {
            return null;
        }
        return number.trim().replaceAll("\\s+|-", "");
    }

    public boolean validateCard() {

        CardType type = getCardType();
        String cardType = null;

        if (type != null) {
            cardType = type.toString();
        }

        if ("MTRO".equalsIgnoreCase(cardType)) {
            return validateNumber() && validateMaestroCtype();
        } else if (cardCVV == null) {
            return validateNumber() && validateExpiryDate();
        } else {
            return validateNumber() && validateExpiryDate() && validateCVC();
        }
    }

    public boolean validateMaestroCtype() {
        if (getCrdr() == null)
            return false;
        return getCrdr().equalsIgnoreCase(CType.DEBIT.toString()) ? true : false;//Maestro is debit card
    }

    public boolean validateNumber() {

        CardType type = getCardType();

        if (type == null)
            return false;

        cardType = type.toString();

        if (TextUtils.isBlank(cardnumber)) {
            return false;
        }

        String rawNumber = cardnumber.trim().replaceAll("\\s+|-", "");

        if (android.text.TextUtils.equals(cardType, "MTRO")) {
            return isValidLuhnNumber(rawNumber);
        }


        if (TextUtils.isBlank(rawNumber) || !TextUtils.isWholePositiveNumber(rawNumber)
                || !isValidLuhnNumber(rawNumber)) {
            return false;
        }

        if (!"AMEX".equals(cardType) && rawNumber.length() != 16) {
            return false;
        }

        if ("AMEX".equals(cardType) && rawNumber.length() != 15) {
            return false;
        }

        return true;
    }

    public boolean validateExpiryDate() {
        if (!validateExpMonth()) {
            return false;
        }
        if (!validateExpYear()) {
            return false;
        }
        return !DateUtils.hasMonthPassed(Integer.valueOf(expYear), Integer.valueOf(expMonth));
    }

    public boolean validateExpMonth() {
        if (expMonth == null) {
            return false;
        }
        return (Integer.valueOf(expMonth) >= 1 && Integer.valueOf(expMonth) <= 12);
    }

    public boolean validateExpYear() {
        if (expYear == null) {
            return false;
        }
        return !DateUtils.hasYearPassed(Integer.valueOf(expYear));
    }

    public boolean validateCVC() {
        if (TextUtils.isBlank(cardCVV)) {
            return false;
        }
        String cvcValue = cardCVV.trim();
        cardType = getCardType().toString();
        boolean validLength = ((cardType == null && cvcValue.length() >= 3 && cvcValue.length() <= 4)
                || ("AMEX".equals(cardType) && cvcValue.length() == 4) || (!"AMEX".equals(cardType) && cvcValue
                .length() == 3));

        if (!TextUtils.isWholePositiveNumber(cvcValue) || !validLength) {
            return false;
        }
        return true;
    }

    private boolean isValidLuhnNumber(String number) {
        boolean isOdd = true;
        int sum = 0;

        for (int index = number.length() - 1; index >= 0; index--) {
            char c = number.charAt(index);
            if (!Character.isDigit(c)) {
                return false;
            }
            int digitInteger = Integer.parseInt("" + c);
            isOdd = !isOdd;

            if (isOdd) {
                digitInteger *= 2;
            }

            if (digitInteger > 9) {
                digitInteger -= 9;
            }

            sum += digitInteger;
        }

        return sum % 10 == 0;
    }
}
