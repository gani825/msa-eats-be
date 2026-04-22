package com.green.eats.store.application;

import com.green.eats.common.model.MenuGetClientRes;
import com.green.eats.store.application.model.MenuGetRes;
import com.green.eats.store.application.model.MenuPostReq;
import com.green.eats.store.entity.Menu;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StoreService {
    private final MenuRepository menuRepository;

    // 메뉴 등록
    public void addMenu(MenuPostReq req) {
        Menu menu = new Menu(req);
        menuRepository.save(menu);
    }

    // 전체 메뉴 목록 조회
    public List<MenuGetRes> getAllMenus() {
        List<Menu> menuList = menuRepository.findAll();

        List<MenuGetRes> resList = new ArrayList<>(menuList.size());
        for (Menu menu : menuList) {
            resList.add(new MenuGetRes(menu));
        }
        return resList;
    }

    // 재고 차감 (주문 시 FeignClient로 호출됨)
    @Transactional
    public void decreaseStock(Long menuId, int quantity) {
        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new IllegalArgumentException("메뉴를 찾을 수 없습니다."));
        menu.removeStock(quantity);
        menuRepository.save(menu);
    }

    /*
     menuId 목록으로 메뉴 일괄 조회 후 Map으로 반환
     - order-service에서 주문 상세 조회 시 FeignClient로 호출
     - findAllById: IN 절 단일 쿼리 (N+1 방지)
     - Map<menuId, MenuGetClientRes>: order-service에서 O(1)로 메뉴명 접근 가능
     */
    public Map<Long, MenuGetClientRes> getMenuListByIds(List<Long> menuIds) {
        List<Menu> menus = menuRepository.findAllById(menuIds);
        return menus.stream()
                .collect(Collectors.toMap(
                        Menu::getId,
                        menu -> MenuGetClientRes.builder()
                                .menuId(menu.getId())
                                .name(menu.getName())
                                .price(menu.getPrice())
                                .build()
                ));
    }
}