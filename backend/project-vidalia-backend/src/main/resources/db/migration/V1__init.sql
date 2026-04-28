-- Flyway Migration V1: Initial schema for Project Vidalia backend

-- Create users table
CREATE TABLE users
(
    user_id         uuid PRIMARY KEY,
    email           varchar(254) NOT NULL UNIQUE,
    password        varchar(254) NOT NULL,
    secondary_email varchar(254),
    phone_number    varchar(16),
    created_at      timestamp without time zone NOT NULL,
    last_login      timestamp without time zone,
    role            varchar(50)  NOT NULL
);

-- Create organisation_profiles table
CREATE TABLE organisation_profiles
(
    v_profile_id        uuid PRIMARY KEY,
    user_id             uuid         NOT NULL UNIQUE,
    display_name        varchar(100) NOT NULL,
    profile_picture_url varchar(500),
    account_type        varchar(100) NOT NULL,
    description         text,
    contact_email       varchar(254),
    location            varchar(255),
    website_url         varchar(500),
    last_updated        timestamp without time zone NOT NULL,
    verified            boolean      NOT NULL DEFAULT false,
    CONSTRAINT fk_org_profile_user FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE
);

-- Create volunteer_profiles table
CREATE TABLE volunteer_profiles
(
    v_profile_id        uuid PRIMARY KEY,
    user_id             uuid         NOT NULL UNIQUE,
    forename            varchar(100) NOT NULL,
    surname             varchar(100),
    preferred_name      varchar(100) NOT NULL,
    profile_picture_url varchar(500),
    cv_url              varchar(500),
    contact_email       varchar(254),
    location            varchar(255),
    profile_description text,
    longitude           double precision,
    latitude            double precision,
    max_travel_distance integer               DEFAULT 0,
    remote_only         boolean               DEFAULT false,
    total_hours         integer               DEFAULT 0,
    availability        varchar(255),
    date_of_birth       date         NOT NULL,
    last_updated        timestamp without time zone NOT NULL,
    points_balance      integer      NOT NULL DEFAULT 0,
    CONSTRAINT fk_vol_profile_user FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE
);

-- Create opportunities table
CREATE TABLE opportunities
(
    opportunity_id          uuid PRIMARY KEY,
    title                   varchar(255) NOT NULL,
    description             text         NOT NULL,
    location                varchar(255),
    longitude               double precision,
    latitude                double precision,
    remote                  boolean      NOT NULL DEFAULT false,
    status                  varchar(50)  NOT NULL,
    min_age                 integer,
    start_date              date         NOT NULL,
    end_date                date,
    recurring               boolean,
    required_hours          integer,
    capacity                integer,
    date_created            timestamp without time zone NOT NULL,
    last_updated            timestamp without time zone NOT NULL,
    organisation_profile_id uuid         NOT NULL,
    CONSTRAINT fk_opportunity_org FOREIGN KEY (organisation_profile_id) REFERENCES organisation_profiles (v_profile_id) ON DELETE CASCADE
);

-- Create semantic_tags table
CREATE TABLE semantic_tags
(
    id   bigserial PRIMARY KEY,
    name varchar(255) NOT NULL UNIQUE
);

-- Create labels table
CREATE TABLE label
(
    id              bigserial PRIMARY KEY,
    name            varchar(255) NOT NULL,
    semantic_tag_id bigint,
    required        boolean      NOT NULL,
    type            varchar(50)  NOT NULL,
    CONSTRAINT fk_label_semantic_tag FOREIGN KEY (semantic_tag_id) REFERENCES semantic_tags (id) ON DELETE SET NULL
);

-- Create opportunity_label_links table
CREATE TABLE opportunity_label_links
(
    opportunity_id uuid             NOT NULL,
    label_id       bigint           NOT NULL,
    weight         double precision NOT NULL,
    PRIMARY KEY (opportunity_id, label_id),
    CONSTRAINT fk_opp_label_link_opp FOREIGN KEY (opportunity_id) REFERENCES opportunities (opportunity_id) ON DELETE CASCADE,
    CONSTRAINT fk_opp_label_link_label FOREIGN KEY (label_id) REFERENCES label (id) ON DELETE CASCADE
);

-- Create volunteer_label_links table
CREATE TABLE volunteer_label_links
(
    volunteer_id uuid             NOT NULL,
    label_id     bigint           NOT NULL,
    weight       double precision NOT NULL,
    PRIMARY KEY (volunteer_id, label_id),
    CONSTRAINT fk_vol_label_link_vol FOREIGN KEY (volunteer_id) REFERENCES volunteer_profiles (v_profile_id) ON DELETE CASCADE,
    CONSTRAINT fk_vol_label_link_label FOREIGN KEY (label_id) REFERENCES label (id) ON DELETE CASCADE
);

-- Create organisation_label_links table
CREATE TABLE organisation_label_links
(
    organisation_id uuid             NOT NULL,
    label_id        bigint           NOT NULL,
    weight          double precision NOT NULL,
    PRIMARY KEY (organisation_id, label_id),
    CONSTRAINT fk_org_label_link_org FOREIGN KEY (organisation_id) REFERENCES organisation_profiles (v_profile_id) ON DELETE CASCADE,
    CONSTRAINT fk_org_label_link_label FOREIGN KEY (label_id) REFERENCES label (id) ON DELETE CASCADE
);

-- Create semantic_links table
CREATE TABLE semantic_links
(
    semantic_tag_one_id bigint           NOT NULL,
    semantic_tag_two_id bigint           NOT NULL,
    weight              double precision NOT NULL,
    PRIMARY KEY (semantic_tag_one_id, semantic_tag_two_id),
    CONSTRAINT fk_sem_link_tag_one FOREIGN KEY (semantic_tag_one_id) REFERENCES semantic_tags (id) ON DELETE CASCADE,
    CONSTRAINT fk_sem_link_tag_two FOREIGN KEY (semantic_tag_two_id) REFERENCES semantic_tags (id) ON DELETE CASCADE
);

-- Create indexes for common lookups
CREATE INDEX idx_users_email ON users (email);
CREATE INDEX idx_org_profiles_user_id ON organisation_profiles (user_id);
CREATE INDEX idx_vol_profiles_user_id ON volunteer_profiles (user_id);
CREATE INDEX idx_opportunities_org ON opportunities (organisation_profile_id);
CREATE INDEX idx_label_semantic_tag ON label (semantic_tag_id);