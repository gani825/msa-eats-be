package com.green.eats.store.application;

import com.green.eats.common.auth.UserContext;
import com.green.eats.common.enumcode.EnumMapper;
import com.green.eats.common.model.MenuGetClientRes;
import com.green.eats.common.model.ResultResponse;
import com.green.eats.common.model.UserDto;
import com.green.eats.store.application.model.MenuGetRes;
import com.green.eats.store.application.model.MenuPostReq;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/store")
public class StoreController {
    private final StoreService storeService;
    private final EnumMapper enumMapper; // Enum 코드 목록 조회용

    // 메뉴 등록 (관리자용)
    @PostMapping("/menu")
    public ResultResponse<?> addMenu(@Valid @RequestBody MenuPostReq req) {
        log.info("menuPostReq: {}", req);

        // MenuPostReq를 받아서 메뉴 등록 처리
        storeService.addMenu(req);

        return ResultResponse.builder()
                .resultMessage("success")
                .build();
    }

    // 전체 메뉴 목록 조회 (일반 사용자용 - 홈 화면)
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

    // 카테고리 코드 목록 조회
    @GetMapping("/code")
    public ResultResponse<?> getCodeList(@RequestParam String code_type) {
        // Enum 코드 목록 조회 (예: code_type=menuCategory)
        return ResultResponse.builder()
                .resultMessage("success")
                .resultData(enumMapper.get(code_type))
                .build();
    }

    // 재고 차감 (주문 시 호출)
    @PutMapping("/menu/{menuId}/stock")
    public void decreaseStock(@PathVariable Long menuId, @RequestParam int quantity) {
        // 해당 메뉴의 재고를 차감
        storeService.decreaseStock(menuId, quantity);
    }

    // 메뉴 ID 목록으로 메뉴 상세 정보 일괄 조회 (서비스 간 통신 전용)
    @GetMapping("/menu/list")
    public Map<Long, MenuGetClientRes> getMenusByIds(@RequestParam List<Long> menuIds) {
        log.info("menuIds: {}, size: {}", menuIds, menuIds.size());
        return storeService.getMenuListByIds(menuIds);
    }
}