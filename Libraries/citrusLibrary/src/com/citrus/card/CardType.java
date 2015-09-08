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
package com.citrus.card;

import com.citrus.sdk.payment.CardOption;

/**
 * @deprecated in v3
 * <p/>
 * Use {@link CardOption#getCardScheme()} instead.
 */
@Deprecated
public enum CardType {
    VISA("4"),
    MTRO("502260", "504433",
            "504434", "504435", "504437", "504645", "504681",
            "504753", "504775", "504809", "504817", "504834",
            "504848", "504884", "504973", "504993", "508125",
            "508126", "508159", "508192", "508227", "56",
            "600206", "603123", "603741", "603845", "622018",
            "67"),

    MCRD("5"),
    DINERS("30", "36", "38", "39"),
    JCB("35"),
    AMEX("34", "37"),
    DISCOVER("60", "62", "64", "65"),
    UNKNOWN("0");

    private final String[] pattern;

    private CardType(String... pattern) {
        this.pattern = pattern;
    }

    public static CardType typeOf(String card) {
        for (CardType type : values()) {
            if (TextUtils.hasAnyPrefix(card, type.pattern)) {
                return type;
            }
        }
        return null;
    }

    public static String getScheme(CardType cardType) {
        switch (cardType) {
            case VISA:
                return "VISA";
            case MCRD:
                return "MCRD";
            case MTRO:
                return "MTRO";
            case DINERS:
                return "DINERS";
            case JCB:
                return "JCB";
            case AMEX:
                return "AMEX";
            case DISCOVER:
                return "DISCOVER";
            default:
                return "UNKNOWN";
        }
    }
}
