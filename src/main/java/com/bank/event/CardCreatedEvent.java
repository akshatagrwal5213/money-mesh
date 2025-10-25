package com.bank.event;

import org.springframework.context.ApplicationEvent;

public class CardCreatedEvent
extends ApplicationEvent {
    private final Long cardId;
    private final Long accountId;

    public CardCreatedEvent(Object source, Long cardId, Long accountId) {
        super(source);
        this.cardId = cardId;
        this.accountId = accountId;
    }

    public Long getCardId() {
        return cardId;
    }

    public Long getAccountId() {
        return accountId;
    }
}
