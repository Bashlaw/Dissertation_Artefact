CREATE TABLE bill_log
(
    bill_log_id                      VARCHAR(255)                NOT NULL,
    created_at                       TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at                       TIMESTAMP WITHOUT TIME ZONE,
    account_id                       VARCHAR(255),
    item_item_id                     BIGINT                      NOT NULL,
    item_quantity                    BIGINT,
    item_ref                         VARCHAR(255),
    charge_amount                    DOUBLE PRECISION            NOT NULL,
    payment_status                   VARCHAR(255),
    payment_origin_payment_source_id BIGINT                      NOT NULL,
    trans_ref                        VARCHAR(255),
    is_used                          BOOLEAN                     NOT NULL,
    billing_setup_bill_id            UUID                        NOT NULL,
    is_email_sent                    BOOLEAN                     NOT NULL,
    issmssent                        BOOLEAN                     NOT NULL,
    is_notification_sent             BOOLEAN                     NOT NULL,
    payment_confirm_email_sent       BOOLEAN                     NOT NULL,
    payment_success                  BOOLEAN                     NOT NULL,
    currency                         VARCHAR(255),
    item_quantity_left               BIGINT,
    CONSTRAINT pk_billlog PRIMARY KEY (bill_log_id)
);

CREATE TABLE billing_method
(
    billing_methodid    BIGINT                      NOT NULL,
    created_at          TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at          TIMESTAMP WITHOUT TIME ZONE,
    billing_method_name VARCHAR(255),
    description         VARCHAR(255),
    validate            BOOLEAN                     NOT NULL,
    CONSTRAINT pk_billingmethod PRIMARY KEY (billing_methodid)
);

CREATE TABLE billing_setup
(
    bill_id                          UUID                        NOT NULL,
    created_at                       TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at                       TIMESTAMP WITHOUT TIME ZONE,
    account_id                       VARCHAR(255),
    billing_method_billing_methodid  BIGINT                      NOT NULL,
    means_of_payment                 VARCHAR(255),
    validate                         BOOLEAN                     NOT NULL,
    valid_from                       TIMESTAMP WITHOUT TIME ZONE,
    valid_till                       TIMESTAMP WITHOUT TIME ZONE,
    license_upgrade                  BOOLEAN                     NOT NULL,
    charge_amount                    DOUBLE PRECISION            NOT NULL,
    payment_success                  BOOLEAN                     NOT NULL,
    payment_ref                      VARCHAR(255),
    currency                         VARCHAR(255),
    email                            VARCHAR(255),
    phone                            VARCHAR(255),
    country                          VARCHAR(255),
    first_name                       VARCHAR(255),
    is_email_sent                    BOOLEAN                     NOT NULL,
    issmssent                        BOOLEAN                     NOT NULL,
    is_notification_sent             BOOLEAN                     NOT NULL,
    payment_confirm_email_sent       BOOLEAN                     NOT NULL,
    payment_source_payment_source_id BIGINT,
    CONSTRAINT pk_billingsetup PRIMARY KEY (bill_id)
);

CREATE TABLE billing_setup_bill_logs
(
    billing_setup_bill_id UUID         NOT NULL,
    bill_logs_bill_log_id VARCHAR(255) NOT NULL
);

CREATE TABLE billing_setup_packages
(
    billing_setup_bill_id UUID   NOT NULL,
    packages_package_id   BIGINT NOT NULL
);

CREATE TABLE change_log
(
    log_id      VARCHAR(255)                NOT NULL,
    created_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at  TIMESTAMP WITHOUT TIME ZONE,
    module      VARCHAR(255)                NOT NULL,
    user_id     BIGINT                      NOT NULL,
    action      VARCHAR(255)                NOT NULL,
    change_type SMALLINT                    NOT NULL,
    CONSTRAINT pk_changelog PRIMARY KEY (log_id)
);

CREATE TABLE clients
(
    client_id       BIGINT                      NOT NULL,
    created_at      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at      TIMESTAMP WITHOUT TIME ZONE,
    client_name     VARCHAR(255),
    description     VARCHAR(255),
    office_address  VARCHAR(255),
    office_phone_no VARCHAR(255),
    office_mail     VARCHAR(255),
    contact_person  VARCHAR(255),
    activation      BOOLEAN                     NOT NULL,
    CONSTRAINT pk_clients PRIMARY KEY (client_id)
);

CREATE TABLE confirm_payment
(
    confirm_payment_id VARCHAR(255) NOT NULL,
    status             BIGINT,
    message            VARCHAR(255),
    company_ref        VARCHAR(255),
    transaction_ref    VARCHAR(255),
    amount             VARCHAR(255),
    fraud_level        VARCHAR(255),
    fraud_explanation  VARCHAR(255),
    trans_fee          VARCHAR(255),
    CONSTRAINT pk_confirmpayment PRIMARY KEY (confirm_payment_id)
);

CREATE TABLE country
(
    country_code BIGINT                      NOT NULL,
    created_at   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at   TIMESTAMP WITHOUT TIME ZONE,
    short_code   VARCHAR(255)                NOT NULL,
    country_name VARCHAR(255)                NOT NULL,
    phone_format VARCHAR(255),
    CONSTRAINT pk_country PRIMARY KEY (country_code)
);

CREATE TABLE country_payment_sources
(
    country_country_code              BIGINT NOT NULL,
    payment_sources_payment_source_id BIGINT NOT NULL
);

CREATE TABLE coupon
(
    coupon_id            VARCHAR(255)                NOT NULL,
    created_at           TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at           TIMESTAMP WITHOUT TIME ZONE,
    code                 VARCHAR(255),
    bill_log_bill_log_id VARCHAR(255)                NOT NULL,
    used                 BOOLEAN                     NOT NULL,
    CONSTRAINT pk_coupon PRIMARY KEY (coupon_id)
);

CREATE TABLE item
(
    item_id        BIGINT                      NOT NULL,
    created_at     TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at     TIMESTAMP WITHOUT TIME ZONE,
    item_name      VARCHAR(255),
    description    VARCHAR(255),
    del_flag       BOOLEAN                     NOT NULL,
    item_ref       VARCHAR(255),
    item_price     DOUBLE PRECISION            NOT NULL,
    unit           VARCHAR(255),
    item_min_price DOUBLE PRECISION            NOT NULL,
    standalone     BOOLEAN                     NOT NULL,
    CONSTRAINT pk_item PRIMARY KEY (item_id)
);

CREATE TABLE item_package_quantity
(
    id                  BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    created_at          TIMESTAMP WITHOUT TIME ZONE             NOT NULL,
    updated_at          TIMESTAMP WITHOUT TIME ZONE,
    quantity            BIGINT,
    packages_package_id BIGINT                                  NOT NULL,
    item_item_id        BIGINT                                  NOT NULL,
    CONSTRAINT pk_itempackagequantity PRIMARY KEY (id)
);

CREATE TABLE item_packages
(
    item_item_id        BIGINT NOT NULL,
    packages_package_id BIGINT NOT NULL
);

CREATE TABLE license_type
(
    license_type_id   BIGINT                      NOT NULL,
    created_at        TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at        TIMESTAMP WITHOUT TIME ZONE,
    license_type_name VARCHAR(255),
    description       VARCHAR(255),
    user_count        BIGINT,
    valid             BOOLEAN                     NOT NULL,
    del_flag          BOOLEAN                     NOT NULL,
    client_client_id  BIGINT                      NOT NULL,
    CONSTRAINT pk_licensetype PRIMARY KEY (license_type_id)
);

CREATE TABLE license_upgrade
(
    license_upgrade_id       BIGINT                      NOT NULL,
    created_at               TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at               TIMESTAMP WITHOUT TIME ZONE,
    upgraded_from_package_id BIGINT                      NOT NULL,
    upgraded_to_package_id   BIGINT                      NOT NULL,
    billing_setup_bill_id    UUID                        NOT NULL,
    license_upgrade_bill_id  UUID                        NOT NULL,
    account_id               VARCHAR(255)                NOT NULL,
    initial_bill_id          UUID                        NOT NULL,
    CONSTRAINT pk_licenseupgrade PRIMARY KEY (license_upgrade_id)
);

CREATE TABLE package_rate
(
    package_rate_id     BIGINT                      NOT NULL,
    created_at          TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at          TIMESTAMP WITHOUT TIME ZONE,
    version_no          BIGINT,
    effect_date         TIMESTAMP WITHOUT TIME ZONE,
    rate                DOUBLE PRECISION            NOT NULL,
    validate            BOOLEAN                     NOT NULL,
    packages_package_id BIGINT                      NOT NULL,
    CONSTRAINT pk_packagerate PRIMARY KEY (package_rate_id)
);

CREATE TABLE package_type
(
    package_type_id              BIGINT                      NOT NULL,
    created_at                   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at                   TIMESTAMP WITHOUT TIME ZONE,
    package_type_name            VARCHAR(255),
    description                  VARCHAR(255),
    license_type_license_type_id BIGINT                      NOT NULL,
    is_visit                     BOOLEAN DEFAULT FALSE,
    CONSTRAINT pk_packagetype PRIMARY KEY (package_type_id)
);

CREATE TABLE packages
(
    package_id                   BIGINT                      NOT NULL,
    created_at                   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at                   TIMESTAMP WITHOUT TIME ZONE,
    package_name                 VARCHAR(255),
    description                  VARCHAR(255),
    duration                     BIGINT,
    activation                   BOOLEAN                     NOT NULL,
    recurring                    BOOLEAN                     NOT NULL,
    package_type_package_type_id BIGINT                      NOT NULL,
    CONSTRAINT pk_packages PRIMARY KEY (package_id)
);

CREATE TABLE packages_item_list
(
    packages_package_id BIGINT NOT NULL,
    item_list_item_id   BIGINT NOT NULL
);

CREATE TABLE payment_integration
(
    payment_id                       VARCHAR(255)                NOT NULL,
    created_at                       TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at                       TIMESTAMP WITHOUT TIME ZONE,
    client                           VARCHAR(255),
    account_id                       VARCHAR(255),
    trans_ref                        VARCHAR(255)                NOT NULL,
    amount                           DOUBLE PRECISION            NOT NULL,
    currency                         VARCHAR(255),
    payment_type                     VARCHAR(255),
    country                          VARCHAR(255),
    method                           VARCHAR(255),
    redirect_url                     VARCHAR(255),
    back_url                         VARCHAR(255),
    service_type                     VARCHAR(255),
    service_date                     VARCHAR(255),
    description                      VARCHAR(255),
    payment_source_payment_source_id BIGINT                      NOT NULL,
    phone_number                     VARCHAR(255),
    CONSTRAINT pk_paymentintegration PRIMARY KEY (payment_id)
);

CREATE TABLE payment_response
(
    payment_response_id            VARCHAR(255) NOT NULL,
    status                         INTEGER      NOT NULL,
    message                        VARCHAR(255),
    error                          VARCHAR(255),
    payment_url                    VARCHAR(255),
    payment_integration_payment_id VARCHAR(255),
    CONSTRAINT pk_paymentresponse PRIMARY KEY (payment_response_id)
);

CREATE TABLE payment_source
(
    payment_source_id BIGINT                      NOT NULL,
    created_at        TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at        TIMESTAMP WITHOUT TIME ZONE,
    source_code       VARCHAR(255)                NOT NULL,
    CONSTRAINT pk_paymentsource PRIMARY KEY (payment_source_id)
);

CREATE TABLE payment_source_country_list
(
    payment_source_payment_source_id BIGINT NOT NULL,
    country_list_country_code        BIGINT NOT NULL
);

CREATE TABLE payment_source_url_list
(
    payment_source_payment_source_id BIGINT NOT NULL,
    url_list_url_id                  BIGINT NOT NULL
);

CREATE TABLE paymenturl
(
    url_id     BIGINT                      NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    url        VARCHAR(255),
    CONSTRAINT pk_paymenturl PRIMARY KEY (url_id)
);

CREATE TABLE paymenturl_payment_sources
(
    paymenturl_url_id                 BIGINT NOT NULL,
    payment_sources_payment_source_id BIGINT NOT NULL
);

CREATE TABLE transaction_log
(
    trans_log_id         VARCHAR(255)                NOT NULL,
    created_at           TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at           TIMESTAMP WITHOUT TIME ZONE,
    bill_log_bill_log_id VARCHAR(255)                NOT NULL,
    is_credit            BOOLEAN                     NOT NULL,
    trans_ref            VARCHAR(255),
    reason               VARCHAR(255),
    CONSTRAINT pk_transactionlog PRIMARY KEY (trans_log_id)
);

ALTER TABLE billing_method
    ADD CONSTRAINT uc_billingmethod_billingmethodname UNIQUE (billing_method_name);

ALTER TABLE bill_log
    ADD CONSTRAINT uc_billlog_transref UNIQUE (trans_ref);

ALTER TABLE clients
    ADD CONSTRAINT uc_clients_clientname UNIQUE (client_name);

ALTER TABLE confirm_payment
    ADD CONSTRAINT uc_confirmpayment_transactionref UNIQUE (transaction_ref);

ALTER TABLE country
    ADD CONSTRAINT uc_country_countryname UNIQUE (country_name);

ALTER TABLE country
    ADD CONSTRAINT uc_country_shortcode UNIQUE (short_code);

ALTER TABLE coupon
    ADD CONSTRAINT uc_coupon_code UNIQUE (code);

ALTER TABLE item
    ADD CONSTRAINT uc_item_itemname UNIQUE (item_name);

ALTER TABLE license_type
    ADD CONSTRAINT uc_licensetype_licensetypename UNIQUE (license_type_name);

ALTER TABLE license_upgrade
    ADD CONSTRAINT uc_licenseupgrade_initialbillid UNIQUE (initial_bill_id);

ALTER TABLE license_upgrade
    ADD CONSTRAINT uc_licenseupgrade_licenseupgradebillid UNIQUE (license_upgrade_bill_id);

ALTER TABLE package_rate
    ADD CONSTRAINT uc_packagerate_versionno UNIQUE (version_no);

ALTER TABLE packages
    ADD CONSTRAINT uc_packages_packagename UNIQUE (package_name);

ALTER TABLE package_type
    ADD CONSTRAINT uc_packagetype_packagetypename UNIQUE (package_type_name);

ALTER TABLE payment_source
    ADD CONSTRAINT uc_paymentsource_sourcecode UNIQUE (source_code);

ALTER TABLE transaction_log
    ADD CONSTRAINT uc_transactionlog_transref UNIQUE (trans_ref);

ALTER TABLE billing_setup
    ADD CONSTRAINT FK_BILLINGSETUP_ON_BILLINGMETHOD_BILLINGMETHODID FOREIGN KEY (billing_method_billing_methodid) REFERENCES billing_method (billing_methodid);

ALTER TABLE billing_setup
    ADD CONSTRAINT FK_BILLINGSETUP_ON_PAYMENTSOURCE_PAYMENTSOURCEID FOREIGN KEY (payment_source_payment_source_id) REFERENCES payment_source (payment_source_id);

ALTER TABLE bill_log
    ADD CONSTRAINT FK_BILLLOG_ON_BILLINGSETUP_BILLID FOREIGN KEY (billing_setup_bill_id) REFERENCES billing_setup (bill_id);

ALTER TABLE bill_log
    ADD CONSTRAINT FK_BILLLOG_ON_ITEM_ITEMID FOREIGN KEY (item_item_id) REFERENCES item (item_id);

ALTER TABLE bill_log
    ADD CONSTRAINT FK_BILLLOG_ON_PAYMENTORIGIN_PAYMENTSOURCEID FOREIGN KEY (payment_origin_payment_source_id) REFERENCES payment_source (payment_source_id);

ALTER TABLE coupon
    ADD CONSTRAINT FK_COUPON_ON_BILLLOG_BILLLOGID FOREIGN KEY (bill_log_bill_log_id) REFERENCES bill_log (bill_log_id);

ALTER TABLE item_package_quantity
    ADD CONSTRAINT FK_ITEMPACKAGEQUANTITY_ON_ITEM_ITEMID FOREIGN KEY (item_item_id) REFERENCES item (item_id);

ALTER TABLE item_package_quantity
    ADD CONSTRAINT FK_ITEMPACKAGEQUANTITY_ON_PACKAGES_PACKAGEID FOREIGN KEY (packages_package_id) REFERENCES packages (package_id);

ALTER TABLE license_type
    ADD CONSTRAINT FK_LICENSETYPE_ON_CLIENT_CLIENTID FOREIGN KEY (client_client_id) REFERENCES clients (client_id);

ALTER TABLE license_upgrade
    ADD CONSTRAINT FK_LICENSEUPGRADE_ON_BILLINGSETUP_BILLID FOREIGN KEY (billing_setup_bill_id) REFERENCES billing_setup (bill_id);

ALTER TABLE license_upgrade
    ADD CONSTRAINT FK_LICENSEUPGRADE_ON_UPGRADEDFROM_PACKAGEID FOREIGN KEY (upgraded_from_package_id) REFERENCES packages (package_id);

ALTER TABLE license_upgrade
    ADD CONSTRAINT FK_LICENSEUPGRADE_ON_UPGRADEDTO_PACKAGEID FOREIGN KEY (upgraded_to_package_id) REFERENCES packages (package_id);

ALTER TABLE package_rate
    ADD CONSTRAINT FK_PACKAGERATE_ON_PACKAGES_PACKAGEID FOREIGN KEY (packages_package_id) REFERENCES packages (package_id);

ALTER TABLE packages
    ADD CONSTRAINT FK_PACKAGES_ON_PACKAGETYPE_PACKAGETYPEID FOREIGN KEY (package_type_package_type_id) REFERENCES package_type (package_type_id);

ALTER TABLE package_type
    ADD CONSTRAINT FK_PACKAGETYPE_ON_LICENSETYPE_LICENSETYPEID FOREIGN KEY (license_type_license_type_id) REFERENCES license_type (license_type_id);

ALTER TABLE payment_integration
    ADD CONSTRAINT FK_PAYMENTINTEGRATION_ON_PAYMENTSOURCE_PAYMENTSOURCEID FOREIGN KEY (payment_source_payment_source_id) REFERENCES payment_source (payment_source_id);

ALTER TABLE payment_response
    ADD CONSTRAINT FK_PAYMENTRESPONSE_ON_PAYMENTINTEGRATION_PAYMENTID FOREIGN KEY (payment_integration_payment_id) REFERENCES payment_integration (payment_id);

ALTER TABLE transaction_log
    ADD CONSTRAINT FK_TRANSACTIONLOG_ON_BILLLOG_BILLLOGID FOREIGN KEY (bill_log_bill_log_id) REFERENCES bill_log (bill_log_id);

ALTER TABLE billing_setup_bill_logs
    ADD CONSTRAINT fk_bilsetbillog_on_bill_log FOREIGN KEY (bill_logs_bill_log_id) REFERENCES bill_log (bill_log_id);

ALTER TABLE billing_setup_bill_logs
    ADD CONSTRAINT fk_bilsetbillog_on_billing_setup FOREIGN KEY (billing_setup_bill_id) REFERENCES billing_setup (bill_id);

ALTER TABLE billing_setup_packages
    ADD CONSTRAINT fk_bilsetpac_on_billing_setup FOREIGN KEY (billing_setup_bill_id) REFERENCES billing_setup (bill_id);

ALTER TABLE billing_setup_packages
    ADD CONSTRAINT fk_bilsetpac_on_packages FOREIGN KEY (packages_package_id) REFERENCES packages (package_id);

ALTER TABLE country_payment_sources
    ADD CONSTRAINT fk_coupaysou_on_country FOREIGN KEY (country_country_code) REFERENCES country (country_code);

ALTER TABLE country_payment_sources
    ADD CONSTRAINT fk_coupaysou_on_payment_source FOREIGN KEY (payment_sources_payment_source_id) REFERENCES payment_source (payment_source_id);

ALTER TABLE item_packages
    ADD CONSTRAINT fk_itepac_on_item FOREIGN KEY (item_item_id) REFERENCES item (item_id);

ALTER TABLE item_packages
    ADD CONSTRAINT fk_itepac_on_packages FOREIGN KEY (packages_package_id) REFERENCES packages (package_id);

ALTER TABLE packages_item_list
    ADD CONSTRAINT fk_pacitelis_on_item FOREIGN KEY (item_list_item_id) REFERENCES item (item_id);

ALTER TABLE packages_item_list
    ADD CONSTRAINT fk_pacitelis_on_packages FOREIGN KEY (packages_package_id) REFERENCES packages (package_id);

ALTER TABLE paymenturl_payment_sources
    ADD CONSTRAINT fk_paypaysou_on_payment_source FOREIGN KEY (payment_sources_payment_source_id) REFERENCES payment_source (payment_source_id);

ALTER TABLE paymenturl_payment_sources
    ADD CONSTRAINT fk_paypaysou_on_payment_u_r_l FOREIGN KEY (paymenturl_url_id) REFERENCES paymenturl (url_id);

ALTER TABLE payment_source_country_list
    ADD CONSTRAINT fk_paysoucoulis_on_country FOREIGN KEY (country_list_country_code) REFERENCES country (country_code);

ALTER TABLE payment_source_country_list
    ADD CONSTRAINT fk_paysoucoulis_on_payment_source FOREIGN KEY (payment_source_payment_source_id) REFERENCES payment_source (payment_source_id);

ALTER TABLE payment_source_url_list
    ADD CONSTRAINT fk_paysouurllis_on_payment_source FOREIGN KEY (payment_source_payment_source_id) REFERENCES payment_source (payment_source_id);

ALTER TABLE payment_source_url_list
    ADD CONSTRAINT fk_paysouurllis_on_payment_u_r_l FOREIGN KEY (url_list_url_id) REFERENCES paymenturl (url_id);