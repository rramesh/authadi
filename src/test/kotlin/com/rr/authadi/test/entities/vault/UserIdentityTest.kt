package com.rr.authadi.test.entities.vault

import com.rr.authadi.entities.vault.UserIdentity
import com.rr.authadi.library.JwtHelper
import io.jsonwebtoken.Jwts
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.sql.Timestamp
import java.time.Instant.now
import java.util.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserIdentityTest {
    @Test
    fun `it should generate a valid jws`() {
        val userIdentity = randomUserIdentity()
        val jws = userIdentity.getJws()
        val secretKey = JwtHelper.getKeyFromSecret(userIdentity.secret)
        val expectedUuid = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jws).body.subject
        assertEquals(expectedUuid, userIdentity.uuid.toString())
    }

    @Test
    fun `it should return true with valid jws token`() {
        val userIdentity = randomUserIdentity()
        val jws = userIdentity.getJws()
        val result = userIdentity.validateJws(jws)
        assertTrue(result)
    }

    @Test
    fun `it should return false with invalid jws token`() {
        val userIdentity = randomUserIdentity()
        val jws = userIdentity.getJws()
        val differentUser = randomUserIdentity()
        val result = differentUser.validateJws(jws)
        assertFalse(result)
    }

    private fun randomUserIdentity(): UserIdentity {
        val now = Timestamp.from(now())
        val secret = JwtHelper.generateUserSecret()
        return UserIdentity(
                uuid = UUID.randomUUID(),
                userKey = "${UUID.randomUUID().toString()}@userkey.keyo",
                password = "Secret Password",
                clientId = UUID.randomUUID(),
                secret = secret,
                createdAt = now,
                updatedAt = now
        )
    }
}