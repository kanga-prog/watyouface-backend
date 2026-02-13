package com.watyouface.dto;

import java.time.LocalDateTime;

public class TransactionDTO {
    public Double amount;
    public Long fromUserId;
    public Long toUserId;
    public Long listingId;
    public LocalDateTime createdAt;
}
