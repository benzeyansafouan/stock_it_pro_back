package com.pedramero.sms.pmsms.service;

import com.pedramero.sms.pmsms.repository.ProductReturnRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductReturnService {

    @Autowired
    ProductReturnRepository productReturnRepository;
}
