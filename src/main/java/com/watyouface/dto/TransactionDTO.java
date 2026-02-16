package com.watyouface.dto;

import java.time.LocalDateTime;

public class TransactionDTO {
    public Long id;
    public Double amount;
    public String status;
    public Long fromUserId;
    public Long toUserId;
    public Long listingId;
    public LocalDateTime createdAt;
}
