package com.shortify.paymentservice.repository;

import com.shortify.paymentservice.entity.PaymentOrder;
import com.shortify.paymentservice.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentOrderRepository extends JpaRepository<PaymentOrder, Long> {

    /** Primary lookup for payment verification — by Razorpay's order ID. */
    Optional<PaymentOrder> findByRazorpayOrderId(String razorpayOrderId);

    /** Check for duplicate verification attempts. */
    Optional<PaymentOrder> findByRazorpayPaymentId(String razorpayPaymentId);

    /** User payment history (for billing page). */
    Page<PaymentOrder> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    /** Count failed orders (for fraud detection). */
    long countByUserIdAndStatus(Long userId, OrderStatus status);
}
