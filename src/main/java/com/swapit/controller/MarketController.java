package com.swapit.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/market")
public class MarketController {
    private final List<Map<String, Object>> products = List.of(
            Map.of(
                    "productId", "washer-fhp1411z9p",
                    "category", "세탁기",
                    "name", "LG 11kg Front Load Washing Machine",
                    "price", 62900,
                    "currency", "INR",
                    "description", "AI Direct Drive와 Steam+ 기능을 갖춘 LG India 공식 세탁기 데모 상품입니다.",
                    "productUrl", "https://www.lg.com/in/laundry/front-loading-washing-machines/fhp1411z9p/"
            ),
            Map.of(
                    "productId", "fridge-gl-t422vpzx",
                    "category", "냉장고",
                    "name", "LG 398L Double Door Refrigerator",
                    "price", 74900,
                    "currency", "INR",
                    "description", "Convertible 기능과 Wi-Fi 제어를 지원하는 LG India 공식 냉장고 데모 상품입니다.",
                    "productUrl", "https://www.lg.com/in/refrigerators/double-door-refrigerators/gl-t422vpzx/"
            ),
            Map.of(
                    "productId", "ac-us-q19bnze3",
                    "category", "에어컨",
                    "name", "LG 5 Star 1.5 Ton Split AC",
                    "price", 45900,
                    "currency", "INR",
                    "description", "Dual Inverter와 AI Convertible 6-in-1 기능을 갖춘 LG India 공식 에어컨 데모 상품입니다.",
                    "productUrl", "https://www.lg.com/in/air-conditioners/split-air-conditioners/us-q19bnze3/"
            )
    );

    @GetMapping("/products")
    public List<Map<String, Object>> getProducts() {
        return products;
    }

    @GetMapping("/products/{productId}")
    public Map<String, Object> getProduct(@PathVariable String productId) {
        return products.stream()
                .filter(product -> productId.equals(product.get("productId")))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Product not found: " + productId));
    }

    @PostMapping("/orders")
    public Map<String, Object> createOrder(@RequestBody Map<String, Object> request) {
        return Map.of(
                "orderId", "ORDER-DEMO-001",
                "status", "CREATED",
                "message", "데모 주문이 생성되었습니다.",
                "request", request
        );
    }
}
