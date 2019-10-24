CREATE SCHEMA IF NOT EXISTS vault;

CREATE EXTENSION IF NOT EXISTS btree_gist;
CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE vault.users(
    uuid character varying NOT NULL DEFAULT gen_random_uuid(),
    user_uuid character varying NOT NULL,
    phone_number character varying(16) NOT NULL,
    password character varying NOT NULL DEFAULT gen_random_uuid(),
    client_id character varying NOT NULL DEFAULT gen_random_uuid(),
    secret character varying NOT NULL DEFAULT gen_random_uuid(),
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL
);

ALTER TABLE ONLY vault.users
    ADD CONSTRAINT uuid_pkey PRIMARY KEY (uuid);

CREATE UNIQUE INDEX unique_phone_number ON vault.users(phone_number);
CREATE UNIQUE INDEX unique_user_uuid ON vault.users(user_uuid);
CREATE UNIQUE INDEX unique_client_uuid ON vault.users(client_id);