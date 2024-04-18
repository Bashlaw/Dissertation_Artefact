CREATE TABLE region_rate
(
    region_rate_id               BIGINT                      NOT NULL,
    created_at                   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at                   TIMESTAMP WITHOUT TIME ZONE,
    rate                         DOUBLE PRECISION            NOT NULL,
    country_country_code         BIGINT                      NOT NULL,
    package_rate_package_rate_id BIGINT                      NOT NULL,
    CONSTRAINT pk_regionrate PRIMARY KEY (region_rate_id)
);

ALTER TABLE region_rate
    ADD CONSTRAINT FK_REGIONRATE_ON_COUNTRY_COUNTRYCODE FOREIGN KEY (country_country_code) REFERENCES country (country_code);

ALTER TABLE region_rate
    ADD CONSTRAINT FK_REGIONRATE_ON_PACKAGERATE_PACKAGERATEID FOREIGN KEY (package_rate_package_rate_id) REFERENCES package_rate (package_rate_id);

