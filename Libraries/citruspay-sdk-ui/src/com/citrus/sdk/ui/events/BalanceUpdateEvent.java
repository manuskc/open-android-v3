package com.citrus.sdk.ui.events;

import com.citrus.sdk.classes.Amount;

/**
 * Created by akshaykoul on 6/11/15.
 */
public class BalanceUpdateEvent {
    Amount amount;
    boolean success = false;

    public BalanceUpdateEvent(boolean success,Amount amount) {
        this.success = success;
        this.amount = amount;
    }

    public Amount getAmount() {
        return amount;
    }

    public void setAmount(Amount amount) {
        this.amount = amount;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
