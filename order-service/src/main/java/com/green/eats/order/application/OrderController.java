package com.green.eats.order.application;

import com.green.eats.common.auth.UserContext;
import com.green.eats.common.model.ResultResponse;
import com.green.eats.common.model.UserDto;
import com.green.eats.order.application.model.OrderGetDetailRes;
import com.green.eats.order.application.model.OrderGetPageRes;
import com.green.eats.order.application.model.OrderPostReq;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    // 주문 생성: POST /api/order
    @PostMapping
    public ResultResponse<?> postOrder(@RequestBody OrderPostReq req) {
        log.info("orderPostReq: {}", req);
        UserDto userDto = UserContext.get(); // Gateway JWT 필터에서 주입된 사용자 정보
        Long orderId = orderService.postOrder(userDto.id(), req);
        return ResultResponse.builder()
                .resultMessage("success")
                .resultData(orderId)
                .build();
    }

    /*
     주문 목록 조회: GET /api/order?last_id=xxx
     - lastId: 커서 페이지네이션용. 첫 요청 시 생략, 이후 응답의 nextLastId 전달
     - required = false: 파라미터 없어도 첫 페이지로 정상 동작
     */
    @GetMapping
    public ResultResponse<?> getOrderList(@RequestParam(required = false) Long lastId) {
        log.info("lastId: {}", lastId);
        OrderGetPageRes data = orderService.getOrders(lastId);
        return ResultResponse.builder()
                .resultMessage(String.format("%d rows", data.getOrders().size()))
                .resultData(data)
                .build();
    }

    // 주문 상세 조회: GET /api/order/{orderId}
    @GetMapping("/{orderId}")
    public ResultResponse<?> getOrderDetail(@PathVariable Long orderId) {
        List<OrderGetDetailRes> data = orderService.getOrderDetail(orderId);
        return ResultResponse.builder()
                .resultMessage(String.format("%d rows", data.size()))
                .resultData(data)
                .build();
    }
}