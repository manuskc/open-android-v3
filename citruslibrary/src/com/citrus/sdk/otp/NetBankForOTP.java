package com.citrus.sdk.otp;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;

/**
 * This class will contain the JS related to sendOTP, enterPassword and processTransaction for every bank.
 * <p/>
 * Created by salil on 21/10/15.
 */
public enum NetBankForOTP {

    SBI {
        @Override
        public String getTransactionJS() {
            return "javascript: document.getElementsByName('submits')[0].click();";
        }

        @Override
        public String getEnterPasswordJS() {
            return ""; // This js is not required, since there is no facility to enter password.
        }

        @Override
        public String getSendOTPJS() {
            return ""; // This js is not required, since auto otp is triggered automatically.
        }

        @Override
        public String getReSendOTPJS() {
            return "javascript:resendOTP();";
        }

        @Override
        public String getSetOTPJS(String otp) {
            return "javascript:document.getElementById('otp').setAttribute('value','" + otp + "');";
        }

        @Override
        public String getBankNameForParsing() {
            return "SBI";
        }

        @Override
        public String getBankIconName() {
            return "sbi_bank";
        }

        @Override
        public String getBankName() {
            return "SBI BANK";
        }

        @Override
        public Drawable getBankIconDrawable(Context context) {
            return getBankIcon(context, getBankIconName());
        }

        @Override
        public boolean isBypassSendOTPButton() {
            return true;
        }

        @Override
        public boolean isBypassEnterPasswordButton() {
            return true;
        }
    }, ICICI_CREDIT {
        @Override
        public String getTransactionJS() {
            return "javascript: document.frmPayerAuth.submit();";
        }

        @Override
        public String getEnterPasswordJS() {
            return "javascript:";
        }

        @Override
        public boolean isBypassEnterPasswordButton() {
            return true;
        }

        @Override
        public String getSendOTPJS() {
            return "javascript:" +
                    "var otpChannels = document.getElementsByName('otpDestinationOption');" + // Select the OTP Channel as mobile.
                    "otpChannels[0].checked = true;" +
                    "document.getElementsByTagName('form')[0].submit();"; // Submit the page.
        }

        @Override
        public String getReSendOTPJS() {
            return "javascript:resend_otp()";
        }

        @Override
        public String getSetOTPJS(String otp) {
            return "javascript:" +
                    "var txtOTP = document.getElementsByName('txtAutoOtp');" +
                    "txtOTP[0].setAttribute('value','" + otp + "');";
        }

        @Override
        public String getBankNameForParsing() {
            return "ICICIB";
        }

        @Override
        public String getBankIconName() {
            return "icici_bank";
        }

        @Override
        public String getBankName() {
            return "ICICI BANK";
        }

        @Override
        public Drawable getBankIconDrawable(Context context) {
            return getBankIcon(context, getBankIconName());
        }
    }, ICICI_DEBIT {
        @Override
        public String getTransactionJS() {
            return "javascript:submitPassword();";
        }

        @Override
        public String getEnterPasswordJS() {
            return "javascript:" +
                    "var txtPassword=document.getElementById('txtPassword');" +
                    "txtPassword.focus(); txtPassword.scrollIntoView();";
        }

        @Override
        public String getSendOTPJS() {
            return "javascript:showChannelSelectPage();" + // Show OTP Channel Page.
                    "var otpChannels = document.getElementsByName('otpDestinationOption');" + // Select the OTP Channel as mobile.
                    "otpChannels[0].checked = true;" +
                    "pwdBaseOtpChannelSelected(1);"; // Submit the page.
        }

        @Override
        public String getReSendOTPJS() {
            return "javascript:resendOTP();";
        }

        @Override
        public String getSetOTPJS(String otp) {
            return "javascript:" +
                    "var txtOTP = document.getElementsByName('otpPassword'); \n" +
                    "txtOTP[0].setAttribute('value','" + otp + "');";
        }

        @Override
        public String getBankNameForParsing() {
            return "ICICIB";
        }

        @Override
        public String getBankIconName() {
            return "icici_bank";
        }

        @Override
        public String getBankName() {
            return "ICICI BANK";
        }

        @Override
        public Drawable getBankIconDrawable(Context context) {
            return getBankIcon(context, getBankIconName());
        }
    }, HDFC {
        @Override
        public String getTransactionJS() {
            return "javascript:document.frmDynamicAuth.submit();";
        }

        @Override
        public boolean isMultipartEnterPasswordJS() {
            return true;
        }

        @Override
        public String getMultiPartEnterPasswordJS() {
            return "javascript:" +
                    "var txtPassword=document.getElementById('txtPassword');" +
                    "txtPassword.focus(); txtPassword.scrollIntoView();";
        }

        @Override
        public String getEnterPasswordJS() {
            return "javascript: " +
                    "var radioButtons = document.getElementsByName('acsRadio');" +
                    "radioButtons[0].checked = true;" +
                    "selectOption();";
        }

        @Override
        public boolean isMultipartSendOTPJS() {
            return true;
        }

        @Override
        public String getSendOTPJS() {
            return "javascript: " +
                    "var radioButtons = document.getElementsByName('acsRadio');" +
                    "radioButtons[1].checked = true;" +
                    "selectOption();";
        }

        @Override
        public String getSetOTPJS(String otp) {
            return "javascript:document.getElementsByName('txtOtpPassword')[0].setAttribute('value','" + otp + "');";
        }

        @Override
        public String getMultiPartSendOTPJS() {
            return "javascript:generateOTP();";
        }

        @Override
        public String getReSendOTPJS() {
            return "javascript:generateOTP();";
        }

        @Override
        public String getBankNameForParsing() {
            return "HDFCBK";
        }

        @Override
        public String getBankIconName() {
            return "hdfc_bank";
        }

        @Override
        public String getBankName() {
            return "HDFC BANK";
        }

        @Override
        public Drawable getBankIconDrawable(Context context) {
            return getBankIcon(context, getBankIconName());
        }

    }, KOTAK_DEBIT {
        @Override
        public String getTransactionJS() {
            return "javascript: " +
                    "var forms = document.getElementsByTagName('form');" +
                    "forms[forms.length - 1].submit();";
        }

        @Override
        public String getEnterPasswordJS() {
            return "javascript:"; // In case of debit card, otp is triggered directly.
        }

        @Override
        public String getSendOTPJS() {
            return "javascript:"; // In case of debit card, otp is triggered directly so explicitly call is not required.
        }

        @Override
        public String getSetOTPJS(String otp) {
            return "javascript:document.getElementById('txtOtp').setAttribute('value','" + otp + "');";
        }

        @Override
        public String getReSendOTPJS() {
            return "javascript:reSendOtp();";
        }

        @Override
        public String getBankNameForParsing() {
            return "KOTAKB";
        }

        @Override
        public String getBankIconName() {
            return "kotak_mahindra_bank";
        }

        @Override
        public String getBankName() {
            return "KOTAK MAHINDRA BANK";
        }

        @Override
        public Drawable getBankIconDrawable(Context context) {
            return getBankIcon(context, getBankIconName());
        }

        @Override
        public boolean isBypassEnterPasswordButton() {
            return true;
        }

        @Override
        public boolean isBypassSendOTPButton() {
            return true;
        }
    }, KOTAK_CREDIT {
        @Override
        public String getTransactionJS() {
            return "javascript: document.getElementById('cmdSubmit').click();";
        }

        @Override
        public String getEnterPasswordJS() {
            return "javascript: document.getElementsByName('authenticationOption')[1].click();" +
                    "var txtPassword = document.getElementById('txtPassword');" +
                    "txtPassword.focus(); txtPassword.scrollIntoView();";
        }

        @Override
        public String getSendOTPJS() {
            return "javascript: document.getElementsByName('authenticationOption')[0].click();";
        }

        @Override
        public String getSetOTPJS(String otp) {
            return "javascript: document.getElementById('otpValue').setAttribute('value','" + otp + "');";
        }

        @Override
        public String getReSendOTPJS() {
            return "javascript:reSendOtp();";
        }

        @Override
        public String getBankNameForParsing() {
            return "KOTAKB";
        }

        @Override
        public String getBankIconName() {
            return "kotak_mahindra_bank";
        }

        @Override
        public String getBankName() {
            return "KOTAK MAHINDRA BANK";
        }

        @Override
        public Drawable getBankIconDrawable(Context context) {
            return getBankIcon(context, getBankIconName());
        }
    }, CITI {
        @Override
        public String getTransactionJS() {
            return "javascript: validateOTP(1);";
        }

        @Override
        public String getEnterPasswordJS() {
            return "javascript:document.getElementById('uid_tb_r').checked=true;" +
                    " showdiv('uid_tb');" +
                    " setTimeout( function() { " +
                    "       var txtPassword = document.getElementsByName('useridanswer')[0];" +
                    "       txtPassword.focus(); txtPassword.scrollIntoView(); " +
                    " }, 300);" +
                    "";
        }

        @Override
        public String getSendOTPJS() {
            return "javascript:document.getElementById('otp_tb_r').checked=true; OnSubmitHandler1();";
        }

        @Override
        public String getReSendOTPJS() {
            return "javascript:";
        }

        @Override
        public String getBankNameForParsing() {
            return "CITIBK";
        }

        @Override
        public String getSetOTPJS(String otp) {
            return "javascript:document.getElementsByName('otp')[0].setAttribute('value','" + otp + "');";
        }

        @Override
        public boolean isBypassEnterPasswordButton() {
            return false;
        }

        @Override
        public boolean isBypassSendOTPButton() {
            return false;
        }

        @Override
        public String getBankIconName() {
            return "citi_bank";
        }

        @Override
        public String getBankName() {
            return "CITI BANK";
        }

        @Override
        public Drawable getBankIconDrawable(Context context) {
            return getBankIcon(context, getBankIconName());
        }
    }, UNKNOWN {
        @Override
        public String getTransactionJS() {
            return "";
        }

        @Override
        public String getEnterPasswordJS() {
            return "";
        }

        @Override
        public String getSendOTPJS() {
            return "";
        }

        @Override
        public String getReSendOTPJS() {
            return "";
        }

        @Override
        public int getOTPLength() {
            return 0;
        }

        @Override
        public String getBankNameForParsing() {
            return "";
        }

        @Override
        public String getBankIconName() {
            return "";
        }

        @Override
        public String getBankName() {
            return "";
        }

        @Override
        public Drawable getBankIconDrawable(Context context) {
            return getBankIcon(context, getBankIconName());
        }
    };

    public abstract String getTransactionJS();

    /**
     * If the enter password js to be executed multiple times. By default it is false, make it true if required.
     *
     * @return
     */
    public boolean isMultipartEnterPasswordJS() {
        return false;
    }

    public String getMultiPartEnterPasswordJS() {
        return "javascript:";
    }

    public abstract String getEnterPasswordJS();

    /**
     * If the set otp to be executed multiple times. By default it is false, make it true if required.
     *
     * @return
     */
    public boolean isMultipartSendOTPJS() {
        return false;
    }

    public abstract String getSendOTPJS();

    public String getMultiPartSendOTPJS() {
        return "javascript:";
    }

    public abstract String getReSendOTPJS();

    public String getSetOTPJS(String otp) {
        return "javascript: " +
                "var inputs = document.querySelectorAll('input[type=password]');" +
                "inputs[inputs.length - 1].setAttribute('value','" + otp + "');";
    }

    public int getOTPLength() {
        return 6;
    }

    public boolean isBypassSendOTPButton() {
        return false;
    }

    public boolean isBypassEnterPasswordButton() {
        return false;
    }

    public abstract String getBankNameForParsing();

    public abstract String getBankIconName();

    public abstract String getBankName();

    public abstract Drawable getBankIconDrawable(Context context);

    public static NetBankForOTP getNetBankForOTP(String cardType, String bankName) {
        if ("Kotak Mahindra Bank Ltd".equalsIgnoreCase(bankName) && cardType.equalsIgnoreCase("Credit")) {
            return KOTAK_CREDIT;
        } else if ("Kotak Mahindra Bank Ltd".equalsIgnoreCase(bankName) && cardType.equalsIgnoreCase("Debit")) {
            return KOTAK_DEBIT;
        } else if ("ICICI BANK LTD".equalsIgnoreCase(bankName) && cardType.equalsIgnoreCase("Credit")) {
            return ICICI_CREDIT;
        } else if ("ICICI BANK LTD".equalsIgnoreCase(bankName) && cardType.equalsIgnoreCase("Debit")) {
            return ICICI_DEBIT;
        } else if ("State Bank of India".equalsIgnoreCase(bankName) || "SBI(Maestro)".equalsIgnoreCase(bankName) || "SBI CARDS & PAYMENTS".equalsIgnoreCase(bankName)) {
            return SBI;
        } else if ("HDFC BANK LIMITED".equalsIgnoreCase(bankName) || "HDFC BANK LIMITED(Maestro)".equalsIgnoreCase(bankName)) {
            return HDFC;
        } else if ("CITI BANK LTD".equalsIgnoreCase(bankName) || "CITI BANK LTD(Maestro)".equalsIgnoreCase(bankName)) {
            return CITI;
        } else {
            return UNKNOWN;
        }
    }


    public static Drawable getBankIcon(Context context, String bankIconName) {
        // Return the icon depending upon the scheme of the card.
        Drawable drawable = null;

        int resourceId = 0;

        if (!bankIconName.equalsIgnoreCase(""))
            resourceId = context.getResources().getIdentifier(bankIconName, "drawable", context.getPackageName());
        else
            resourceId = context.getResources().getIdentifier("default_bank", "drawable", context.getPackageName());
//
//        if ("axis_bank".equalsIgnoreCase(bankName)) {
//            resourceId = context.getResources().getIdentifier("axis_bank", "drawable", context.getPackageName());
//        }
//        else if ("andhra_bank".equalsIgnoreCase(bankName)) {
//            resourceId = context.getResources().getIdentifier("andhra_bank", "drawable", context.getPackageName());
//        }
//        else if ("Bank of India".equalsIgnoreCase(bankName)) {
//            resourceId = context.getResources().getIdentifier("bank_of_india", "drawable", context.getPackageName());
//        } else if ("Bank Of Baroda".equalsIgnoreCase(bankName)) {
//            resourceId = context.getResources().getIdentifier("bank_of_baroda", "drawable", context.getPackageName());
//        } else if ("Bank of Maharashtra".equalsIgnoreCase(bankName)) {
//            resourceId = context.getResources().getIdentifier("bank_of_maharashtra", "drawable", context.getPackageName());
//        }
//        else if ("Catholic Syrian Bank".equalsIgnoreCase(bankName)) {
//            resourceId = context.getResources().getIdentifier("catholic_syrian", "drawable", context.getPackageName());
//        }
//        else if ("Central Bank of India".equalsIgnoreCase(bankName)) {
//            resourceId = context.getResources().getIdentifier("central_bank_of_india", "drawable", context.getPackageName());
//        } else if ("Citibank".equalsIgnoreCase(bankName)) {
//            resourceId = context.getResources().getIdentifier("citi_bank", "drawable", context.getPackageName());
//        }
//        else if ("CITI Bank".equalsIgnoreCase(bankName)) {
//            resourceId = context.getResources().getIdentifier("citi_bank", "drawable", context.getPackageName());
//        }
//        else if ("Corporation Bank".equalsIgnoreCase(bankName)) {
//            resourceId = context.getResources().getIdentifier("corporation_bank", "drawable", context.getPackageName());
//        } else if ("City Union Bank".equalsIgnoreCase(bankName)) {
//            resourceId = context.getResources().getIdentifier("city_union_bank", "drawable", context.getPackageName());
//        } else if ("Canara Bank".equalsIgnoreCase(bankName)) {
//            resourceId = context.getResources().getIdentifier("canara_bank", "drawable", context.getPackageName());
//        }
//        else if ("Cosmos Bank".equalsIgnoreCase(bankName)) {
//            resourceId = context.getResources().getIdentifier("cosmos_bank", "drawable", context.getPackageName());
//        }
//        else if ("DEUTSCHE Bank".equalsIgnoreCase(bankName)) {
//            resourceId = context.getResources().getIdentifier("deutsche_bank", "drawable", context.getPackageName());
//        }
//        else if ("DCB Bank Personal".equalsIgnoreCase(bankName)) {
//            resourceId = context.getResources().getIdentifier("dcb_bank", "drawable", context.getPackageName());
//        }
//        else if ("Federal Bank".equalsIgnoreCase(bankName)) {
//            resourceId = context.getResources().getIdentifier("federal_bank", "drawable", context.getPackageName());
//        } else if ("HDFC Bank".equalsIgnoreCase(bankName)) {
//            resourceId = context.getResources().getIdentifier("hdfc_bank", "drawable", context.getPackageName());
//        } else if ("ICICI Bank".equalsIgnoreCase(bankName)) {
//            resourceId = context.getResources().getIdentifier("icici_bank", "drawable", context.getPackageName());
//        } else if ("IDBI Bank".equalsIgnoreCase(bankName)) {
//            resourceId = context.getResources().getIdentifier("idbi_bank", "drawable", context.getPackageName());
//        } else if ("Indian Bank".equalsIgnoreCase(bankName)) {
//            resourceId = context.getResources().getIdentifier("indian_bank", "drawable", context.getPackageName());
//        } else if ("Indian Overseas Bank".equalsIgnoreCase(bankName)) {
//            resourceId = context.getResources().getIdentifier("indian_overseas_bank", "drawable", context.getPackageName());
//        } else if ("IndusInd Bank".equalsIgnoreCase(bankName)) {
//            resourceId = context.getResources().getIdentifier("indusind_bank", "drawable", context.getPackageName());
//        } else if ("ING VYSA".equalsIgnoreCase(bankName)) {
//            resourceId = context.getResources().getIdentifier("ing_vysa", "drawable", context.getPackageName());
//        } else if ("Kotak Mahindra Bank".equalsIgnoreCase(bankName)) {
//            resourceId = context.getResources().getIdentifier("kotak_mahindra_bank", "drawable", context.getPackageName());
//        }
//        else if ("ING Vysya Bank (now Kotak)".equalsIgnoreCase(bankName)) {
//            resourceId = context.getResources().getIdentifier("kotak_mahindra_bank", "drawable", context.getPackageName());
//        }
//        else if ("Karur Vysya Bank".equalsIgnoreCase(bankName)) {
//            resourceId = context.getResources().getIdentifier("karur_vyasa_bank", "drawable", context.getPackageName());
//        }
//        else if ("Karnataka Bank".equalsIgnoreCase(bankName)) {
//            resourceId = context.getResources().getIdentifier("karnataka_bank", "drawable", context.getPackageName());
//        } else if ("PNB Retail".equalsIgnoreCase(bankName)) {
//            resourceId = context.getResources().getIdentifier("punjab_national", "drawable", context.getPackageName());
//        } else if ("PNB Corporate".equalsIgnoreCase(bankName)) {
//            resourceId = context.getResources().getIdentifier("punjab_national", "drawable", context.getPackageName());
//        } else if ("SBI Bank".equalsIgnoreCase(bankName)) {
//            resourceId = context.getResources().getIdentifier("sbi_bank", "drawable", context.getPackageName());
//        } else if ("State Bank of Bikaner and Jaipur".equalsIgnoreCase(bankName)) {
//            resourceId = context.getResources().getIdentifier("state_bank_of_bikaner_and_jaipur", "drawable", context.getPackageName());
//        } else if ("State Bank of Hyderabad".equalsIgnoreCase(bankName)) {
//            resourceId = context.getResources().getIdentifier("state_bank_of_hyderabad", "drawable", context.getPackageName());
//        } else if ("State Bank of Mysore".equalsIgnoreCase(bankName)) {
//            resourceId = context.getResources().getIdentifier("state_bank_of_mysore", "drawable", context.getPackageName());
//        } else if ("State Bank of Travancore".equalsIgnoreCase(bankName)) {
//            resourceId = context.getResources().getIdentifier("state_bank_of_travancore", "drawable", context.getPackageName());
//        } else if ("State Bank of Patiala".equalsIgnoreCase(bankName)) {
//            resourceId = context.getResources().getIdentifier("state_bank_of_patiala", "drawable", context.getPackageName());
//        }
//        else if ("South Indian Bank".equalsIgnoreCase(bankName)) {
//            resourceId = context.getResources().getIdentifier("south_indian_bank", "drawable", context.getPackageName());
//        }
//        else if ("Union Bank Of India".equalsIgnoreCase(bankName)) {
//            resourceId = context.getResources().getIdentifier("union_bank_of_india", "drawable", context.getPackageName());
//        }
//        else if ("Union Bank".equalsIgnoreCase(bankName)) {
//            resourceId = context.getResources().getIdentifier("union_bank", "drawable", context.getPackageName());
//        }
//        else if ("UCO Bank".equalsIgnoreCase(bankName)) {
//            resourceId = context.getResources().getIdentifier("uco", "drawable", context.getPackageName());
//        }
//        else if ("United Bank of India".equalsIgnoreCase(bankName)) {
//            resourceId = context.getResources().getIdentifier("united_bank_of_india", "drawable", context.getPackageName());
//        } else if ("Vijaya Bank".equalsIgnoreCase(bankName)) {
//            resourceId = context.getResources().getIdentifier("vijaya_bank", "drawable", context.getPackageName());
//        } else if ("YES Bank".equalsIgnoreCase(bankName)) {
//            resourceId = context.getResources().getIdentifier("yes_bank", "drawable", context.getPackageName());
//        } else {
//            resourceId = context.getResources().getIdentifier("default_bank", "drawable", context.getPackageName());
//        }
//
//        if (resourceId == 0) {
//            resourceId = context.getResources().getIdentifier("default_bank", "drawable", context.getPackageName());
//        }

        if (resourceId != 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                drawable = context.getResources().getDrawable(resourceId, null);
            } else {
                drawable = context.getResources().getDrawable(resourceId);
            }
        }

        return drawable;
    }

}


// Bank List

/*//1     Andhra Bank	CID016
//2	    AXIS Bank	CID002
//3	    Bank of India	CID019
//4	    Bank Of Baroda	CID046
//5	    Bank of Maharashtra	CID021
//6	    Canara Bank	CID051
//7	    Catholic Syrian Bank	CID045
//8	    Central Bank of India	CID023
//9	    CITI Bank	CID003 **
//10	Corporation Bank	CID025
//11	City Union Bank	CID024
//12	DEUTSCHE Bank	CID006
//13	Federal Bank	CID009
//14	HDFC Bank	CID010 **
//15	ICICI Bank	CID001 **
//16	IDBI Bank	CID011
//17	Indian Bank	CID008
//18	Indian Overseas Bank	CID027
//19	Induslnd Bank	CID028
//20	ING VYSA	CID029
//21	Karnataka Bank	CID031
//22	Kotak Mahindra Bank	CID033 **
//23	Karur Vysya Bank	CID032
//24	PNB Retail	CID044
//25	PNB Corporate	CID036
//26	SBI Bank	CID005 **
//27	State Bank of Bikaner and Jaipur	CID013
//28	State Bank of Hyderabad	CID012
//29	State Bank of Mysore	CID014
//30	State Bank of Travancore	CID015
//31	State Bank of Patiala	CID043
//32	Union Bank Of India	CID007
//33	United Bank of India	CID041
//34	Vijaya Bank	CID042
//35	YES Bank	CID004
//36	Cosmos Bank	CID053
//37	UCO Bank	CID070
*/
