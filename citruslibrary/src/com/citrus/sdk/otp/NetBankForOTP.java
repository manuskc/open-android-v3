package com.citrus.sdk.otp;

import android.text.TextUtils;

/**
 * This class will contain the JS related to sendOTP, enterPassword and processTransaction for every bank.
 * <p/>
 * Created by salil on 21/10/15.
 */
public enum NetBankForOTP {

    SBI {
        @Override
        public String getTransactionJS() {
            // TODO:
            return null;
        }

        @Override
        public String getEnterPasswordJS() {
            // TODO:
            return null;
        }

        @Override
        public String getSendOTPJS() {
            // TODO:
            return null;
        }

        @Override
        public String getReSendOTPJS() {
            return null;
        }

        @Override
        public int getOTPLength() {
            // TODO:
            return 6;
        }

        @Override
        public String getBankNameForParsing() {
            // TODO:
            return "SBI";
        }

        @Override
        public String getBankIconName() {
            return "sbi_bank";
        }
    },

    ICICI {
        @Override
        public String getTransactionJS() {
            // TODO:
            return null;
        }

        @Override
        public String getEnterPasswordJS() {
            // TODO:
            return null;
        }

        @Override
        public String getSendOTPJS() {
            // TODO:
            return null;
        }

        @Override
        public String getReSendOTPJS() {
            return null;
        }

        @Override
        public int getOTPLength() {
            // TODO:
            return 6;
        }

        @Override
        public String getBankNameForParsing() {
            // TODO:
            return "ICICIB";
        }

        @Override
        public String getBankIconName() {
            return "icici_bank";
        }
    }, HDFC {
        @Override
        public String getTransactionJS() {
            // TODO:
            return null;
        }

        @Override
        public String getEnterPasswordJS() {
            // TODO:
            return null;
        }

        @Override
        public String getSendOTPJS() {
            // TODO:
            return null;
        }

        @Override
        public String getReSendOTPJS() {
            return null;
        }

        @Override
        public int getOTPLength() {
            // TODO:
            return 6;
        }

        @Override
        public String getBankNameForParsing() {
            // TODO:
            return "HDFCBK";
        }

        @Override
        public String getBankIconName() {
            return "hdfc_bank";
        }
    }, KOTAK {
        @Override
        public String getTransactionJS() {
            // TODO:
            //return "javascript:document.getElementById(\"cmdSubmit\").click();";
            return "javascript: " +
                    "var inputs = document.querySelectorAll('input[type=password]');" +
                    "var forms = document.getElementsByTagName('form');" +
                    "inputs[inputs.length - 1].value='%s';" +
                    "forms[forms.length - 1].submit();";
        }

        @Override
        public String getEnterPasswordJS() {
            // TODO:
            return null;
        }

        @Override
        public String getSendOTPJS() {
            // TODO:
            return null;
        }

        @Override
        public String getReSendOTPJS() {
            // TODO:
            return "javascript:reSendOtp();";
        }

        @Override
        public int getOTPLength() {
            // TODO:
            return 6;
        }

        @Override
        public String getBankNameForParsing() {
            // TODO:
            return "KOTAKB";
        }

        @Override
        public String getBankIconName() {
            return "kotak_mahindra_bank";
        }
    }, CITI {
        @Override
        public String getTransactionJS() {
            return "javascript:document.optInForm.otp.value='%s'; validateOTP(1);";
        }

        @Override
        public String getEnterPasswordJS() {
            // TODO:
            return null;
        }

        @Override
        public String getSendOTPJS() {
            // TODO:
            return null;
        }

        @Override
        public String getReSendOTPJS() {
            return null;
        }

        @Override
        public int getOTPLength() {
            // TODO:
            return 6;
        }

        @Override
        public String getBankNameForParsing() {
            // TODO:
            return "CITIBK";
        }

        @Override
        public String getBankIconName() {
            return "citi_bank";
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
    };

    public abstract String getTransactionJS();

    public abstract String getEnterPasswordJS();

    public abstract String getSendOTPJS();

    public abstract String getReSendOTPJS();

    public abstract int getOTPLength();

    public abstract String getBankNameForParsing();

    public abstract String getBankIconName();

    public static NetBankForOTP getNetBankForOTP(String bankName) {
        if (TextUtils.equals(bankName, "Kotak Mahindra Bank Ltd")) {
            return KOTAK;
        } else if (TextUtils.equals(bankName, "ICICI BANK LTD")) {
            return ICICI;
        } else if (TextUtils.equals(bankName, "State Bank of India")) {
            return SBI;
        } else if (TextUtils.equals(bankName, "HDFC BANK LIMITED")) {
            return HDFC;
        } else if (TextUtils.equals(bankName, "CITI BANK LTD")) {
            return CITI;
        } else {
            return UNKNOWN;
        }
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
