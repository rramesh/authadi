package com.rr.authadi.dao


import com.rr.authadi.entities.UserIdentityQueries
import com.rr.authadi.entities.vault.UserIdentity
import com.rr.authadi.entities.vault.UserIdentityMapper
import org.jdbi.v3.sqlobject.SqlObject
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate
import java.util.*

@RegisterBeanMapper(UserIdentityMapper::class)
interface UserIdentityDao : SqlObject {
    @SqlQuery(UserIdentityQueries.userByUuid)
    fun findByUuid(@Bind("uuid") uuid: UUID): UserIdentity?

    @SqlQuery(UserIdentityQueries.userByKey)
    fun findByUserKey(@Bind("user_key") userKey: String): UserIdentity?

    @SqlQuery(UserIdentityQueries.userByKeyOrSecondary)
    fun findByUserPrimaryOrSecondaryKey(@Bind("identity_key") identityKey: String): UserIdentity?

    @SqlQuery(UserIdentityQueries.authenticatedUser)
    fun authenticatedUser(
            @Bind("user_key") userKey: String,
            @Bind("password") password: String
    ): UserIdentity?

    @SqlQuery(UserIdentityQueries.userByReferenceId)
    fun findByUserByReferenceId(@Bind("user_reference_id") userReferenceId: String): UserIdentity?

    @SqlUpdate(UserIdentityQueries.insert)
    @GetGeneratedKeys("uuid")
    fun insert(
            @Bind("user_reference_id") userReferenceId: String? = null,
            @Bind("user_key") userKey: String,
            @Bind("user_secondary_key") userSecondaryKey: String? = null,
            @Bind("password") password: String,
            @Bind("client_id") clientId: UUID,
            @Bind("secret") secret: String,
            @Bind("active") active: Boolean? = true
    ): UUID
}
