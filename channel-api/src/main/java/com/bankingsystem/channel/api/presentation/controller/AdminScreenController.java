package com.bankingsystem.channel.api.presentation.controller;

import com.bankingsystem.channel.api.application.service.CoreBankingAdminScreenService;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/screens")
public class AdminScreenController {

    private final CoreBankingAdminScreenService coreBankingAdminScreenService;

    public AdminScreenController(CoreBankingAdminScreenService coreBankingAdminScreenService) {
        this.coreBankingAdminScreenService = coreBankingAdminScreenService;
    }

    @GetMapping("/core-banking-overview")
    public Map<String, Object> readCoreBankingOverviewScreen() {
        return coreBankingAdminScreenService.buildScreen();
    }
}
