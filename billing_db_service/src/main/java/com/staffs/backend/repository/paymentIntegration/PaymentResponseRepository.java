package com.staffs.backend.repository.paymentIntegration;

import com.staffs.backend.entity.paymentIntegration.PaymentResponse;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentResponseRepository extends JpaRepository<PaymentResponse, String> {
}
