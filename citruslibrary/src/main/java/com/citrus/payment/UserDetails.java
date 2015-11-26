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
package com.citrus.payment;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * @deprecated in v3
 * <p/>
 * Use {@link com.citrus.sdk.CitrusUser} instead.
 */
@Deprecated
public class UserDetails {
    JSONObject user;

    public UserDetails(JSONObject user) {
        this.user = user;
    }

    public String getFirstname() {
        try {
            return user.getString("firstName");
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getLastname() {
        try {
            return user.getString("lastName");
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getEmail() {
        try {
            return user.getString("email");
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getMobile() {
        try {
            return user.getString("mobileNo");
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getStreet1() {
        try {
            return user.getString("street1");
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getStreet2() {
        try {
            return user.getString("street2");
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getState() {
        try {
            return user.getString("state");
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getCity() {
        try {
            return user.getString("city");
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getCountry() {
        try {
            return user.getString("country");
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getZip() {
        try {
            return user.getString("zip");
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }


}