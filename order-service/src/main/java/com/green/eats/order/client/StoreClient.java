package com.green.eats.order.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

// store-service에 HTTP 요청을 보내는 FeignClient
// Gateway를 통해 /api/store/menu/{menuId}/stock 경로로 요청
@FeignClient(name = "store-service", url = "http://localhost:8000/api")
public interface StoreClient {

    // 재고 차감 요청 - menuId와 차감할 수량을 전달
    @PutMapping("/store/menu/{menuId}/stock")
    void decreaseStock(@PathVariable Long menuId, @RequestParam int quantity);
}