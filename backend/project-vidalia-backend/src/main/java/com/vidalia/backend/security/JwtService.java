package com.vidalia.backend.security;
import com.vidalia.backend.model.Role;
import com.vidalia.backend.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Service
public class JwtService {

	private static final String CLAIM_USER_ID = "uid";
	private static final String CLAIM_ROLE = "role";
	private static final String CLAIM_TOKEN_TYPE = "type";
	private static final String ACCESS_TOKEN_TYPE = "access";
	private static final String REFRESH_TOKEN_TYPE = "refresh";

	// Provide defaults so the application context can start when no profile-specific
	// properties file is loaded (e.g. some unit tests use the default profile).
	@Value("${app.security.jwt.secret:bG9jYWwtZGV2LWp3dC1zZWNyZXQtbXVzdC1iZS1yZXBsYWNlZC0yMDI2}")
	private String jwtSecret;

	@Value("${app.security.jwt.access-token-expiration-ms:900000}")
	private long accessTokenExpirationMs;

	@Value("${app.security.jwt.refresh-token-expiration-ms:2592000000}")
	private long refreshTokenExpirationMs;

	private SecretKey signingKey;

	@PostConstruct
	void init() {
		this.signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
	}

	public String generateAccessToken(User user) {
		return generateToken(user, ACCESS_TOKEN_TYPE, accessTokenExpirationMs);
	}

	public String generateRefreshToken(User user) {
		return generateToken(user, REFRESH_TOKEN_TYPE, refreshTokenExpirationMs);
	}

	public String extractEmail(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	public UUID extractUserId(String token) {
		String rawUserId = extractClaim(token, claims -> claims.get(CLAIM_USER_ID, String.class));
		return rawUserId == null ? null : UUID.fromString(rawUserId);
	}

	public Role extractRole(String token) {
		String rawRole = extractClaim(token, claims -> claims.get(CLAIM_ROLE, String.class));
		return rawRole == null ? null : Role.valueOf(rawRole);
	}

	public boolean isAccessToken(String token) {
		return ACCESS_TOKEN_TYPE.equals(extractClaim(token, claims -> claims.get(CLAIM_TOKEN_TYPE, String.class)));
	}

	public boolean isRefreshToken(String token) {
		return REFRESH_TOKEN_TYPE.equals(extractClaim(token, claims -> claims.get(CLAIM_TOKEN_TYPE, String.class)));
	}

	public boolean isTokenValid(String token, User user) {
		return isAccessTokenValid(token, user);
	}

	public boolean isAccessTokenValid(String token, User user) {
		return isTokenValid(token, user, ACCESS_TOKEN_TYPE);
	}

	public boolean isRefreshTokenValid(String token, User user) {
		return isTokenValid(token, user, REFRESH_TOKEN_TYPE);
	}

	private boolean isTokenValid(String token, User user, String expectedTokenType) {
		try {
			String email = extractEmail(token);
			String tokenType = extractClaim(token, claims -> claims.get(CLAIM_TOKEN_TYPE, String.class));
			return email != null
					&& user != null
					&& user.getEmail() != null
					&& email.equalsIgnoreCase(user.getEmail())
					&& expectedTokenType.equals(tokenType)
					&& !isTokenExpired(token);
		} catch (RuntimeException exception) {
			return false;
		}
	}

	public boolean isTokenExpired(String token) {
		Date expiration = extractClaim(token, Claims::getExpiration);
		return expiration.before(Date.from(Instant.now()));
	}

	public <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
		Claims claims = extractAllClaims(token);
		return claimResolver.apply(claims);
	}

	private String generateToken(User user, String tokenType, long expirationMs) {
		Map<String, Object> claims = new HashMap<>();
		claims.put(CLAIM_USER_ID, user.getId() == null ? null : user.getId().toString());
		claims.put(CLAIM_ROLE, user.getRole() == null ? null : user.getRole().name());
		claims.put(CLAIM_TOKEN_TYPE, tokenType);

		Instant now = Instant.now();
		Instant expiration = now.plusMillis(expirationMs);

		return Jwts.builder()
				.claims(claims)
				.subject(user.getEmail())
				.issuedAt(Date.from(now))
				.expiration(Date.from(expiration))
				.signWith(signingKey)
				.compact();
	}

	private Claims extractAllClaims(String token) {
		return Jwts.parser()
				.verifyWith(signingKey)
				.build()
				.parseSignedClaims(token)
				.getPayload();
	}
}
