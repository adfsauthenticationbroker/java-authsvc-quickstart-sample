package com.example.restservice;

import java.util.concurrent.atomic.AtomicLong;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.XML;
import org.springframework.util.Base64Utils;
import java.text.DateFormat;  
import java.text.SimpleDateFormat;  
import java.util.Date;  
import java.util.Calendar;  
import java.util.UUID;
import java.util.Base64;


import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;

import io.jsonwebtoken.*;

import java.util.Date;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Claims;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.crypto.DefaultJwtSignatureValidator;

import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

import static io.jsonwebtoken.SignatureAlgorithm.HS256;
import java.util.Locale;
import java.util.TimeZone;




@RestController
public class GreetingController {

	// sample code
	// display login link
	@GetMapping("/")
	public String index() {
			// TODO
			// if you are using angularJS, you can return this URL as Json and use javascript to redirect at client side
			String html = "<html><head><style>.topnav {  background-color: #333;  overflow: hidden;} .topnav a {  float: left;  color: #f2f2f2;  text-align: center;  padding: 14px 16px;  text-decoration: none;  font-size: 17px;}.topnav a:hover {  background-color: #ddd;  color: black;}.topnav a.active {  background-color: #04AA6D;  color: white;}</style></head><body><div class='topnav'><a href='http://localhost:8080/login'>Login</a></div></body></html>";
			return html;
	}

	// sample code
	// format query string to redirect to Auth Svc login url
	@GetMapping("/login")
	public String login() {

		// variables
		String authSvcUrl = "https://localhost:8111/api/users/login"; // TODO: replace with actual authsvc url
		String tenantId = "f98188a6-88cb-4663-a2b4-46e4335969dc"; // TODO: replace with your tenant id
		String redirectUri = "http://localhost:8080/login/callback"; // TODO: replace with your callback url

		// generate state
		String sessionId = UUID.randomUUID().toString(); // TODO: assign your current state here
		String state = sessionId;

		// generate nonce salt time
		Date date = Calendar.getInstance().getTime();  // ensure this time is GMT+0800 Singapore time
        DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss", Locale.ENGLISH);  // datetime must be in this format.
		TimeZone tz = java.util.TimeZone.getTimeZone("Asia/Singapore"); // GMT+0800 Singapore time
		dateFormat.setTimeZone(tz);
        String strDate = dateFormat.format(date);
		String nonce = strDate + "." + sessionId; // e.g. salt + sessionid TODO: replace with your ramdom nounce

		// encode variables in base64
		Base64.Encoder encoder = Base64.getEncoder();
		String encodeTenantId = encoder.encodeToString(tenantId.getBytes());
		String encodeState=encoder.encodeToString(state.getBytes());
		String encodeRedirectUri = encoder.encodeToString(redirectUri.getBytes());
		String encodeNonce = encoder.encodeToString(nonce.getBytes());


		String redirectUrl = authSvcUrl + "?" +
							"tenantid=" + encodeTenantId + "&" +
							"redirecturi=" + encodeRedirectUri +"&" +
							"nonce=" + encodeNonce +"&" +
							"state=" + encodeState;

		System.out.println("auth svc url:  " + redirectUrl);

		if(tenantId != "") {
			// TODO
			// if you are using angularJS, you can return this URL as Json and use javascript to redirect at client side
			// String html = "<html><head></head><body><script language=javascript>window.location.replace('" + redirectUrl + "')</script></body></html>";
			String html = "<html><head></head><body><script language=javascript>window.location.href='" + redirectUrl + "';</script></body></html>";
			return html;
		}
		else {
			return "Error login: Invalid tenandId";
		}
	}

	// sample code
	// Decode JWTResponse - token post from Auth Svc
	@PostMapping("/login/callback")
	public String loginCallback(@RequestParam(value = "token", defaultValue = "") String token) {
		System.out.println("tokenResponse Response:  " + token);

		String secretKey = "[SECRET USED TO SIGN AND VERIFY JWT TOKENS, IT CAN BE ANY STRING]"; // TODO: change to the public key from authsvc

		// check jwt token signature and expiration
		//if (verifyJWTToken(token, secretKey)) // TODO: uncomment once you have the public key from authsvc
		//{

			java.util.Base64.Decoder decoder = java.util.Base64.getUrlDecoder();
			String[] parts = token.split("\\."); // split out the "parts" (header, payload and signature)

			String headerJson = new String(decoder.decode(parts[0]));
			String payloadJson = new String(decoder.decode(parts[1]));
			System.out.println("headerJson Response:  " + headerJson);
			System.out.println("payloadJson Response:  " + payloadJson);

			JSONObject jsonObject= new JSONObject(payloadJson );
			String tenantid = jsonObject.getString("tenantid");
			String userid = jsonObject.getString("userid");
			String emailaddress = jsonObject.getString("emailaddress");
			String givenname = jsonObject.getString("givenname");
			String surname = jsonObject.getString("surname");
			String displayname = jsonObject.getString("displayname");
			String nonce = jsonObject.getString("nonce");
			String state = jsonObject.getString("state");
			String redirecturi = jsonObject.getString("redirecturi");

			String jsonPrettyPrintString = "" +
					" {userid: '" + userid + "'," +
					" emailaddress: '" + emailaddress + "'," +
					" givenname: '" + givenname  + "'," +
					" surname: '" + surname  + "'," +
					" displayname: '" + displayname  + "'," +
					" tenantid: '" + tenantid  + "'," +
					" nonce: '" + nonce  + "'," +
					" state: '" + state  + "'," +
					" redirecturi: '" + redirecturi  + "'}";

			// TODO 1: check referer is from Auth Svc
			// TODO 2: check the state in the token is a valid state in your application
			// TODO 3:
			// create you own application login token hereusing the token or the information you get from Auth Svc
			// redirect to the page you want after login
			String html = "userid: " + userid + "<br/>emailaddress: " + emailaddress  + "<br/>givenname: " + givenname  + "<br/>surname: " + surname + "<br/>displayname: " + displayname  + "<br/>tenantid: " + tenantid + "<br/>nonce: " + nonce + "<br/>state: " + state + "<br/>redirecturi: " + redirecturi ;
			//return "Java Spring Boot - Login successfully. <br/>" + jsonPrettyPrintString;
			return "<b>Java Spring Boot - Login successfully.</b> <br/><br/><b>Token Information:</b><br/>" + html;
		//}
		//else
		//{
		//	return "jwt token is not valid.";
		//}
	}

	public static boolean verifyJWTToken(String token, String secretKey)  {
		// variables
		Base64.Decoder decoder = Base64.getDecoder();
		String[] chunks = token.split("\\.");
		String header = new String(decoder.decode(chunks[0]));
		String payload = new String(decoder.decode(chunks[1]));
		String tokenWithoutSignature = chunks[0] + "." + chunks[1];
		String signature = chunks[2];
		SignatureAlgorithm sa = HS256;
		SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(), sa.getJcaName());
		DefaultJwtSignatureValidator validator = new DefaultJwtSignatureValidator(sa, secretKeySpec);

		boolean isTokenValid = false;
		JSONObject payloadJSONObject = new JSONObject(payload);

		if (!validator.isValid(tokenWithoutSignature, signature)) // check signature
		{
			// throw new Exception("Could not verify JWT token integrity!");
			System.out.println("Could not verify JWT token integrity!");
			isTokenValid = false;
		}
		else if (!(payloadJSONObject.getLong("exp") > (System.currentTimeMillis() / 1000))) // check if token expired
		{
			// throw new Exception("Token expired!");
			System.out.println("token expired");
			isTokenValid = false;
		}
		else
		{
			System.out.println("validator.isValid: " + "true");
			isTokenValid = true;
		}

		System.out.println("Payload: " + payload);
		return isTokenValid;

	}


}
