package com.swapit.controller;

import com.swapit.dto.FinalValuationRequest;
import com.swapit.dto.SwapRequestResponse;
import com.swapit.service.SwapRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    private final SwapRequestService swapRequestService;

    @GetMapping("/swap-requests")
    public List<SwapRequestResponse> getSwapRequests() {
        return swapRequestService.getAll();
    }

    @PostMapping("/swap-requests/{id}/final-valuation")
    public SwapRequestResponse completeFinalValuation(
            @PathVariable long id,
            @RequestBody FinalValuationRequest request
    ) {
        return swapRequestService.adminCompleteFinalValuation(id, request);
    }

    @PostMapping("/swap-requests/{id}/re-review/complete")
    public SwapRequestResponse completeReReview(@PathVariable long id) {
        return swapRequestService.completeMockReReview(id);
    }

    @GetMapping("/operation-summary")
    public Map<String, Object> getOperationSummary() {
        List<SwapRequestResponse> requests = swapRequestService.getAll();
        return Map.of(
                "totalSwapRequests", requests.size(),
                "availableCrewCalls", swapRequestService.getAvailableCalls().size(),
                "message", "MVP 관리자 요약 Mock 데이터입니다."
        );
    }
}
