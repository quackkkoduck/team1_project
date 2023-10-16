package com.example.board.controller;

import com.example.board.model.Point;
import com.example.board.repository.PointRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/map") // 주소 매핑을 클래스 수준으로 변경
public class MapController {
    @Autowired
    PointRepository pointRepository;

    @GetMapping
    public String map(Model model) {
        // 데이터를 가져와서 모델에 추가
        model.addAttribute("latitude", 37.4980239);
        model.addAttribute("longitude", 127.027572);
        
        return "map/map"; // Thymeleaf 템플릿 이름
    }

    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        dist = dist * 1.609344;
        return dist;
    }

    private double deg2rad(double deg) {
        return deg * Math.PI / 180.0;
    }

    private double rad2deg(double rad) {
        return rad * 180 / Math.PI;
    }

    @GetMapping("/getPoint")
    @ResponseBody
    public List<Point> getPoint(
            @RequestParam("lat") double lat,
            @RequestParam("lng") double lng,
            @RequestParam("km") int km) {
        List<Point> list = pointRepository.findByLatLng(lat, lng, km, km);
        return list;
    }
}
