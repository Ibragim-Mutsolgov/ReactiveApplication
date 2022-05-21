package lets.code.reactor.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lets.code.reactor.pages.authorize.domain.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JWTUtil { // Тут я не смогу все описать

    @Value("${jwt.secret}")
    private String secret; // Секретка
    @Value("${jwt.expiration}")
    private String expireTimeInMilliSec; // Тайминг

    public String generateToken(User user){
        Date now = new Date();
        Map<String, Object> claim = new HashMap<>();
        claim.put("alg", "HS256");
        claim.put("typ", "JWT");

        return Jwts.builder()
                .setSubject(user.getUsername())
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + Long.parseLong(expireTimeInMilliSec))) // Время действия токена
                .signWith(SignatureAlgorithm.HS256, Base64.getEncoder().encodeToString(secret.getBytes()))
                .setHeaderParams(claim)
                .compact();
    }

    public Claims getClaimsFromToken(String token){
        return Jwts.parser()
                .setSigningKey(Base64.getEncoder().encodeToString(secret.getBytes()))
                .parseClaimsJws(token) // Тут обязательно должно быть parseclaimsjwS
                .getBody();
    }

    public String getUsernameFromToken(String token){
        return getClaimsFromToken(token).getSubject();
    }

    public Date getExpirationDate(String token){
        return getClaimsFromToken(token).getExpiration();
    }

    public Boolean isTokenExpired(String token){
        Date expirationDate = getExpirationDate(token);
        return expirationDate.before(new Date());
    }

    public Boolean isTokenValidated(String token){
        return !isTokenExpired(token);
    }
}
