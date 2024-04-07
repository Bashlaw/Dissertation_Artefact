package com.staffs.backend.repository.billingMethod;

import com.staffs.backend.entity.billingMethod.BillingMethod;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BillingMethodRepository extends JpaRepository<BillingMethod, Long> {

    boolean existsByBillingMethodName(String name);

    BillingMethod findByBillingMethodNameAndValidate(String billingMethodName , boolean validate);

    BillingMethod findByBillingMethodIDAndValidate(Long billingMethodID , boolean validate);

    BillingMethod findByBillingMethodName(String billingMethodName);

}
