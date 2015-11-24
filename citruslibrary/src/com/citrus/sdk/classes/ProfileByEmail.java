package com.citrus.sdk.classes;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by MANGESH KADAM on 6/23/2015.
 */
public class ProfileByEmail {

    private String email;
    private Integer emailVerified;
    private Long  emailVerifiedDate;
    private String mobile;
    private Integer mobileVerified;
    private Long  mobileVerifiedDate;
    private String firstName;
    private String lastName;
    private String uuid;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     *
     * @return
     * The email
     */
    public String getEmail() {
        return email;
    }

    /**
     *
     * @param email
     * The email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     *
     * @return
     * The emailVerified
     */
    public Integer getEmailVerified() {
        return emailVerified;
    }

    /**
     *
     * @param emailVerified
     * The emailVerified
     */
    public void setEmailVerified(Integer emailVerified) {
        this.emailVerified = emailVerified;
    }

    /**
     *
     * @return
     * The emailVerifiedDate
     */
    public Long getEmailVerifiedDate() {
        return emailVerifiedDate;
    }

    /**
     *
     * @param emailVerifiedDate
     * The emailVerifiedDate
     */
    public void setEmailVerifiedDate(Long emailVerifiedDate) {
        this.emailVerifiedDate = emailVerifiedDate;
    }

    /**
     *
     * @return
     * The mobile
     */
    public String getMobile() {
        return mobile;
    }

    /**
     *
     * @param mobile
     * The mobile
     */
    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    /**
     *
     * @return
     * The mobileVerified
     */
    public Integer getMobileVerified() {
        return mobileVerified;
    }

    /**
     *
     * @param mobileVerified
     * The mobileVerified
     */
    public void setMobileVerified(Integer mobileVerified) {
        this.mobileVerified = mobileVerified;
    }

    /**
     *
     * @return
     * The mobileVerifiedDate
     */
    public Long getMobileVerifiedDate() {
        return mobileVerifiedDate;
    }

    /**
     *
     * @param mobileVerifiedDate
     * The mobileVerifiedDate
     */
    public void setMobileVerifiedDate(Long mobileVerifiedDate) {
        this.mobileVerifiedDate = mobileVerifiedDate;
    }

    /**
     *
     * @return
     * The firstName
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     *
     * @param firstName
     * The firstName
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     *
     * @return
     * The lastName
     */
    public String getLastName() {
        return lastName;
    }

    /**
     *
     * @param lastName
     * The lastName
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     *
     * @return
     * The uuid
     */
    public String getUuid() {
        return uuid;
    }

    /**
     *
     * @param uuid
     * The uuid
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
