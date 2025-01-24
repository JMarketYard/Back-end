package com.example.demo.controller.general;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/")
public class PaymentConnectController {
    @GetMapping("/payment/approve")
    public String approvePayment(@RequestParam String pg_token) {
        return "redirect:http://localhost:3000/api/payments/approve?pgToken=" + pg_token;
    }
}