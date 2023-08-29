package org.zhangyan.controller;


import javax.websocket.server.PathParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/data-track")
public class DataTrackController {

    @GetMapping("/test")
    public String Test(@PathParam("input") String input) {
        return input;
    }


}
