/*
 *
 *    Copyright 2014 Citrus Payment Solutions Pvt. Ltd.
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *      http://www.apache.org/licenses/LICENSE-2.0
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 * /
 */

package com.citrus.sdk.classes;

import com.citrus.sdk.Constants;

/**
 * Created by salil on 18/5/15.
 */
public class CitrusConfig {

    private static CitrusConfig instance = null;

    private boolean pgHealthEnabled = false;
    private boolean autoReadOTP = false;

    /**
     * Primary color for the app, in the form of #123456.
     * This is the color of the status bar when the is opened.
     * This will be used only on android versions lollipop and above.
     */
    private String colorPrimaryDark = Constants.colorPrimaryDark;
    /**
     * Main color code for the app in the form #123456
     */
    private String colorPrimary = Constants.colorPrimary;
    /**
     * Primary text color. #123456
     */
    private String textColorPrimary = Constants.textColor;
    /**
     * Accent color for the app, will be used to display common actions.
     */
    private String accentColor = Constants.accentColor;

    private String citrusActivityTitle = null;

    private CitrusConfig() {
    }

    public static CitrusConfig getInstance() {
        if (instance == null) {
            synchronized (CitrusConfig.class) {
                if (instance == null) {
                    instance = new CitrusConfig();
                }
            }
        }

        return instance;
    }

//    public void enablePGHealth(boolean enable) {
//        this.pgHealthEnabled = enable;
//    }

//
//    public void setAutoReadOTP(boolean autoReadOTP) {
//        this.autoReadOTP = autoReadOTP;
//    }

    public synchronized void setColorPrimaryDark(String colorPrimaryDark) {
        this.colorPrimaryDark = colorPrimaryDark;
    }

    public synchronized void setColorPrimary(String colorPrimary) {
        this.colorPrimary = colorPrimary;
    }

    public synchronized void setTextColorPrimary(String textColorPrimary) {
        this.textColorPrimary = textColorPrimary;
    }

    public synchronized void setAccentColor(String accentColor) {
        this.accentColor = accentColor;
    }

    public String getColorPrimaryDark() {
        return colorPrimaryDark;
    }

    public String getColorPrimary() {
        return colorPrimary;
    }

    public String getTextColorPrimary() {
        return textColorPrimary;
    }

    public String getAccentColor() {
        return accentColor;
    }

    public String getCitrusActivityTitle() {
        return citrusActivityTitle;
    }

    public void setCitrusActivityTitle(String citrusActivityTitle) {
        this.citrusActivityTitle = citrusActivityTitle;
    }
}
