package com.rr.authadi.test.entities.vault

import com.rr.authadi.entities.vault.UserIdentity
import com.rr.authadi.library.JwtHelper
import io.jsonwebtoken.Jwts
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.sql.Timestamp
import java.time.Instant.now
import java.util.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserIdentityTest {
    @Test
    fun `it should generate a valid jws`() {
        val now = Timestamp.from(now())
        val secret = JwtHelper.generateUserSecret()
        val userIdentity = UserIdentity(
                uuid = UUID.randomUUID(),
                userKey = "user@userkey.keyo",
                password = "Secret Password",
                clientId = UUID.randomUUID(),
                secret = secret,
                createdAt = now,
                updatedAt = now
        )
        val jws = userIdentity.getJws()
        val secretKey = JwtHelper.getKeyFromSecret(secret)
        val expectedUuid = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jws).body.subject
        assertEquals(expectedUuid, userIdentity.uuid.toString())
    }
}