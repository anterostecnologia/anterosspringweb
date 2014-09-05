/**
 *    Copyright 2013 Autentia Real Business Solution S.L.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package br.com.anteros.springWeb.rest.wadl;

import javax.servlet.http.HttpServletRequest;

public final class HttpServletRequestUtils {

    private HttpServletRequestUtils() {
        // Utility class, so instances are not allowed.
    }

    public static String getBaseUrlOf(HttpServletRequest request) {
        return request.getScheme() + "://" + request.getServerName() + ':' + request.getServerPort()
                + request.getContextPath() + request.getServletPath();
    }

}
