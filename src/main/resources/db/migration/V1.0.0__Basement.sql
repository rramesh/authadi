CREATE SCHEMA IF NOT EXISTS vault;

CREATE EXTENSION IF NOT EXISTS btree_gist;
CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE vault.user_identities(
    uuid UUID NOT NULL DEFAULT gen_random_uuid(),
    user_reference_id character varying,
    user_key character varying NOT NULL,
    user_secondary_key character varying,
    password character varying NOT NULL,
    client_id UUID NOT NULL,
    secret character varying NOT NULL,
    active boolean NOT NULL DEFAULT true,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL
);

ALTER TABLE ONLY vault.user_identities
    ADD CONSTRAINT uuid_pkey PRIMARY KEY (uuid);

CREATE UNIQUE INDEX unique_user_key ON vault.user_identities(user_key);
CREATE UNIQUE INDEX unique_user_reference_id ON vault.user_identities(user_reference_id);
CREATE UNIQUE INDEX unique_client_uuid ON vault.user_identities(client_id);
