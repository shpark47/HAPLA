package com.hapla.place.controller;

import com.hapla.place.model.service.PlaceService;
import com.hapla.place.model.vo.Place;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequiredArgsConstructor
public class PlaceController {

    private final PlaceService placeService;

    // 장소 상세 페이지
    @GetMapping("/detail/{place_id}/{type}")
    public String getPlaceDetails(@PathVariable("place_id") String placeId, @PathVariable("type") String type, Model model) {
        try {
            // 구글 Places API에서 장소 상세 정보 가져오기
            Map<String, Object> placeDetails = placeService.getPlaceDetails(placeId);
            int count = placeService.countStar(placeId);

            model.addAttribute("placeDetails", placeDetails);
            model.addAttribute("placeId", placeId);
            model.addAttribute("type", type);
            model.addAttribute("count", count);
            return "/place-detail"; // placeDetail.html로 데이터 전송
        } catch (Exception e) {
            throw new com.hapla.exception.Exception("오류 발생");
        }
    }

    @PostMapping("/star")
    @ResponseBody
    public String star(@RequestBody Place place) {

        return null;
    }
}
