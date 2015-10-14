package com.citrus.sdk.dynamicPricing;

/**
 * Created by salil on 24/7/15.
 */
public enum DynamicPricingOperation {
    VALIDATE_RULE{
        @Override
        public String toString() {
            return "validateRule";
        }
    }, CALCULATE_PRICE{
        @Override
        public String toString() {
            return "calculatePricing";
        }
    }, SEARCH_AND_APPLY_RULE{
        @Override
        public String toString() {
            return "searchAndApply";
        }
    };
}
