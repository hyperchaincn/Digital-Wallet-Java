package com.hyperchain.wallet.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

public class JwtHelper {

    private static Key getKeyInstance() {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary("hyperchain");
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());
        return signingKey;
    }

    public static String createJavaWebToken(String address,String phone, String password) {
        return Jwts.builder()
                .claim("phone", phone)
                .claim("address",address)
                .claim("userid", password).signWith(SignatureAlgorithm.HS256, getKeyInstance()).compact();
    }

    public static Map<String, Object> parserJavaWebToken(String jwt) {
        try {
            Map<String, Object> jwtClaims =
                    Jwts.parser().setSigningKey(getKeyInstance()).parseClaimsJws(jwt).getBody();
            return jwtClaims;
        } catch (Exception e) {
            return null;
        }
    }


    public static void main(String[] args) {
        String token = "Basic ZXlKaGJHY2lPaUpJVXpJMU5pSjkuZXlKcFpDSTZJakU0TnpBeU5qQTBOemt6SWl3aWNHaHZibVVpT2lJeE9EY3dNall3TkRjNU15SXNJblZ6WlhKcFpDSTZJbTFsTVRJek5EVTJJbjAuUG9rR3RPRkRQWUJLcjMyeXVLRDNmVGFKdGpMZ0dZa0RMLXM3cXR1YkhHTTo=";
        token = token.substring(6);
        final Base64.Decoder decoder = Base64.getDecoder();
        try {
            token = new String(decoder.decode(token), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        token = token.split(":")[0];
        Map<String, Object>  map = JwtHelper.parserJavaWebToken(token);
        System.out.println(map);
    }
}
