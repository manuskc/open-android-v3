/*
   Copyright 2015 Citrus Payment Solutions Pvt. Ltd.
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

package com.citrus.sdk;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by salil on 16/2/15.
 */
public final class CitrusUser implements Parcelable {

    public static final CitrusUser DEFAULT_USER = new CitrusUser();

    public static final Creator<CitrusUser> CREATOR = new Creator<CitrusUser>() {
        public CitrusUser createFromParcel(Parcel source) {
            return new CitrusUser(source);
        }

        public CitrusUser[] newArray(int size) {
            return new CitrusUser[size];
        }
    };
    private String firstName = null;
    private String lastName = null;
    private String emailId = null;
    private String mobileNo = null;
    private Address address = null;

    private CitrusUser() {

    }

    public CitrusUser(String emailId, String mobileNo) {
        this.emailId = emailId;
        this.mobileNo = mobileNo;
    }

    public CitrusUser(String emailId, String mobileNo, String firstName, String lastName, Address address) {
        this.emailId = emailId;
        this.mobileNo = mobileNo;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
    }

    private CitrusUser(Parcel in) {
        this.firstName = in.readString();
        this.lastName = in.readString();
        this.emailId = in.readString();
        this.mobileNo = in.readString();
        this.address = in.readParcelable(Address.class.getClassLoader());
    }

    public static CitrusUser fromJSON(String json) {
        JSONObject jsonObject = null;

        try {
            jsonObject = new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return fromJSONObject(jsonObject);
    }

    public static CitrusUser fromJSONObject(JSONObject response) {
        CitrusUser user = null;

        if (response != null) {
            String email = response.optString("email");
            String mobileNo = response.optString("mobileNo", response.optString("mobile"));
            String firstName = response.optString("firstName");
            String lastName = response.optString("lastName");
            Address address = Address.fromJSONObject(response);

            user = new CitrusUser(email, mobileNo, firstName, lastName, address);
        }

        return user;
    }

    public static JSONObject toJSONObject(CitrusUser user) {
        JSONObject customer = null;
        /*
         * All the below mentioned parameters are mandatory - missing anyone of them may create errors Do not change the
		 * key in the json below - only change the values
		 */

        try {
            customer = new JSONObject();

            // Check whether the values are set then set the given values, else set default values.

            if (user == null || TextUtils.isEmpty(user.firstName)) {
                customer.put("firstName", "Tester");
            } else {
                customer.put("firstName", user.firstName);
            }
            if (user == null || TextUtils.isEmpty(user.lastName)) {
                customer.put("lastName", "Citrus");
            } else {
                customer.put("lastName", user.lastName);
            }

            if (user == null || TextUtils.isEmpty(user.emailId)) {
                customer.put("email", "tester@gmail.com");
            } else {
                customer.put("email", user.emailId);
            }

            if (user == null || TextUtils.isEmpty(user.mobileNo)) {
                customer.put("mobileNo", "9999999999");
            } else {
                customer.put("mobileNo", user.mobileNo);
            }

            if (user == null || user.address == null || TextUtils.isEmpty(user.address.street1)) {
                customer.put("street1", "streetone");
            } else {
                customer.put("street1", user.address.street1);
            }

            if (user == null || user.address == null || TextUtils.isEmpty(user.address.street2)) {
                customer.put("street2", "streettwo");
            } else {
                customer.put("street2", user.address.street2);
            }

            if (user == null || user.address == null || TextUtils.isEmpty(user.address.city)) {
                customer.put("city", "Mumbai");
            } else {
                customer.put("city", user.address.city);
            }

            if (user == null || user.address == null || TextUtils.isEmpty(user.address.state)) {
                customer.put("state", "Maharashtra");
            } else {
                customer.put("state", user.address.state);
            }

            if (user == null || user.address == null || TextUtils.isEmpty(user.address.country)) {
                customer.put("country", "India");
            } else {
                customer.put("country", user.address.country);
            }

            if (user == null || user.address == null || TextUtils.isEmpty(user.address.zip)) {
                customer.put("zip", "400052");
            } else {
                customer.put("zip", user.address.zip);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return customer;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getEmailId() {
        return emailId;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Address getAddress() {
        return address;
    }

    @Override
    public String toString() {
        return "CitrusUser{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", emailId='" + emailId + '\'' +
                ", mobileNo='" + mobileNo + '\'' +
                ", address=" + address +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.firstName);
        dest.writeString(this.lastName);
        dest.writeString(this.emailId);
        dest.writeString(this.mobileNo);
        dest.writeParcelable(this.address, flags);
    }

    public static class Address implements Parcelable {

        public static final Creator<Address> CREATOR = new Creator<Address>() {
            public Address createFromParcel(Parcel source) {
                return new Address(source);
            }

            public Address[] newArray(int size) {
                return new Address[size];
            }
        };
        private String street1 = null;
        private String street2 = null;
        private String city = null;
        private String state = null;
        private String country = null;
        private String zip = null;

        public Address(String street1, String street2, String city, String state, String country, String zip) {
            this.street1 = normalizeString(street1);
            this.street2 = normalizeString(street2);
            this.city = normalizeString(city);
            this.state = normalizeString(state);
            this.country = normalizeString(country);
            this.zip = normalizeString(zip);
        }

        private Address(Parcel in) {
            this.street1 = in.readString();
            this.street2 = in.readString();
            this.city = in.readString();
            this.state = in.readString();
            this.country = in.readString();
            this.zip = in.readString();
        }

        public static Address fromJSONObject(JSONObject response) {
            Address address = null;

            if (response != null) {
                String addressCountry = response.optString("addressCountry");
                String addressState = response.optString("addressState");
                String addressCity = response.optString("addressCity");
                String addressStreet1 = response.optString("addressStreet1");
                String addressStreet2 = response.optString("addressStreet2");
                String addressZip = response.optString("addressZip");

                address = new Address(addressStreet1, addressStreet2, addressCity, addressState, addressCountry, addressZip);
            }

            return address;
        }

        public String getStreet1() {
            return street1;
        }

        public String getStreet2() {
            return street2;
        }

        public String getCity() {
            return city;
        }

        public String getState() {
            return state;
        }

        public String getCountry() {
            return country;
        }

        public String getZip() {
            return zip;
        }

        @Override
        public String toString() {
            return "Address{" +
                    "street1='" + street1 + '\'' +
                    ", street2='" + street2 + '\'' +
                    ", city='" + city + '\'' +
                    ", state='" + state + '\'' +
                    ", country='" + country + '\'' +
                    ", zip='" + zip + '\'' +
                    '}';
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.street1);
            dest.writeString(this.street2);
            dest.writeString(this.city);
            dest.writeString(this.state);
            dest.writeString(this.country);
            dest.writeString(this.zip);
        }
    }

    private static String normalizeString(String input) {
        return (!TextUtils.isEmpty(input) ? input.replaceAll("[\\p{Cntrl}^\r\n\t]+", "") : null);
    }
}