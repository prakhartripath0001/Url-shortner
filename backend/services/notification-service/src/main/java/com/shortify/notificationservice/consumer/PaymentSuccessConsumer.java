package com.shortify.notificationservice.consumer;

import com.shortify.notificationservice.event.PaymentSuccessEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PaymentSuccessConsumer {

    @KafkaListener(
            topics = "${app.kafka.topics.payment-success:payment.success}",
            groupId = "notification-service"
    )
    public void consume(PaymentSuccessEvent event) {
        log.info("Received PaymentSuccessEvent via Kafka: userId={}, plan={}, amount={}, paymentId={}",
                event.userId(), event.plan(), event.amount(), event.razorpayPaymentId());
        
        // Simulating sending email
        log.info("📧 Mock Email sent to user ID {}: Success! Your premium plan [{}] has been activated. Thank you!",
                event.userId(), event.plan());
    }
}
