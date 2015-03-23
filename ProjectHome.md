Recaptcha4j is a library to work with [reCAPTCHA](http://recaptcha.net/)'s implementation of a CAPTCHA API. The special about recaptcha is that their implementation picks words to help solving difficult OCR scanning processes so people can enjoy more books online. Thats quite a noble quest IMHO, so the more implementations of clients for their API, the more books we will get.

## Using ##

Before you can use the library, you need to get API keys from their website. Follow the link above to find those. After that you have to call the library from your code. You can do that something like this (example in JSP):

```
	...
<%
	// create recaptcha without <noscript> tags
	ReCaptcha captcha = ReCaptchaFactory.newReCaptcha("my-public-key", "my-private-key", false);
	String captchaScript = captcha.createRecaptchaHtml(request.getParameter("error"), null);
	
	out.print(captchaScript);
%>
	...
```

Checking the captcha goes like this:

```
	...
<%
	// create recaptcha without <noscript> tags
	ReCaptcha captcha = ReCaptchaFactory.newReCaptcha("my-public-key", "my-private-key", false);
	ReCaptchaResponse response = captcha.checkAnswer(request.getRemoteAddr(), request.getParameter("recaptcha_challenge_field"), request.getParameter("recaptcha_response_field"));

	if (response.isValid()) {
		out.print("Success");
	}
	else {
		out.print(response.getErrorMessage());
	}
%>
	...
```

There is a small example web-app in the SVN-repository which you can find here: https://svn.kokila.eu/repo/sandbox/recaptcha4j-example/trunk/.

## Using with Spring ##

If you are using the [Spring framework](http://www.springframework.org/) then you can configure the ReCaptcha as a bean in Spring quite easily, so you can use it for injecting into whatever other logic you might have. Instead of using the ReCaptchaFactory indirection, you should use the ReCaptchaImpl directly and then use the ReCaptcha interface in your sepending beans.

```
<beans>
	...
	<bean id="reCaptcha" class="net.tanesha.recaptcha.ReCaptchaImpl">
		<property name="privateKey" value="my private key" />
		<property name="publicKey" value="my public key" />
		<property name="includeNoscript" value="false" />
	</bean>
	...
</beans>
```

## Using with Maven2 ##

If you're using Maven2, you can include it as dependency in your project simply like this:

```
<project>
    ...
	<dependency>
		<groupId>net.tanesha.recaptcha4j</groupId>
		<artifactId>recaptcha4j</artifactId>
		<version>0.0.8-SNAPSHOT</version>
	</dependency>
    ...
    
</project>
```


## Problems and solutions ##

  * Java is little eager to cache, so you must set the System property `networkaddress.cache.ttl`. You can do that by passing `-Dnetworkaddress.cache.ttl=500` to your app-server, or by adding `System.setProperty("networkaddress.cache.ttl", "500");` somewhere in your code. See [java.sun.com properties guide](http://java.sun.com/j2se/1.5.0/docs/guide/net/properties.html) for more information.