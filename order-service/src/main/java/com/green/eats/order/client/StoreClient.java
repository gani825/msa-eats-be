package com.green.eats.order.client;

import com.green.eats.common.model.MenuGetClientRes;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

// store-service에 HTTP 요청을 보내는 FeignClient
// Gateway를 통해 /api/store/menu/{menuId}/stock 경로로 요청
@FeignClient(name = "store-service", url = "http://localhost:8000/api")
public interface StoreClient {

    // 재고 차감: 주문 생성 시 호출 (내가 추가한 기능 유지)
    @PutMapping("/api/store/menu/{menuId}/stock")
    void decreaseStock(@PathVariable Long menuId, @RequestParam int quantity);

    /*
     메뉴 ID 목록으로 메뉴 정보 일괄 조회 (주문 상세 조회용)
     - N+1 방지: menuId별로 따로 조회하지 않고 IN 절로 한 번에 요청
     - 반환: Map<menuId, MenuGetClientRes> → O(1) 접근으로 메뉴명 매핑
     */
    @GetMapping("/api/store/menu/list")
    Map<Long, MenuGetClientRes> getMenuDetail(@RequestParam("menuIds") List<Long> menuIds);
}