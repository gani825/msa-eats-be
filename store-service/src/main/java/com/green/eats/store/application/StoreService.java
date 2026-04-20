package com.green.eats.store.application;

import com.green.eats.store.application.model.MenuGetRes;
import com.green.eats.store.application.model.MenuPostReq;
import com.green.eats.store.entity.Menu;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StoreService {
    private final MenuRepository menuRepository;

    public void addMenu(MenuPostReq req) {
        // MenuPostReq를 받아서 Menu 엔티티로 변환 후 DB 저장
        Menu menu = new Menu(req);
        menuRepository.save(menu); // INSERT 쿼리 실행
    }

    public List<MenuGetRes> getAllMenus() {
        List<Menu> menuList = menuRepository.findAll();

        // 박스갈이 작업 (Entity → DTO 변환)
        List<MenuGetRes> resList = new ArrayList<>(menuList.size());
        for (Menu menu : menuList) {
            MenuGetRes menuGetRes = new MenuGetRes(menu);
            resList.add(menuGetRes);
        }

        //보지마세요.
        List<MenuGetRes> resList2 = menuList.stream()
                .map(MenuGetRes::new).toList();

        return resList;
    }
}