package com.citrus.analytics;

/**
 * Created by MANGESH KADAM on 4/24/2015.
 */
public enum APILevel {
    Level9(67),
    Level10(71),
    Level14(73),
    Level15(79),
    Level16(83),
    Level17(89),
    Level18(97),
    Level19(101),
    Level21(103),
    Level22(107),
    LevelOther(109);

    private final int apiValue;

    APILevel(int apiValue) {
        this.apiValue = apiValue;
    }

    public static int getValue(int APIID) {
        for(APILevel level : APILevel.values()){
            if(level.toString().contains(String.valueOf(APIID))) {
                return level.apiValue;
            }
        }

        return LevelOther.apiValue;
    }

}
