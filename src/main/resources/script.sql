CREATE TABLE IF NOT EXISTS clans (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY,
    name VARCHAR(255) NOT NULL,
    gold BIGINT,
    CONSTRAINT pk_clan PRIMARY KEY (id),
    CONSTRAINT UQ_name UNIQUE (name)
);

CREATE TABLE IF NOT EXISTS persons (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY,
    name VARCHAR(255) NOT NULL,
    gold BIGINT NOT NULL,
    clan_id BIGINT REFERENCES clans(id) ON DELETE CASCADE,
    CONSTRAINT pk_person PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS clan_person (
    person_id BIGINT REFERENCES persons(id),
    clan_id BIGINT REFERENCES clans(id),
    CONSTRAINT pk_clan_person PRIMARY KEY (person_id, clan_id)
);

CREATE TABLE IF NOT EXISTS gold_operations (
    operation_id BIGINT GENERATED BY DEFAULT AS IDENTITY,
    clan_id BIGINT REFERENCES clans(id),
    person_id BIGINT REFERENCES persons(id),
    clan_gold_amount BIGINT NOT NULL REFERENCES clans(gold),
    person_gold_amount BIGINT NOT NULL REFERENCES persons(gold),
    operation_date TIMESTAMP,
    isTaken boolean,
    isCompleted boolean
);