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
package com.citrus.netbank;


/**
 * @deprecated in v3
 * <p/>
 * Use {@link com.citrus.sdk.payment.NetbankingOption} instead.
 */
@Deprecated
public class Bank {

    private String cidnumber;

    private String bankName;

    private BankPaymentType bankPaymentType = null;

    private String bankToken = null;


    public Bank(String cidnumber) {
        this.cidnumber = cidnumber;
    }

    public Bank(String bankName, String cidnumber) {
        this.bankName = bankName;
        this.cidnumber = cidnumber;
    }

    public Bank(String bankToken, BankPaymentType bankPaymentType) {
        this.bankToken = bankToken;
        this.bankPaymentType = bankPaymentType;
    }


    public String getBankName() {
        return this.bankName;
    }

    public String getCidnumber() {
        return this.cidnumber;
    }

    public String getBankToken() {
        return this.bankToken;
    }

    public BankPaymentType getPaymentType() {
        return bankPaymentType;
    }

}
