package odyseja.odysejapka.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import odyseja.odysejapka.users.UserEntity
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.Date
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

@Service
class JwtService(
    @Value("\${jwt.secret}") private val secret: String,
    @Value("\${jwt.expiration:3600}") private val expirationSeconds: Long
) {
    companion object {
        const val ISSUER = "odysejapka"
    }

    private val key: SecretKey by lazy {
        // Hash to exactly 32 bytes for consistent HS256
        val keyBytes = java.security.MessageDigest.getInstance("SHA-256").digest(secret.toByteArray())
        SecretKeySpec(keyBytes, "HmacSHA256")
    }

    fun generateToken(user: UserEntity): String {
        val now = Date()
        val expiration = Date(now.time + expirationSeconds * 1000)

        return Jwts.builder()
            .subject(user.userId)
            .issuer(ISSUER)
            .claim("email", user.email)
            .claim("name", user.name)
            .issuedAt(now)
            .expiration(expiration)
            .signWith(key, Jwts.SIG.HS256)
            .compact()
    }

    fun parseToken(token: String): Claims {
        return Jwts.parser()
            .verifyWith(key)
            .requireIssuer(ISSUER)
            .build()
            .parseSignedClaims(token)
            .payload
    }

    fun getSigningKey(): SecretKey = key
}
