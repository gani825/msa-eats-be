package com.green.eats.store.application;

import com.green.eats.common.auth.UserContext;
import com.green.eats.common.model.ResultResponse;
import com.green.eats.common.model.UserDto;
import com.green.eats.store.application.model.MenuGetRes;
import com.green.eats.store.application.model.MenuPostReq;
import jakarta.validation.Valid;
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
public class StoreController {
    private final StoreService storeService;

    @PostMapping("/menu")
    public ResultResponse<?> addMenu(@Valid @RequestBody MenuPostReq req) {
        log.info("menuPostReq: {}", req);

        // MenuPostReq를 받아서 메뉴 등록 처리
        storeService.addMenu(req);

        return ResultResponse.builder()
                .resultMessage("success")
                .build();
    }

    @GetMapping("/menu")
    public ResultResponse<?> getAllMenus() {
        // Gateway가 헤더로 넘긴 유저 정보
        UserDto userDto = UserContext.get();
        log.info("userDto: {}", userDto);

        List<MenuGetRes> menus = storeService.getAllMenus();
        return ResultResponse.builder()
                .resultMessage(String.format("%d rows", menus.size()))
                .resultData(menus)
                .build();
    }
}