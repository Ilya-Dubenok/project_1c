CREATE SCHEMA IF NOT EXISTS app;

CREATE TABLE IF NOT EXISTS app.category
(
    id uuid NOT NULL,
    name character varying(255)  NOT NULL,
    parent_id uuid,
    CONSTRAINT category_pkey PRIMARY KEY (id),
    CONSTRAINT category_name_unique_constraint UNIQUE (name),
    CONSTRAINT fk_category_parent FOREIGN KEY (parent_id)
        REFERENCES app.category (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);