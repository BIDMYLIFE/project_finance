IF NOT EXISTS (SELECT 1 FROM sys.schemas WHERE name = 'SOUTHWND') EXEC('CREATE SCHEMA SOUTHWND');

CREATE TABLE SOUTHWND.organizations (
    id UNIQUEIDENTIFIER NOT NULL CONSTRAINT pk_organizations PRIMARY KEY,
    name NVARCHAR(200) NOT NULL,
    created_at DATETIME2(6) NOT NULL
);

CREATE TABLE SOUTHWND.users (
    id UNIQUEIDENTIFIER NOT NULL CONSTRAINT pk_users PRIMARY KEY,
    organization_id UNIQUEIDENTIFIER NOT NULL,
    email NVARCHAR(320) NOT NULL,
    password_hash NVARCHAR(512) NOT NULL,
    enabled BIT NOT NULL CONSTRAINT df_users_enabled DEFAULT 1,
    created_at DATETIME2(6) NOT NULL,
    CONSTRAINT fk_users_organization FOREIGN KEY (organization_id) REFERENCES SOUTHWND.organizations(id),
    CONSTRAINT uq_users_email UNIQUE (email)
);

CREATE TABLE SOUTHWND.user_roles (
    user_id UNIQUEIDENTIFIER NOT NULL,
    role_name NVARCHAR(50) NOT NULL,
    CONSTRAINT pk_user_roles PRIMARY KEY (user_id, role_name),
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES SOUTHWND.users(id)
);

CREATE TABLE SOUTHWND.auth_sessions (
    id UNIQUEIDENTIFIER NOT NULL CONSTRAINT pk_auth_sessions PRIMARY KEY,
    user_id UNIQUEIDENTIFIER NOT NULL,
    organization_id UNIQUEIDENTIFIER NOT NULL,
    refresh_token_hash VARBINARY(32) NOT NULL,
    created_at DATETIME2(6) NOT NULL,
    expires_at DATETIME2(6) NOT NULL,
    last_used_at DATETIME2(6) NULL,
    revoked_at DATETIME2(6) NULL,
    CONSTRAINT fk_auth_sessions_user FOREIGN KEY (user_id) REFERENCES SOUTHWND.users(id),
    CONSTRAINT fk_auth_sessions_organization FOREIGN KEY (organization_id) REFERENCES SOUTHWND.organizations(id),
    CONSTRAINT uq_auth_sessions_refresh_hash UNIQUE (refresh_token_hash)
);

CREATE INDEX ix_users_organization ON SOUTHWND.users(organization_id);
CREATE INDEX ix_auth_sessions_user ON SOUTHWND.auth_sessions(user_id);
CREATE INDEX ix_auth_sessions_expiry ON SOUTHWND.auth_sessions(expires_at);

CREATE TABLE SOUTHWND.bootstrap_state (
    state_key NVARCHAR(50) NOT NULL CONSTRAINT pk_bootstrap_state PRIMARY KEY,
    initialized BIT NOT NULL,
    initialized_at DATETIME2(6) NULL
);
INSERT INTO SOUTHWND.bootstrap_state(state_key, initialized) VALUES ('PRIMARY', 0);