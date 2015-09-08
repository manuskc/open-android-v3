package com.citrus.mobile;

/**
 * Created by MANGESH KADAM on 4/23/2015.
 */

/**
 * @deprecated in v3
 * <p/>
 * Use {@link com.citrus.sdk.classes.Year} instead.
 */
public enum Year {
    _2015("2015"),
    _2016("2016"),
    _2017("2017"),
    _2018("2018"),
    _2019("2019"),
    _2020("2020"),
    _2021("2021"),
    _2022("2022"),
    _2023("2023"),
    _2024("2024"),
    _2025("2025"),
    _2026("2026"),
    _2027("2027"),
    _2028("2028"),
    _2029("2029"),
    _2030("2030"),
    _2031("2031"),
    _2032("2032"),
    _2033("2033"),
    _2034("2034"),
    _2035("2035"),
    _2036("2036"),
    _2037("2037"),
    _2038("2038"),
    _2039("2039"),
    _2040("2040"),
    _2041("2041"),
    _2042("2042"),
    _2043("2043"),
    _2044("2044"),
    _2045("2045"),
    _2046("2046"),
    _2047("2047"),
    _2048("2048"),
    _2049("2049"),
    _2050("2050"),
    _2051("2051"),
    _2052("2052"),
    _2053("2053"),
    _2054("2054"),
    _2055("2055"),
    _2056("2056"),
    _2057("2057"),
    _2058("2058"),
    _2059("2059"),
    _2060("2060"),
    _2061("2061"),
    _2062("2062"),
    _2063("2063"),
    _2064("2064"),
    _2065("2065"),
    _2066("2066"),
    _2067("2067"),
    _2068("2068"),
    _2069("2069"),
    _2070("2070"),
    _2071("2071"),
    _2072("2072"),
    _2073("2073"),
    _2074("2074"),
    _2075("2075"),
    _2076("2076"),
    _2077("2077"),
    _2078("2078"),
    _2079("2079"),
    _2080("2080"),
    _2081("2081"),
    _2082("2082"),
    _2083("2083"),
    _2084("2084"),
    _2085("2085"),
    _2086("2086"),
    _2087("2087"),
    _2088("2088"),
    _2089("2089"),
    _2090("2090"),
    _2091("2091"),
    _2092("2092"),
    _2093("2093"),
    _2094("2094"),
    _2095("2095"),
    _2096("2096"),
    _2097("2097"),
    _2098("2098"),
    _2099("2099");

    private final String year;

    Year(String year) {
        this.year = year;
    }

    @Override
    public String toString() {
        return year;
    }

    /**
     * Get an object of Year
     *
     * @param year - year in yyyy or yy format
     * @return
     */
    public static Year getYear(String year) {
        Year year1 = null;

        if ("15".equals(year) || "2015".equals(year)) {
            year1 = _2015;
        } else if ("16".equals(year) || "2016".equals(year)) {
            year1 = _2016;
        } else if ("17".equals(year) || "2017".equals(year)) {
            year1 = _2017;
        } else if ("18".equals(year) || "2018".equals(year)) {
            year1 = _2018;
        } else if ("19".equals(year) || "2019".equals(year)) {
            year1 = _2019;
        } else if ("20".equals(year) || "2020".equals(year)) {
            year1 = _2020;
        } else if ("21".equals(year) || "2021".equals(year)) {
            year1 = _2021;
        } else if ("22".equals(year) || "2022".equals(year)) {
            year1 = _2022;
        } else if ("23".equals(year) || "2023".equals(year)) {
            year1 = _2023;
        } else if ("24".equals(year) || "2024".equals(year)) {
            year1 = _2024;
        } else if ("25".equals(year) || "2025".equals(year)) {
            year1 = _2025;
        } else if ("26".equals(year) || "2026".equals(year)) {
            year1 = _2026;
        } else if ("27".equals(year) || "2027".equals(year)) {
            year1 = _2027;
        } else if ("28".equals(year) || "2028".equals(year)) {
            year1 = _2028;
        } else if ("29".equals(year) || "2029".equals(year)) {
            year1 = _2029;
        } else if ("30".equals(year) || "2030".equals(year)) {
            year1 = _2030;
        } else if ("31".equals(year) || "2031".equals(year)) {
            year1 = _2031;
        } else if ("32".equals(year) || "2032".equals(year)) {
            year1 = _2032;
        } else if ("33".equals(year) || "2033".equals(year)) {
            year1 = _2033;
        } else if ("34".equals(year) || "2034".equals(year)) {
            year1 = _2034;
        } else if ("35".equals(year) || "2035".equals(year)) {
            year1 = _2035;
        } else if ("36".equals(year) || "2036".equals(year)) {
            year1 = _2036;
        } else if ("37".equals(year) || "2037".equals(year)) {
            year1 = _2037;
        } else if ("38".equals(year) || "2038".equals(year)) {
            year1 = _2038;
        } else if ("39".equals(year) || "2039".equals(year)) {
            year1 = _2039;
        } else if ("40".equals(year) || "2040".equals(year)) {
            year1 = _2040;
        } else if ("41".equals(year) || "2041".equals(year)) {
            year1 = _2041;
        } else if ("42".equals(year) || "2042".equals(year)) {
            year1 = _2042;
        } else if ("43".equals(year) || "2043".equals(year)) {
            year1 = _2043;
        } else if ("44".equals(year) || "2044".equals(year)) {
            year1 = _2044;
        } else if ("45".equals(year) || "2045".equals(year)) {
            year1 = _2045;
        } else if ("46".equals(year) || "2046".equals(year)) {
            year1 = _2046;
        } else if ("47".equals(year) || "2047".equals(year)) {
            year1 = _2047;
        } else if ("48".equals(year) || "2048".equals(year)) {
            year1 = _2048;
        } else if ("49".equals(year) || "2049".equals(year)) {
            year1 = _2049;
        } else if ("50".equals(year) || "2050".equals(year)) {
            year1 = _2050;
        } else if ("51".equals(year) || "2051".equals(year)) {
            year1 = _2051;
        } else if ("52".equals(year) || "2052".equals(year)) {
            year1 = _2052;
        } else if ("53".equals(year) || "2053".equals(year)) {
            year1 = _2053;
        } else if ("54".equals(year) || "2054".equals(year)) {
            year1 = _2054;
        } else if ("55".equals(year) || "2055".equals(year)) {
            year1 = _2055;
        } else if ("56".equals(year) || "2056".equals(year)) {
            year1 = _2056;
        } else if ("57".equals(year) || "2057".equals(year)) {
            year1 = _2057;
        } else if ("58".equals(year) || "2058".equals(year)) {
            year1 = _2058;
        } else if ("59".equals(year) || "2059".equals(year)) {
            year1 = _2059;
        } else if ("60".equals(year) || "2060".equals(year)) {
            year1 = _2060;
        } else if ("61".equals(year) || "2061".equals(year)) {
            year1 = _2061;
        } else if ("62".equals(year) || "2062".equals(year)) {
            year1 = _2062;
        } else if ("63".equals(year) || "2063".equals(year)) {
            year1 = _2063;
        } else if ("64".equals(year) || "2064".equals(year)) {
            year1 = _2064;
        } else if ("65".equals(year) || "2065".equals(year)) {
            year1 = _2065;
        } else if ("66".equals(year) || "2066".equals(year)) {
            year1 = _2066;
        } else if ("67".equals(year) || "2067".equals(year)) {
            year1 = _2067;
        } else if ("68".equals(year) || "2068".equals(year)) {
            year1 = _2068;
        } else if ("69".equals(year) || "2069".equals(year)) {
            year1 = _2069;
        } else if ("70".equals(year) || "2070".equals(year)) {
            year1 = _2070;
        } else if ("71".equals(year) || "2071".equals(year)) {
            year1 = _2071;
        } else if ("72".equals(year) || "2072".equals(year)) {
            year1 = _2072;
        } else if ("73".equals(year) || "2073".equals(year)) {
            year1 = _2073;
        } else if ("74".equals(year) || "2074".equals(year)) {
            year1 = _2074;
        } else if ("75".equals(year) || "2075".equals(year)) {
            year1 = _2075;
        } else if ("76".equals(year) || "2076".equals(year)) {
            year1 = _2076;
        } else if ("77".equals(year) || "2077".equals(year)) {
            year1 = _2077;
        } else if ("78".equals(year) || "2078".equals(year)) {
            year1 = _2078;
        } else if ("79".equals(year) || "2079".equals(year)) {
            year1 = _2079;
        } else if ("80".equals(year) || "2080".equals(year)) {
            year1 = _2080;
        } else if ("81".equals(year) || "2081".equals(year)) {
            year1 = _2081;
        } else if ("82".equals(year) || "2082".equals(year)) {
            year1 = _2082;
        } else if ("83".equals(year) || "2083".equals(year)) {
            year1 = _2083;
        } else if ("84".equals(year) || "2084".equals(year)) {
            year1 = _2084;
        } else if ("85".equals(year) || "2085".equals(year)) {
            year1 = _2085;
        } else if ("86".equals(year) || "2086".equals(year)) {
            year1 = _2086;
        } else if ("87".equals(year) || "2087".equals(year)) {
            year1 = _2087;
        } else if ("88".equals(year) || "2088".equals(year)) {
            year1 = _2088;
        } else if ("89".equals(year) || "2089".equals(year)) {
            year1 = _2089;
        } else if ("90".equals(year) || "2090".equals(year)) {
            year1 = _2090;
        } else if ("91".equals(year) || "2091".equals(year)) {
            year1 = _2091;
        } else if ("92".equals(year) || "2092".equals(year)) {
            year1 = _2092;
        } else if ("93".equals(year) || "2093".equals(year)) {
            year1 = _2093;
        } else if ("94".equals(year) || "2094".equals(year)) {
            year1 = _2094;
        } else if ("95".equals(year) || "2095".equals(year)) {
            year1 = _2095;
        } else if ("96".equals(year) || "2096".equals(year)) {
            year1 = _2096;
        } else if ("97".equals(year) || "2097".equals(year)) {
            year1 = _2097;
        } else if ("98".equals(year) || "2098".equals(year)) {
            year1 = _2098;
        } else if ("99".equals(year) || "2099".equals(year)) {
            year1 = _2099;
        }

        return year1;
    }


}
