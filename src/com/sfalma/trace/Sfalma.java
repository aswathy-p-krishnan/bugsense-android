/*
Copyright (c) 2011 Sfalma.com

Permission is hereby granted, free of charge, to any person
obtaining a copy of this software and associated documentation
files (the "Software"), to deal in the Software without
restriction, including without limitation the rights to use,
copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the
Software is furnished to do so, subject to the following
conditions:

The above copyright notice and this permission notice shall be
included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
OTHER DEALINGS IN THE SOFTWARE.

Contributors:
Jon Vlachoyiannis
 */

package com.sfalma.trace;

import java.io.IOException;
import java.security.*;
import java.math.*;
 
import org.apache.http.NameValuePair;

import org.json.JSONException;
import org.json.JSONObject;

public class Sfalma {
	// FIXME: Use Gson
	public static String createJSON(String app_package, String version, String phoneModel, String android_version, String stackTrace) throws Exception {
		JSONObject json = new JSONObject();

		JSONObject request_json = new JSONObject();
		JSONObject exception_json = new JSONObject();
		JSONObject application_json = new JSONObject();
		JSONObject client_json = new JSONObject();
		
		request_json.put("remote_ip", "192.168.0.1");
		json.put("request", request_json);

		exception_json.put("occured_at", "today");
		exception_json.put("message", "java.lang.NullPointerException");
		exception_json.put("exception_class", "java.lang.NullPointerException");
		exception_json.put("backtrace", stackTrace);
		json.put("exception", exception_json);
		
		application_json.put("phone_model", phoneModel);
		application_json.put("package_version", version);
		application_json.put("package_name", app_package);
		application_json.put("version", android_version);
		json.put("application_environment", application_json);

		client_json.put("version", "sfalma-version-1");
		client_json.put("name", "sfalma-android");
		json.put("client", client_json);

		return json.toString();
	}

	
    public static String MD5 (String data) throws Exception {
		MessageDigest m = MessageDigest.getInstance("MD5");

		m.update(data.getBytes(), 0, data.length());
		return new BigInteger(1, m.digest()).toString(16);
	}

}