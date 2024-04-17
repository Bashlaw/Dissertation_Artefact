package com.staffs.backend.repository.billingMethod;

import com.staffs.backend.entity.billingMethod.BillingMethod;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BillingMethodRepository extends JpaRepository<BillingMethod, Long> {

    boolean existsByBillingMethodName(String name);

    Optional<BillingMethod> findByBillingMethodNameAndValidate(String billingMethodName , boolean validate);

    Optional<BillingMethod> findByBillingMethodIDAndValidate(Long billingMethodID , boolean validate);

    Optional<BillingMethod> findByBillingMethodName(String billingMethodName);

}
