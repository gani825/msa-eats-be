package com.green.eats.order.client;

import com.green.eats.common.model.MenuGetClientRes;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

// store-service에 HTTP 요청을 보내는 FeignClient
// url: yml의 constants.http.store.url 값 사용, 없으면 빈 문자열 → 서비스 디스커버리 수행
@FeignClient(name = "store-service", url = "${constants.http.store.url:}")
public interface StoreClient {

    // 재고 차감 요청 - menuId와 차감할 수량을 전달
    @PutMapping("/api/store/menu/{menuId}/stock")
    void decreaseStock(@PathVariable Long menuId, @RequestParam int quantity);

    // 메뉴 ID 목록으로 메뉴 정보 일괄 조회 (주문 상세 조회용)
    // N+1 방지: IN 절로 한 번에 요청, Map으로 반환해 O(1) 접근
    @GetMapping("/api/store/menu/list")
    Map<Long, MenuGetClientRes> getMenuDetail(@RequestParam("menuIds") List<Long> menuIds);
}