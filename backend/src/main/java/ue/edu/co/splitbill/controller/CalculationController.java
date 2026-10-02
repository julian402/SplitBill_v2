package ue.edu.co.splitbill.controller;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ue.edu.co.splitbill.dto.QuickSplitRequest;
import ue.edu.co.splitbill.dto.QuickSplitResponse;
import ue.edu.co.splitbill.service.SettlementService;

// Calculos que no se guardan en la base
@RestController
@RequestMapping("/api/calculations")
public class CalculationController {

    private final SettlementService settlementService;

    public CalculationController(SettlementService settlementService) {
        this.settlementService = settlementService;
    }

    // POST /api/calculations/quick-split: cuenta rapida con propina
    @PostMapping("/quick-split")
    public QuickSplitResponse quickSplit(@Valid @RequestBody QuickSplitRequest request) {
        return this.settlementService.quickSplit(request);
    }
}
