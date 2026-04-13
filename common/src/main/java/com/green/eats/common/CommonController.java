package com.green.eats.common;

import com.green.eats.common.enumcode.EnumMapper;
import com.green.eats.common.enumcode.EnumMapperValue;
import com.green.eats.common.model.ResultResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


 // 공통 코드 API 제공프론트엔드가 드롭다운, 필터 등에 사용할 Enum 코드 목록을 요청하는 API
@Slf4j
@RestController
@RequiredArgsConstructor
public class CommonController {
    private final EnumMapper enumMapper;

    @GetMapping("code")
    public ResultResponse getCodeList(@RequestParam("code_type") String codeType) {
        // codeType으로 해당 Enum의 코드 목록 조회
        List<EnumMapperValue> enumCodeList = enumMapper.get(codeType);
        return ResultResponse.builder()
                .resultMessage(String.format("%d rows", enumCodeList.size()))
                .resultData(enumCodeList)
                .build();
    }
}