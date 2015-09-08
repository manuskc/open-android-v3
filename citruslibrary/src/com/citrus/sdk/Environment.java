package com.citrus.sdk;

/**
 * Created by MANGESH KADAM on 6/3/2015.
 */
public enum Environment {
    SANDBOX {
        @Override
        public String getBaseUrl() {
            return "https://sandboxadmin.citruspay.com";
        }

        @Override
        public String getBaseCitrusUrl() {
            return "https://sandbox.citruspay.com";
        }

        @Override
        public String toString() {
            return "SANDBOX";
        }
    }, PRODUCTION {
        @Override
        public String getBaseUrl() {
            return "https://admin.citruspay.com";
        }

        @Override
        public String getBaseCitrusUrl() {
            return "https://citruspay.com";
        }

        @Override
        public String toString() {
            return "PRODUCTION";
        }
    },OOPS {
        @Override
        public String getBaseUrl() {
            return "https://oops.citruspay.com";
        }

        @Override
        public String getBaseCitrusUrl() {
            return "https://oops.citruspay.com";
        }

        @Override
        public String toString() {
            return "OOPS";
        }
    },
    STG4 {
        @Override
        public String getBaseUrl() {
            return "https://stg4admin.citruspay.com";
        }

        @Override
        public String getBaseCitrusUrl() {
            return "https://stg4admin.citruspay.com";
        }

        @Override
        public String toString() {
            return "STG4";
        }
    },NONE {
        @Override
        public String getBaseUrl() {
            return null;
        }

        @Override
        public String toString() {
            return null;
        }

        @Override
        public String getBaseCitrusUrl() {
            return null;
        }

    };

    public abstract String getBaseUrl();

    public abstract String getBaseCitrusUrl();
}
