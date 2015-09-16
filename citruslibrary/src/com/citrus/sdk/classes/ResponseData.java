package com.citrus.sdk.classes;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by MANGESH KADAM on 6/23/2015.
 */
public class ResponseData {

    private ProfileByEmail profileByEmail;
    private ProfileByMobile profileByMobile;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     *
     * @return
     * The profileByEmail
     */
    public ProfileByEmail getProfileByEmail() {
        return profileByEmail;
    }

    /**
     *
     * @param profileByEmail
     * The profileByEmail
     */
    public void setProfileByEmail(ProfileByEmail profileByEmail) {
        this.profileByEmail = profileByEmail;
    }

    /**
     *
     * @return
     * The profileByMobile
     */
    public ProfileByMobile getProfileByMobile() {
        return profileByMobile;
    }

    /**
     *
     * @param profileByMobile
     * The profileByMobile
     */
    public void setProfileByMobile(ProfileByMobile profileByMobile) {
        this.profileByMobile = profileByMobile;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
