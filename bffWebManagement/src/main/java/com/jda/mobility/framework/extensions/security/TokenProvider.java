package com.jda.mobility.framework.extensions.security;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jda.mobility.framework.extensions.config.AppProperties;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

/**
 * This Class provide implementation for generating/validating/extracting
 * information from jwt tokens.
 */
@Component
public class TokenProvider {

    private static final Logger LOGGER = LogManager.getLogger(TokenProvider.class);

    private AppProperties appProperties;

    /**
     * @param appProperties
     */
    public TokenProvider(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    /**
     * This method is used to create token- Only when Basic Authentication is enabled.
     */
    public String createToken(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + appProperties.getAuth().getTokenExpirationMsec());
        return Jwts.builder()
                .setSubject(userPrincipal.getUserId())
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .setAudience(appProperties.getAuth().getClientId())
                .signWith(SignatureAlgorithm.HS512, appProperties.getAuth().getTokenSecret())
                .compact();
    }
    /**
     * extract claims from JWT token- Only when Basic Authentication is enabled.
     */
    public Claims getClaimFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(appProperties.getAuth().getTokenSecret())
                .parseClaimsJws(token)
                .getBody();
      
        return claims;
    }
    /**
     * The method implementation provides extraction of userId from JWT token
     * - Only when Basic Authentication is enabled.
     */
    public String getUserIdFromToken(String token) {
        return getClaimFromToken(token).getSubject();
    }

    /**
     * This method is used to validateToken
     * - Only when Basic Authentication is enabled.
     */
    public boolean validateToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(appProperties.getAuth().getTokenSecret()).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException ex) {
            LOGGER.error("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            LOGGER.error("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            LOGGER.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            LOGGER.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            LOGGER.error("JWT claims string is empty.");
        }
        return false;
    }
    
    /**
     * Common method for any type of JWT Auth.
     * @param openIdToken
     * @return Map&lt;String, String&gt;
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
	public Map<String, String> getAuthMapFromToken(String openIdToken) throws IOException {
    	Map<String, String> authInfo = null;		
		if(openIdToken != null) {
			final Jwt tokenDecoded = JwtHelper.decode(openIdToken);
			authInfo = new ObjectMapper().readValue(tokenDecoded.getClaims(), Map.class);
		}		
		return authInfo;
	}
    
    /**
     * Gets JWT Token from Http Request.
     * @param request
     * @return String
     */
	public String getJwtFromRequest(HttpServletRequest request) {
		String bearerToken = request.getHeader(BffAdminConstantsUtils.AUTHORIZATION);
		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BffAdminConstantsUtils.BEARER)) {
			return bearerToken.substring(7, bearerToken.length());
		}
		return null;
	}

    /**
     * Gets Bearer JWT Token from Http Request.
     * @param request
     * @return String
     */
	public String getBearerJwtFromRequest(HttpServletRequest request) {
		String bearerToken = request.getHeader(BffAdminConstantsUtils.AUTHORIZATION);
		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BffAdminConstantsUtils.BEARER)) {
			return bearerToken;
		}
		return null;
	}

}
