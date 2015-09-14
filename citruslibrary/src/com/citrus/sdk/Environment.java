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
        public String getBinServiceURL() {
            return "https://citrusapi.citruspay.com";
        }

        @Override
        public String getVanity() {
            return "prepaid";
        }

        @Override
        public String getAnalyticsID() {
            return "UA-33514461-4";
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
        public String getBinServiceURL() {
            return "https://citrusapi.citruspay.com";
        }

        @Override
        public String getVanity() {
            return "prepaid";
        }

        @Override
        public String getAnalyticsID() {
            return "UA-33514461-5";
        }

        @Override
        public String toString() {
            return "PRODUCTION";
        }
    }, STAGING {
        @Override
        public String getBaseUrl() {
            return "https://stg1admin.citruspay.com";
        }

        @Override
        public String getBaseCitrusUrl() {
            return "https://staging.citruspay.com";
        }

        @Override
        public String getBinServiceURL() {
            return "https://citrusapi.citruspay.com";
        }

        @Override
        public String getVanity() {
            return "citrusbank";
        }

        @Override
        public String getAnalyticsID() {
            return "UA-33514461-4";
        }

        @Override
        public String toString() {
            return "STAGING";
        }
    },
    NONE {
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

        @Override
        public String getBinServiceURL() {
            return null;
        }

        @Override
        public String getVanity() {
            return null;
        }

        @Override
        public String getAnalyticsID() {
            return null;
        }

    };

    public abstract String getBaseUrl();

    public abstract String getBaseCitrusUrl();

    public abstract String getBinServiceURL();

    public abstract String getVanity();

    public abstract String getAnalyticsID();
}
