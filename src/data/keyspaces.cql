CREATE KEYSPACE IF NOT EXISTS hacora WITH REPLICATION = {'class' : 'SimpleStrategy', 'replication_factor': 3};
CREATE TYPE IF NOT EXISTS hacora.address(
    adminArea text,
    countryCode text,
    countryName text,
    featureName text,
    locality    text,
    postalCode  text,
    premises    text
);

CREATE TYPE IF NOT EXISTS hacora.location(
    altitude double,
    longitude double,
    speed float,
    addresses set<frozen <address>>
);

CREATE TYPE IF NOT EXISTS hacora.motion_event(
    sensorType text,
    accuracy int,
    values list<float>
);



CREATE TABLE IF NOT EXISTS hacora.activities(
    user_id bigint,
    created timeuuid,
    source  text,
    event frozen <motion_event>,
    location frozen<location>,
    PRIMARY KEY (user_id,created)
) WITH CLUSTERING ORDER BY (created DESC);
