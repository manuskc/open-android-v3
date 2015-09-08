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

package com.citrus.retrofit;

import com.citrus.sdk.Constants;
import com.orhanobut.logger.Logger;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.HashSet;

import de.greenrobot.event.EventBus;
import eventbus.CookieEvents;

/**
 * Created by MANGESH KADAM on 5/15/2015.
 */
public class ReceivedCookiesInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Response originalResponse = chain.proceed(chain.request());
       // Logger.d("PREPAID COOKIE**" + originalResponse.header("prepaiduser-payauth"));
        if (!originalResponse.headers("Set-Cookie").isEmpty()) {
            HashSet<String> cookies = new HashSet<>();
            for (String header : originalResponse.headers("Set-Cookie")) {
                    if(header.contains(Constants.HEADER_PREPAID_COOKIE)) {
                        Logger.d("PREPAID COOKIE**" + header);
                        EventBus.getDefault().post(new CookieEvents(header));
                    }

            }


        }
        else {
            EventBus.getDefault().post(new CookieEvents(null));
        }
        RetroFitClient.removeInterCeptor();
        return originalResponse;
    }
}
