package com.bank.event;

// CardCreatedEvent is in same package; explicit import removed
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class CardEventListener {
    private static final Logger logger = LoggerFactory.getLogger(CardEventListener.class);

    @EventListener
    public void onCardCreated(CardCreatedEvent ev) {
        logger.info("Card created event received for cardId={} accountId={}", (Object)ev.getCardId(), (Object)ev.getAccountId());
    }
}
