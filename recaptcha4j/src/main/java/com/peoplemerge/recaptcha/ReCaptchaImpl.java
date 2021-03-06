/*
 * Copyright 2007 Soren Davidsen, Tanesha Networks
 *  
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 *    
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.peoplemerge.recaptcha;

import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.Properties;

import com.peoplemerge.recaptcha.http.HttpLoader;
import com.peoplemerge.recaptcha.http.SimpleHttpLoader;
import org.json.JSONObject;

public class ReCaptchaImpl implements ReCaptcha {

	public static final String PROPERTY_THEME = "theme";
	public static final String PROPERTY_TABINDEX = "tabindex";
	
	public static final String HTTP_SERVER = "http://www.google.com/recaptcha";
	public static final String HTTPS_SERVER = "https://www.google.com/recaptcha";
	public static final String VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify";
	
	private String privateKey;
	private String publicKey;
	private String recaptchaServer = HTTPS_SERVER;
    private String verifyUrl = VERIFY_URL;
	private HttpLoader httpLoader = new SimpleHttpLoader();
	
	public void setPrivateKey(String privateKey) {
		this.privateKey = privateKey;
	}
	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}
	public void setRecaptchaServer(String recaptchaServer) {
		this.recaptchaServer = recaptchaServer;
	}
    public void setVerifyUrl(String verifyUrl) {
        this.verifyUrl = verifyUrl;
    }
    public void setHttpLoader(HttpLoader httpLoader) {
		this.httpLoader  = httpLoader;
	}

	public ReCaptchaResponse checkAnswer(String remoteAddr, String response) {

		String postParameters = "secret=" + URLEncoder.encode(privateKey) + "&remoteip=" + URLEncoder.encode(remoteAddr) +
                "&response=" + URLEncoder.encode(response);

        final String message;
        try {
            message = httpLoader.httpPost(verifyUrl, postParameters);

            if (message == null) {
                return new ReCaptchaResponse(false, "recaptcha-not-reachable");
            }
        }
        catch (ReCaptchaException networkProblem) {
            return new ReCaptchaResponse(false, "recaptcha-not-reachable");
        }
        try {
            JSONObject json = new JSONObject(message);
            boolean isSuccessful = json.getBoolean("success");

            if (isSuccessful){
                return new ReCaptchaResponse(true, null);
            }else {
                return new ReCaptchaResponse(false, json.get("error-codes").toString());
            }

        }catch(Exception e){
            return new ReCaptchaResponse(false, "recaptcha-not-valid: " + e);
        }

	}

    /**
     * Source: https://developers.google.com/recaptcha/docs/display
     *
     * @param errorMessage An errormessage to display in the captcha, null if none.
     * @param options Options for rendering, <code>tabindex</code> and <code>theme</code> are currently supported by recaptcha. You can
     *   put any options here though, and they will be added to the RecaptchaOptions javascript array.
     * @return
     */
	public String createRecaptchaHtml(String errorMessage, Properties options) throws ReCaptchaException {

		throw new ReCaptchaException ("Not implemented yet. Pull requests welcome. https://developers.google.com/recaptcha/docs/display says this is as simple as: " +
                "<html>\n" +
                "  <head>\n" +
                "     <script src=\"https://www.google.com/recaptcha/api.js\" async defer></script>\n" +
                "  </head>\n" +
                "  <body>\n" +
                "    <form action=\"?\" method=\"POST\">\n" +
                "      <div class=\"g-recaptcha\" data-sitekey=\""+publicKey +"\"></div>\n" +
                "      <br/>\n" +
                "      <input type=\"submit\" value=\"Submit\">\n" +
                "    </form>\n" +
                "  </body>\n" +
                "</html>");
		

	}
	
	public String createRecaptchaHtml(String errorMessage, String theme, Integer tabindex) throws ReCaptchaException  {

		Properties options = new Properties();

		if (theme != null) {
			options.setProperty(PROPERTY_THEME, theme);
		}
		if (tabindex != null) {
			options.setProperty(PROPERTY_TABINDEX, String.valueOf(tabindex));
		}

		return createRecaptchaHtml(errorMessage, options);
	}

	/**
	 * Produces javascript array with the RecaptchaOptions encoded.
	 * 
	 * @param properties
	 * @return
	 */
	private String fetchJSOptions(Properties properties) {

		if (properties == null || properties.size() == 0) {
			return "";
		}

		String jsOptions =
			"<script type=\"text/javascript\">\r\n" + 
			"var RecaptchaOptions = {";
			
		for (Enumeration e = properties.keys(); e.hasMoreElements(); ) {
			String property = (String) e.nextElement();
			
			jsOptions += property + ":'" + properties.getProperty(property)+"'";
			
			if (e.hasMoreElements()) {
				jsOptions += ",";
			}
			
		}

		jsOptions += "};\r\n</script>\r\n";

		return jsOptions;
	}
}
