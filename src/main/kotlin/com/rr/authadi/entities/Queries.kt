package com.rr.authadi.entities

object UserIdentityQueries {
    const val userByUuid = "Select * from USER_IDENTITIES WHERE uuid = :uuid"

    const val userByKey = "SELECT * FROM USER_IDENTITIES WHERE user_key = :user_key LIMIT 1"

    const val userByKeyOrSecondary = """
        SELECT * FROM USER_IDENTITIES 
            WHERE user_key = :identity_key OR 
            (user_secondary_key IS NOT NULL AND user_secondary_key = :identity_key) LIMIT 1
        """
    const val authenticatedUser = "SELECT * FROM USER_IDENTITIES WHERE user_key = :user_key AND password = :password LIMIT 1"

    const val userByReferenceId = """
        SELECT * FROM USER_IDENTITIES 
            WHERE user_reference_id IS NOT NULL 
                AND user_reference_id = :user_reference_id 
        LIMIT 1
        """
    const val insert = """
        INSERT INTO USER_IDENTITIES(
            uuid, user_reference_id, user_key, user_secondary_key, password, client_id, secret, active
            ) 
        VALUES(
            gen_random_uuid(), :user_reference_id, :user_key, 
            :user_secondary_key, :password, :client_id, :secret, :active)
        """
}