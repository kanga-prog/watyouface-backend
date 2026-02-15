package com.watyouface.controller;

import com.watyouface.repository.TransactionRepository;
import com.watyouface.security.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionRepository transactionRepository;
    private final JwtUtil jwtUtil;

    public TransactionController(TransactionRepository transactionRepository, JwtUtil jwtUtil) {
        this.transactionRepository = transactionRepository;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping
    public Object all(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        Long currentUserId = jwtUtil.getUserIdFromHeader(authHeader);
        boolean isAdmin = "ADMIN".equals(jwtUtil.getRoleFromHeader(authHeader));

        if (isAdmin) {
            return transactionRepository.findAll();
        }
        return transactionRepository.findByFromUserIdOrToUserId(currentUserId, currentUserId);
    }
}
