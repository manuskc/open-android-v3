package com.citrus.sdk.otp;

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
        public int getOTPLength() {
            // TODO:
            return 6;
        }

        @Override
        public String getBankNameForParsing() {
            // TODO:
            return null;
        }

        @Override
        public String getBankCID() {
            return "CID005";
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
        public int getOTPLength() {
            // TODO:
            return 6;
        }

        @Override
        public String getBankNameForParsing() {
            // TODO:
            return null;
        }

        @Override
        public String getBankCID() {
            return "CID001";
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
        public int getOTPLength() {
            // TODO:
            return 6;
        }

        @Override
        public String getBankNameForParsing() {
            // TODO:
            return null;
        }

        @Override
        public String getBankCID() {
            return "CID010";
        }
    }, KOTAK {
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
        public int getOTPLength() {
            // TODO:
            return 6;
        }

        @Override
        public String getBankNameForParsing() {
            // TODO:
            return null;
        }

        @Override
        public String getBankCID() {
            return "CID033";
        }
    }, CITI {
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
        public int getOTPLength() {
            // TODO:
            return 6;
        }

        @Override
        public String getBankNameForParsing() {
            // TODO:
            return null;
        }

        @Override
        public String getBankCID() {
            return "CID003";
        }
    };

    public abstract String getTransactionJS();

    public abstract String getEnterPasswordJS();

    public abstract String getSendOTPJS();

    public abstract int getOTPLength();

    public abstract String getBankNameForParsing();

    public abstract String getBankCID();

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
