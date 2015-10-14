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
        public String getDynamicPricingBaseUrl() {
            return "https://sandboxmars1.citruspay.com/dynamic-pricing/";
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
        public String getDynamicPricingBaseUrl() {
            return "https://mars.citruspay.com/dynamicpricing";
        }

        @Override
        public String toString() {
            return "PRODUCTION";
        }
    }, OOPS {
        @Override
        public String getBaseUrl() {
            return "https://oops.citruspay.com";
        }

        @Override
        public String getBaseCitrusUrl() {
            return "https://oops.citruspay.com";
        }

        @Override
        public String getDynamicPricingBaseUrl() {
            return "https://oops.citruspay.com/dynamic-pricing/";
        }

        @Override
        public String toString() {
            return "OOPS";
        }
    },
    STG5 {
        @Override
        public String getBaseUrl() {
            return "https://stgadmin5.citruspay.com/";
        }

        @Override
        public String getBaseCitrusUrl() {
            return "https://stgadmin5.citruspay.com/";
        }

        @Override
        public String getDynamicPricingBaseUrl() {
            return "https://stgadmin5.citruspay.com/dynamic-pricing/";
        }

        @Override
        public String toString() {
            return "STG5";
        }
    },STG3 {
        @Override
        public String getBaseUrl() {
            return "https://stg3admin.citruspay.com/";
        }

        @Override
        public String getBaseCitrusUrl() {
            return "https://stg3admin.citruspay.com/";
        }

        @Override
        public String getDynamicPricingBaseUrl() {
            return "https://stg3admin.citruspay.com/dynamic-pricing/";
        }

        @Override
        public String toString() {
            return "STG5";
        }
    }, NONE {
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
        public String getDynamicPricingBaseUrl() {
            return null;
        }
    };

    public abstract String getBaseUrl();

    public abstract String getBaseCitrusUrl();

    public abstract String getDynamicPricingBaseUrl();
}
