create or replace procedure get_package_rate(IN packagename character varying, INOUT count integer)
    language plpgsql
as
$$
BEGIN
    count = 0;

    --get default count for existing package
    IF EXISTS (SELECT version_no FROM package_rate
               WHERE validate = true AND packages_package_id = (SELECT package_id FROM packages WHERE package_name = packageName AND activation = true)
               ORDER BY effect_date)
    THEN
        count = (SELECT version_no FROM package_rate
                 WHERE validate = true AND packages_package_id = (SELECT package_id FROM packages WHERE package_name = packageName AND activation = true)
                   AND effect_date = (SELECT MIN(effect_date)  FROM package_rate WHERE packages_package_id = (SELECT package_id FROM packages WHERE package_name = packageName AND activation = true) and validate = true)
                   AND effect_date < now() and validate = true
                 ORDER BY effect_date
                 LIMIT 1);
    END IF;

    IF (count IS NULL)
    THEN
        count = 0;
    END IF;

    --check if there is more than one active rate
    IF (
        (SELECT COUNT(*) FROM package_rate
         WHERE packages_package_id = (SELECT package_id FROM packages WHERE package_name = packageName AND activation = true) and validate = true)
            > 1)
    THEN

        --check if there is an outdated rate
        IF EXISTS (SELECT * FROM package_rate
                   WHERE packages_package_id = (SELECT package_id FROM packages WHERE package_name = packageName AND activation = true)
                     AND effect_date = (SELECT MAX(effect_date)  FROM package_rate WHERE packages_package_id = (SELECT package_id FROM packages WHERE package_name = packageName AND activation = true) and validate = true)
                     AND effect_date < now() and validate = true) AND NOT EXISTS (SELECT effect_date FROM package_rate
                                                                                  WHERE packages_package_id = (SELECT package_id FROM packages WHERE package_name = packageName AND activation = true)
                                                                                    and validate = true
                                                                                  GROUP BY effect_date
                                                                                  HAVING COUNT(effect_date) > 1)
        THEN
            --invalidate outdated rate
            UPDATE package_rate
            SET validate = false, updated_at = now()
            WHERE package_rate_id = (SELECT package_rate_id FROM package_rate
                                     WHERE packages_package_id = (SELECT package_id FROM packages WHERE package_name = packageName AND activation = true)
                                       AND effect_date = (SELECT MIN(effect_date)  FROM package_rate WHERE packages_package_id = (SELECT package_id FROM packages WHERE package_name = packageName AND activation = true) and validate = true)
                                       AND effect_date < now() and validate = true LIMIT 1);
        END IF;

        --check if more than one rate have same effective date
        IF EXISTS (SELECT effect_date FROM package_rate
                   WHERE packages_package_id = (SELECT package_id FROM packages WHERE package_name = packageName AND activation = true)
                     and validate = true
                   GROUP BY effect_date
                   HAVING COUNT(effect_date) > 1)
        THEN
            --invalidate rate with least version
            UPDATE package_rate
            SET validate = false, updated_at = now()
            WHERE effect_date = (SELECT effect_date FROM package_rate
                                 WHERE packages_package_id = (SELECT package_id FROM packages WHERE package_name = packageName AND activation = true)
                                   and validate = true
                                 GROUP BY effect_date
                                 HAVING COUNT(effect_date) > 1
                                 LIMIT 1)
              AND package_rate_id = (SELECT package_rate_id FROM package_rate
                                     WHERE effect_date = (SELECT effect_date FROM package_rate
                                                          WHERE packages_package_id = (SELECT package_id FROM packages WHERE package_name = packageName AND activation = true)
                                                            and validate = true
                                                          GROUP BY effect_date
                                                          HAVING COUNT(effect_date) > 1)
                                       AND version_no = (SELECT  MIN(version_no) FROM package_rate
                                                         WHERE packages_package_id = (SELECT package_id FROM packages WHERE package_name = packageName AND activation = true)
                                                           and validate = true
                                                         GROUP BY effect_date
                                                         HAVING COUNT(effect_date) > 1));
        END IF;

--      count = 1;
        count = (SELECT version_no FROM package_rate
                 WHERE validate = true AND packages_package_id = (SELECT package_id FROM packages WHERE package_name = packageName AND activation = true)
                 ORDER BY effect_date
                 LIMIT 1);

        IF (count IS NULL)
        THEN
            count = 0;
        END IF;
    END IF;

END
$$;

alter procedure get_package_rate(varchar, inout integer) owner to postgres;


