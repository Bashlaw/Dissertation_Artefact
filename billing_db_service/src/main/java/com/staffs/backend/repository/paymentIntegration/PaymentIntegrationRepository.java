package com.staffs.backend.repository.paymentIntegration;

import com.staffs.backend.entity.paymentIntegration.PaymentIntegration;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentIntegrationRepository extends JpaRepository<PaymentIntegration, String> {
}
