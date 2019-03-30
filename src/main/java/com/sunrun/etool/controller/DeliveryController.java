package com.sunrun.etool.controller;

import com.sunrun.etool.data.request.DeliveryNumberData;
import com.sunrun.etool.service.DeliveryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RequestMapping("delivery")
@Controller
public class DeliveryController {

    @Autowired
    private DeliveryService deliveryService;

    @RequestMapping({"index","/"})
    public String DeliveryNumber(){
        return "delivery/index";
    }

    @RequestMapping("start")
    @ResponseBody
    public  HashMap<String, Object> start(@RequestBody DeliveryNumberData data){
        HashMap<String, Object> result = new HashMap<>();
        try {
            result = deliveryService.start(data, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

}