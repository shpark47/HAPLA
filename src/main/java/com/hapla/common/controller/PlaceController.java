package com.hapla.common.controller;

import com.hapla.common.service.GooglePlaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@Controller
@RequiredArgsConstructor
public class PlaceController {

    private final GooglePlaceService googlePlaceService;

    // 장소 상세 페이지
    @GetMapping("/detail/{place_id}")
    public String getPlaceDetails(@PathVariable("place_id") String placeId, Model model) {
        try {
            // 구글 Places API에서 장소 상세 정보 가져오기
            Map<String, Object> placeDetails = googlePlaceService.getPlaceDetails(placeId);
            model.addAttribute("place", placeDetails);
            System.out.println(placeDetails);
            return "/place-detail"; // placeDetail.html로 데이터 전송
        } catch (Exception e) {
            model.addAttribute("error", "장소 정보를 불러오는 데 오류가 발생했습니다.");
            return "error";
        }
    }
}
