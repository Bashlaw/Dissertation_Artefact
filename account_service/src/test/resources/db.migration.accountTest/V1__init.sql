CREATE TABLE access_log
(
    id               BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    created_at       TIMESTAMP WITHOUT TIME ZONE             NOT NULL,
    updated_at       TIMESTAMP WITHOUT TIME ZONE,
    user_id          BIGINT,
    device_info      VARCHAR(255),
    ip_address       VARCHAR(255),
    accessed_service BIGINT,
    CONSTRAINT pk_accesslog PRIMARY KEY (id)
);

CREATE TABLE otp
(
    id             BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    encrypted_code VARCHAR(255),
    recipient      VARCHAR(255),
    recipient_name VARCHAR(255),
    used           BOOLEAN                                 NOT NULL,
    created_date   TIMESTAMP WITHOUT TIME ZONE,
    expiry_date    TIMESTAMP WITHOUT TIME ZONE,
    user_type      SMALLINT,
    CONSTRAINT pk_otp PRIMARY KEY (id)
);

CREATE TABLE permissions
(
    id          BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name        VARCHAR(255),
    description VARCHAR(255),
    CONSTRAINT pk_permissions PRIMARY KEY (id)
);

CREATE TABLE permissions_user_type_list
(
    permissions_id BIGINT NOT NULL,
    user_type_list VARCHAR(255)
);

CREATE TABLE user_roles
(
    id          BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    created_at  TIMESTAMP WITHOUT TIME ZONE             NOT NULL,
    updated_at  TIMESTAMP WITHOUT TIME ZONE,
    name        VARCHAR(255),
    alias       VARCHAR(255)                            NOT NULL,
    description VARCHAR(255),
    disabled    BOOLEAN                                 NOT NULL,
    CONSTRAINT pk_user_roles PRIMARY KEY (id)
);

CREATE TABLE user_roles_permission_list
(
    permission_list_id BIGINT NOT NULL,
    user_roles_id      BIGINT NOT NULL
);

CREATE TABLE users
(
    id             BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    created_at     TIMESTAMP WITHOUT TIME ZONE             NOT NULL,
    updated_at     TIMESTAMP WITHOUT TIME ZONE,
    first_name     VARCHAR(255),
    last_name      VARCHAR(255),
    email          VARCHAR(255),
    phone_number   VARCHAR(255),
    password       VARCHAR(255),
    user_role_id   BIGINT                                  NOT NULL,
    disabled       BOOLEAN                                 NOT NULL,
    reset_password BOOLEAN                                 NOT NULL,
    user_type      VARCHAR(255),
    account_id     VARCHAR(255),
    dob      VARCHAR(255),
    gender     VARCHAR(255),
    deleted        BOOLEAN                                 NOT NULL,
    CONSTRAINT pk_users PRIMARY KEY (id)
);

ALTER TABLE otp
    ADD CONSTRAINT uc_otp_recipient_user_type UNIQUE (recipient);

ALTER TABLE permissions
    ADD CONSTRAINT uc_permissions_name UNIQUE (name);

ALTER TABLE user_roles
    ADD CONSTRAINT uc_user_roles_alias UNIQUE (alias);

ALTER TABLE users
    ADD CONSTRAINT uc_users_email UNIQUE (email);

ALTER TABLE users
    ADD CONSTRAINT uc_users_phonenumber UNIQUE (phone_number);

CREATE INDEX idx_permission_name ON permissions (name);

CREATE INDEX idx_userrole_alias ON user_roles (alias);

CREATE INDEX idx_userrole_name ON user_roles (name);

CREATE INDEX idx_users_email ON users (email);

CREATE INDEX idx_users_phonenumber ON users (phone_number);

CREATE INDEX idx_users_usertype ON users (user_type);

ALTER TABLE access_log
    ADD CONSTRAINT FK_ACCESSLOG_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE users
    ADD CONSTRAINT FK_USERS_ON_USERROLE FOREIGN KEY (user_role_id) REFERENCES user_roles (id);

ALTER TABLE permissions_user_type_list
    ADD CONSTRAINT fk_permissions_usertypelist_on_user_permission FOREIGN KEY (permissions_id) REFERENCES permissions (id);

ALTER TABLE user_roles_permission_list
    ADD CONSTRAINT fk_userolperlis_on_user_permission FOREIGN KEY (permission_list_id) REFERENCES permissions (id);

ALTER TABLE user_roles_permission_list
    ADD CONSTRAINT fk_userolperlis_on_user_role FOREIGN KEY (user_roles_id) REFERENCES user_roles (id);