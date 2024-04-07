package com.staffs.backend.repository.paymentIntegration;

import com.staffs.backend.entity.paymentIntegration.ConfirmPayment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConfirmPaymentRepository extends JpaRepository<ConfirmPayment, String> {

    boolean existsByTransactionRef(String transRef);

}
