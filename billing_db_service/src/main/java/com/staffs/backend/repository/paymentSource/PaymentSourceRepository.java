package com.staffs.backend.repository.paymentSource;

import com.staffs.backend.entity.paymentSource.PaymentSource;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentSourceRepository extends JpaRepository<PaymentSource, Long> {

    boolean existsBySourceCode(String sourceCode);

    PaymentSource findBySourceCode(String sourceCode);

    List<PaymentSource> findByCountryList_shortCode(String shortCode);

}
