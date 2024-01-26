package com.pedramero.sms.pmsms.controller;

import com.pedramero.sms.pmsms.service.ProductReturnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("product-return")
public class ProductReturnController {

    @Autowired
    ProductReturnService productReturnService;
}
