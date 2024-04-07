package com.staffs.backend.repository.paymentSource;

import com.staffs.backend.entity.paymentSource.PaymentURL;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentUrlRepository extends JpaRepository<PaymentURL, Long> {

    List<PaymentURL> findByPaymentSources_sourceCode(String paymentSourceCode);

    PaymentURL findByUrlAndPaymentSources_sourceCode(String url , String paymentSourceCode);

}
