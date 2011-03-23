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
import java.io.BufferedReader;
import java.io.StringReader;
import java.lang.String; 
import java.util.Date;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class Sfalma {

	// FIXME: Use Gson
	public static String createJSON(String app_package, String version, String phoneModel, String android_version, String stackTrace, String wifi_status, String mob_net_status, String gps_status, Date occuredAt) throws Exception {
		JSONObject json = new JSONObject();

		JSONObject request_json = new JSONObject();
		JSONObject exception_json = new JSONObject();
		JSONObject application_json = new JSONObject();
		JSONObject client_json = new JSONObject();
		
		request_json.put("remote_ip", " "); 
		json.put("request", request_json);

		// stackTrace contains many info we need to extract
		BufferedReader reader = new BufferedReader(new StringReader(stackTrace));
		
		if (occuredAt == null)
			exception_json.put("occured_at", reader.readLine()); 
		else
			exception_json.put("occured_at", occuredAt);
		exception_json.put("message", reader.readLine()); //get message

		String exception_class = reader.readLine();
		exception_json.put("where", exception_class.substring(exception_class.lastIndexOf("(") + 1, exception_class.lastIndexOf(")")));  

		exception_json.put("klass", getClass(stackTrace));
		exception_json.put("backtrace", stackTrace);

		json.put("exception", exception_json);
		
		reader.close();

		application_json.put("phone", phoneModel);
		application_json.put("appver", version);
		application_json.put("appname", app_package);
		application_json.put("osver", android_version); //os_ver
		application_json.put("wifi_on", wifi_status);
		application_json.put("mobile_net_on", mob_net_status);
		application_json.put("gps_on", gps_status);
		json.put("application_environment", application_json);

		client_json.put("version", "sfalma-version-0.6");
		client_json.put("name", "sfalma-android");
		json.put("client", client_json);

		return json.toString();
	}

	
    public static String MD5 (String data) throws Exception {
		MessageDigest m = MessageDigest.getInstance("MD5");

		m.update(data.getBytes(), 0, data.length());
		return new BigInteger(1, m.digest()).toString(16);
	}

	// FIXME: This need some optimizing
	public static String getClass(String in) {
		String out = "";
		int endOfFirstLine = in.indexOf("\n");
		if (endOfFirstLine != -1 && endOfFirstLine+1 < in.length() ) {
			out = in.substring(endOfFirstLine + 1);
		}
		return out;
	}

	public static void submitError(int sTimeout, Date occuredAt, final String stacktrace) throws Exception {
		// Transmit stack trace with POST request
		try {
			Log.d(G.TAG, "Transmitting stack trace: " + stacktrace);									

			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpParams params = httpClient.getParams();

			// Lighty 1.4 has trouble with the expect header
			// (http://redmine.lighttpd.net/issues/1017), and a
			// potential workaround is only included in 1.4.21
			// (http://www.lighttpd.net/2009/2/16/1-4-21-yes-we-can-do-another-release).
			HttpProtocolParams.setUseExpectContinue(params, false);
			if (sTimeout != 0) {
				HttpConnectionParams.setConnectionTimeout(params, sTimeout);
				HttpConnectionParams.setSoTimeout(params, sTimeout);
			}
		
			HttpPost httpPost = new HttpPost(G.URL);
			httpPost.addHeader("X-Sfalma-Api-Key", G.API_KEY);
		
			List <NameValuePair> nvps = new ArrayList <NameValuePair>();
			nvps.add(new BasicNameValuePair("data", createJSON(G.APP_PACKAGE, G.APP_VERSION, G.PHONE_MODEL, G.ANDROID_VERSION, stacktrace, SfalmaHandler.isWifiOn(), SfalmaHandler.isMobileNetworkOn(), SfalmaHandler.isGPSOn(), occuredAt)));
			nvps.add(new BasicNameValuePair("hash", MD5(stacktrace)));
		
			httpPost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
			// We don't care about the response, so we just hope it
			// went well and on with it.
			httpClient.execute(httpPost);
		} catch (Exception e) {
			Log.e(G.TAG, "Error sending exception stacktrace", e);
			throw e;
		}
	}
}