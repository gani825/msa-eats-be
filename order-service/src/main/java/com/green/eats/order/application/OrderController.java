package com.green.eats.order.application;

import com.green.eats.common.auth.UserContext;
import com.green.eats.common.model.ResultResponse;
import com.green.eats.common.model.UserDto;
import com.green.eats.order.application.model.OrderPostReq;
import com.green.eats.order.entity.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public ResultResponse<?> placeOrder(@RequestBody OrderPostReq req) {
        log.info("orderPostReq: {}", req);
        // Gateway가 헤더로 넘긴 유저 정보 꺼내기
        UserDto userDto = UserContext.get();
        Long orderId = orderService.postOrder(userDto.id(), req);
        return ResultResponse.builder()
                .resultMessage("success")
                .resultData(orderId)
                .build();
    }

    @GetMapping
    public ResultResponse<?> getMyOrders() {
        // Gateway가 헤더로 넘긴 유저 정보에서 userId 꺼내기
        UserDto userDto = UserContext.get();
        List<Order> orders = orderService.getMyOrders(userDto.id());
        return ResultResponse.builder()
                .resultMessage(String.format("%d rows", orders.size()))
                .resultData(orders)
                .build();
    }
}