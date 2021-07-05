package com.common.util;

import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator.Builder;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.impl.PublicClaims;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TokenUtil {
    /*
     * Config
     */
    // public static final String WILDCARD_DOMAIN = "*.dogy.xyz";
    @Value("${jwt.token.key}")
    public static final String    SECRET_KEY_FOR_JWT_TOKEN        = "72906GBzVeH8v9Phttj48QYs9456G291";
//    public static final String    SECRET_KEY_FOR_JWT_TOKEN        = StringUtil.randomString(32);
    /*
     * HTTP HEADER
     */
    public static final String    HTTP_HEADER_VALUE_TOKEN_PREFIX  = "Bearer ";
    public static final String    HTTP_HEADER_KEY_JWT             = "Authorization";
    public static final String    HTTP_PARAM_KEY_JWT              = "auth";
    /*
     * JWT HEADER
     */
    public static final String    JWT_HEADER_VALUE_JWT            = "JWT";
    public static final String    JWT_HEADER_VALUE_HS512          = "HS512";
    public static final String    JWT_HEADER_VALUE_HS256          = "HS256";
    public static final String    JWT_HEADER_KEY_ALG              = "alg";
    public static final String    JWT_HEADER_KEY_TYP              = "typ";
    /*
     * PAYLOAD Key
     */
    public static final String    JWT_PAYLOAD_USERNAME            = "username";
    public static final String    JWT_PAYLOAD_SESSION_ID          = "sessionId";
    public static final String    JWT_PAYLOAD_ROLE                = "role";
    public static final String    JWT_PAYLOAD_PRIV                = "privilege";
    /*
     * Token key
     */
    public final static String    ACCESS_TOKEN_NAME               = "accessToken";
    public final static String    REFRESH_TOKEN_NAME              = "refreshToken";
    public final static String    ATTRIBUTE_TOKEN_NAME            = "token";
    /*
     * Token Validation Second (ms)
     */
    public final static Long      ACCESS_TOKEN_VALIDATION_SECOND  = 1000L * 60;
    public final static Long      REFRESH_TOKEN_VALIDATION_SECOND = 1000L * 60 * 60 * 24 * 7;
    /*
     * public Claims array
     */
    private static final String[] PUBLIC_CLAIMS                   = { PublicClaims.ISSUER, PublicClaims.SUBJECT, PublicClaims.EXPIRES_AT, PublicClaims.NOT_BEFORE, PublicClaims.ISSUED_AT, PublicClaims.JWT_ID, PublicClaims.AUDIENCE };
    /*
     * verifier
     */
    private static JWTVerifier    verifier                        = null;

    public static String makeAccessToken(Map<String, Object> claims) {
        return makeJWT(claims, ACCESS_TOKEN_VALIDATION_SECOND);
    }

    public static String makeRefreshToken(Map<String, Object> claims) {
        return makeJWT(claims, REFRESH_TOKEN_VALIDATION_SECOND);
    }

    public static String makeJWT(Map<String, Object> claims, Long expiresAt) {
        String token = null;
        try {
            Map<String, Object> headerClaims = new HashMap<String, Object>();
            headerClaims.put(JWT_HEADER_KEY_ALG, JWT_HEADER_VALUE_HS512);
            headerClaims.put(JWT_HEADER_KEY_TYP, JWT_HEADER_VALUE_JWT);
            Builder builder = JWT.create()
                    .withHeader(headerClaims)
                    .withIssuedAt(new Date(System.currentTimeMillis()))
                    .withExpiresAt(new Date(System.currentTimeMillis() + expiresAt));
            Set<String> keys = claims.keySet();
            for (String key : keys) {
                if (claims.get(key) instanceof Map) {
                    builder.withClaim(key, ObjectUtil.writeValueAsString(claims.get(key)));
                } else {
                    builder.withClaim(key, StringUtil.nvlObj(claims.get(key)));
                }
            }
            token = builder.sign(Algorithm.HMAC512(SECRET_KEY_FOR_JWT_TOKEN));
        } catch (IllegalArgumentException | IOException e) {
            log.debug(e.getMessage());
        }
        return token;
    }

    public static Optional<String> extractJWT(HttpServletRequest request) {
        String bearerToken = StringUtil.replaceAll(request.getHeader(HTTP_HEADER_KEY_JWT), HTTP_HEADER_VALUE_TOKEN_PREFIX, ""); // Authorization=Bearer {}
        String queryStrToken = request.getParameter(HTTP_PARAM_KEY_JWT);// auth={}
        String jwt = StringUtil.getValidString(bearerToken, queryStrToken);
        if (StringUtil.isEmpty(jwt)) {
            return Optional.empty();
        }
        return Optional.of(jwt);
    }

    public static Optional<String> verifyJWT(String token) {
        if (verifier == null) {
            Algorithm algorithm = null;
            algorithm = Algorithm.HMAC512(SECRET_KEY_FOR_JWT_TOKEN);
            verifier = JWT.require(algorithm)
                    // .withIssuer(WILDCARD_DOMAIN)
                    .build(); // Reusable verifier instance
        }
        DecodedJWT jwt = verifier.verify(token);
        return Optional.ofNullable(jwt.getClaim(JWT_PAYLOAD_USERNAME).asString());
    }
    
    public static String getClaim(String token, String claim) {
        return getClaims(token).getClaim(claim).asString();
    }

    public static DecodedJWT getClaims(String token) {
        return JWT.decode(token);
    }

    public static Map<String, Object> getPayload(String token) {
        Map<String, Object> claims = new HashMap<>();
        for (Map.Entry<String, Claim> entry : getClaims(token).getClaims().entrySet()) {
            if (StringUtil.notIn(entry.getKey(), PUBLIC_CLAIMS)) {
                claims.put(entry.getKey(), entry.getValue().asString());
            }
        }
        return claims;
    }
    
    public static String[] getRoles(String token) {
        try {
            return JWT.decode(token).getClaim(JWT_PAYLOAD_ROLE).asArray(String.class);
        } catch (Exception e) {
            return new String[0];
        }
    }
    
    public static String[] getPrivileges(String token) {
        try {
            return JWT.decode(token).getClaim(JWT_PAYLOAD_PRIV).asArray(String.class);
        } catch (Exception e) {
            return new String[0];
        }
    }

    
    public static Collection<? extends GrantedAuthority> getAuthorities(String token) {
        try {
            String[] roles = JWT.decode(token).getClaim(JWT_PAYLOAD_ROLE).asArray(String.class);
            return roles.length == 0 || roles == null
                    ? Collections.emptyList()
                    : Arrays.stream(roles).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
    
    public static String getSessionId(String token) {
        return getClaim(token, JWT_PAYLOAD_SESSION_ID);
    }

    /**
     * [경고] Web 요청에 한해 Controller layer에서 만 쓸것
     *
     * @return
     */
    public static String getToken() {
        HttpServletRequest request = SessionUtil.getRequest();
        return getToken(request);
    }

    public static String getToken(HttpServletRequest request) {
        String jwt = StringUtil.nvlObj(request.getAttribute(ATTRIBUTE_TOKEN_NAME), request.getHeader(HTTP_HEADER_KEY_JWT));
        return StringUtil.nvl(jwt).replaceAll(HTTP_HEADER_VALUE_TOKEN_PREFIX, "");
    }

    public static String getJson(String token) {
        Decoder decoder = Base64.getUrlDecoder();
        String[] parts = token.split("\\."); // split out the "parts" (header, payload and signature)
        String headerJson = new String(decoder.decode(parts[0]));
        String payloadJson = new String(decoder.decode(parts[1]));
        String signatureJson = new String(decoder.decode(parts[2]));
        try {
            return ObjectUtil.writeValueAsStringPretty(ObjectUtil.readValueToMap(headerJson)) + "\n"
                    + ObjectUtil.writeValueAsStringPretty(ObjectUtil.readValueToMap(payloadJson)) + "\n" + signatureJson;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return headerJson + payloadJson + signatureJson;
    }
    
    public static String getUsername() {
        return getUsername(getToken());
    }
    public static String getUsername(String token) {
        return getClaim(token, JWT_PAYLOAD_USERNAME);
    }
    public static long getIssuedAt() {
        return getIssuedAt(getToken());
    }
    public static long getIssuedAt(String token) {
        return getClaims(token).getIssuedAt().getTime();
    }
    public static long getExpiresAt() {
        return getExpiresAt(getToken());
    }
    public static long getExpiresAt(String token) {
        return getClaims(token).getExpiresAt().getTime();
    }

    public static long getVaildAt() {
        return getVaildAt(getToken());
    }
    public static long getVaildAt(String token) {
        return getExpiresAt(token) - getIssuedAt(token);
    }
}
