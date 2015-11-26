package com.citrus.sdk.dynamicPricing;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.citrus.sdk.CitrusUser;
import com.citrus.sdk.classes.Amount;
import com.citrus.sdk.payment.PaymentOption;

import java.util.Map;

/**
 * Created by salil on 1/9/15.
 */
public abstract class DynamicPricingRequestType {

    protected Amount originalAmount;
    protected PaymentOption paymentOption;
    protected CitrusUser citrusUser;
    protected Map<String, String> extraParameters;

    /**
     * @param originalAmount - Original Transaction Amount.
     * @param paymentOption  - Selected Payment Option like Credit Card, Debit Card, NetBanking, Citrus Cash
     * @param citrusUser     - User Details
     * @throws IllegalArgumentException - If originalAmount or paymentOption is null.
     */
    public DynamicPricingRequestType(@NonNull Amount originalAmount, @NonNull PaymentOption paymentOption, CitrusUser citrusUser) throws IllegalArgumentException {
        this(originalAmount, paymentOption, citrusUser, null);
    }

    /**
     * @param originalAmount  - Original Transaction Amount.
     * @param paymentOption   - Selected Payment Option like Credit Card, Debit Card, NetBanking, Citrus Cash
     * @param citrusUser      - User Details
     * @param extraParameters - Extra Parameters.
     * @throws IllegalArgumentException - If originalAmount or paymentOption is null.
     */
    public DynamicPricingRequestType(@NonNull Amount originalAmount, @NonNull PaymentOption paymentOption, CitrusUser citrusUser, Map<String, String> extraParameters) throws IllegalArgumentException {
        this.originalAmount = originalAmount;
        this.paymentOption = paymentOption;
        this.citrusUser = citrusUser;
        this.extraParameters = extraParameters;

        if (originalAmount == null) {
            throw new IllegalArgumentException("originalAmount should not be null");
        }

        if (paymentOption == null) {
            throw new IllegalArgumentException("paymentOption should not be null");
        }
    }

    public Amount getOriginalAmount() {
        return originalAmount;
    }

    public PaymentOption getPaymentOption() {
        return paymentOption;
    }

    public CitrusUser getCitrusUser() {
        return citrusUser;
    }

    public Map<String, String> getExtraParameters() {
        return extraParameters;
    }

    public abstract String getDPOperationName();

    public static class SearchAndApplyRule extends DynamicPricingRequestType {

        /**
         * @param originalAmount - Original Transaction Amount.
         * @param paymentOption  - Selected Payment Option like Credit Card, Debit Card, NetBanking, Citrus Cash
         * @param citrusUser     - User Details
         * @throws IllegalArgumentException - If originalAmount or paymentOption is null.
         */
        public SearchAndApplyRule(@NonNull Amount originalAmount, @NonNull PaymentOption paymentOption, CitrusUser citrusUser) throws IllegalArgumentException {
            this(originalAmount, paymentOption, citrusUser, null);
        }

        /**
         * @param originalAmount  - Original Transaction Amount.
         * @param paymentOption   - Selected Payment Option like Credit Card, Debit Card, NetBanking, Citrus Cash
         * @param citrusUser      - User Details
         * @param extraParameters - Extra Parameters.
         * @throws IllegalArgumentException - If originalAmount or paymentOption is null.
         */
        public SearchAndApplyRule(@NonNull Amount originalAmount, @NonNull PaymentOption paymentOption, CitrusUser citrusUser, Map<String, String> extraParameters) throws IllegalArgumentException {
            super(originalAmount, paymentOption, citrusUser, extraParameters);
        }

        @Override
        public String getDPOperationName() {
            return "searchAndApply";
        }
    }

    public static class CalculatePrice extends DynamicPricingRequestType {

        private String ruleName;

        /**
         * @param originalAmount - Original Transaction Amount.
         * @param paymentOption  - Selected Payment Option like Credit Card, Debit Card, NetBanking, Citrus Cash
         * @param ruleName       - Name of the rule or Coupon Code.
         * @param citrusUser     - User Details
         * @throws IllegalArgumentException - If originalAmount or paymentOption or ruleName is null or empty.
         */
        public CalculatePrice(@NonNull Amount originalAmount, @NonNull PaymentOption paymentOption, @NonNull String ruleName, CitrusUser citrusUser) throws IllegalArgumentException {
            this(originalAmount, paymentOption, ruleName, citrusUser, null);
        }

        /**
         * @param originalAmount  - Original Transaction Amount.
         * @param paymentOption   - Selected Payment Option like Credit Card, Debit Card, NetBanking, Citrus Cash
         * @param ruleName        - Name of the rule or coupon Code.
         * @param citrusUser      - User Details
         * @param extraParameters - Extra Parameters.
         * @throws IllegalArgumentException - If originalAmount or paymentOption or ruleName is null or empty.
         */
        public CalculatePrice(@NonNull Amount originalAmount, @NonNull PaymentOption paymentOption, @NonNull String ruleName, CitrusUser citrusUser, Map<String, String> extraParameters) throws IllegalArgumentException {
            super(originalAmount, paymentOption, citrusUser, extraParameters);
            this.ruleName = ruleName;

            if (TextUtils.isEmpty(ruleName)) {
                throw new IllegalArgumentException("ruleName should not be null.");
            }
        }

        public String getRuleName() {
            return ruleName;
        }

        @Override
        public String getDPOperationName() {
            return "calculatePricing";
        }
    }

    public static class ValidateRule extends DynamicPricingRequestType {

        private String ruleName;
        private Amount alteredAmount;

        /**
         * @param originalAmount - Original Transaction Amount.
         * @param paymentOption  - Selected Payment Option like Credit Card, Debit Card, NetBanking, Citrus Cash.
         * @param ruleName       - Name of the rule or Coupon Code.
         * @param alteredAmount  - Altered Transaction Amount.
         * @param citrusUser     - User Details.
         * @throws IllegalArgumentException - If originalAmount or paymentOption or ruleName or alteredAmount is null or empty.
         */
        public ValidateRule(@NonNull Amount originalAmount, @NonNull PaymentOption paymentOption, @NonNull String ruleName, @NonNull Amount alteredAmount, CitrusUser citrusUser) throws IllegalArgumentException {
            this(originalAmount, paymentOption, ruleName, alteredAmount, citrusUser, null);
        }

        /**
         * @param originalAmount  - Original Transaction Amount.
         * @param paymentOption   - Selected Payment Option like Credit Card, Debit Card, NetBanking, Citrus Cash
         * @param ruleName        - Name of the rule or Coupon Code.
         * @param alteredAmount   - Altered Transaction Amount.
         * @param citrusUser      - User Details
         * @param extraParameters - Extra Parameters.
         * @throws IllegalArgumentException - If originalAmount or paymentOption or ruleName or alteredAmount is null or empty.
         */
        public ValidateRule(@NonNull Amount originalAmount, @NonNull PaymentOption paymentOption, @NonNull String ruleName, @NonNull Amount alteredAmount, CitrusUser citrusUser, Map<String, String> extraParameters) throws IllegalArgumentException {
            super(originalAmount, paymentOption, citrusUser, extraParameters);
            this.ruleName = ruleName;
            this.alteredAmount = alteredAmount;

            if (TextUtils.isEmpty(ruleName)) {
                throw new IllegalArgumentException("ruleName should not be null.");
            }

            if (alteredAmount == null) {
                throw new IllegalArgumentException("alteredAmount should not be null.");
            }
        }

        public String getRuleName() {
            return ruleName;
        }

        public Amount getAlteredAmount() {
            return alteredAmount;
        }

        @Override
        public String getDPOperationName() {
            return "validateRule";
        }
    }
}