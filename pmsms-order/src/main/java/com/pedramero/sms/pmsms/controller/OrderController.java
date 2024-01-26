package com.pedramero.sms.pmsms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pedramero.sms.pmsms.config.Logger;
import com.pedramero.sms.pmsms.exception.OrderNtFoundException;
import com.pedramero.sms.pmsms.model.Order;
import com.pedramero.sms.pmsms.model.dto.OrderDto;
import com.pedramero.sms.pmsms.service.OrderService;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
public class OrderController implements Logger {

    @Autowired
    OrderService orderService;
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    ObjectMapper objectMapper;

    @GetMapping("/all/paging")
    public ResponseEntity<Page<OrderDto>> getAllByPage(@RequestParam int page,
                                                       @RequestParam int pageSize) {
        var pageable = (page == -1 || pageSize == -1) ? Pageable.unpaged() :
            PageRequest.of(page, pageSize, Sort.by("name"));
        var orderPage = orderService.getAllByPage(pageable);
        var orders =
            orderPage.map(order -> modelMapper.map(order, OrderDto.class)).stream()
                .collect(
                    Collectors.toList());
        return ResponseEntity.ok(
            new PageImpl<>(orders, pageable, orderPage.getTotalElements()));
    }

    @GetMapping("/all")
    public ResponseEntity<List<OrderDto>> getAll() {
        var orders = orderService.getAll();
        var orderDtos =
            orders.stream().map(order -> modelMapper.map(order, OrderDto.class))
                .collect(
                    Collectors.toList());
        return ResponseEntity.ok(orderDtos);
    }

    @GetMapping("/search/{term}")
    public ResponseEntity<List<OrderDto>> search(@PathVariable Integer term) {
        var foundOrders = orderService.search(term);
        var foundOrderDtos =
            foundOrders.stream().map(order -> modelMapper.map(order, OrderDto.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(foundOrderDtos);
    }

    @PostMapping("/save")
    public ResponseEntity<OrderDto> save(@RequestParam String order){
        try {
            var orderObject = objectMapper.readValue(order,OrderDto.class);
            var newOrder = new Order();
            modelMapper.map(orderObject,newOrder);
            var savedOrder = orderService.save(newOrder);
            var savedOrderDto = modelMapper.map(savedOrder,OrderDto.class);
            return ResponseEntity.ok(savedOrderDto);
        }catch (IOException e){
            getLogger().error("ERROR WHILE SAVING ORDER{}", order, e);
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDto> get(@PathVariable("id") String id){
        try {
            var order = orderService.findById(id);
            var orderDto = modelMapper.map(order,OrderDto.class);
            return ResponseEntity.ok(orderDto);
        } catch (OrderNtFoundException exception){
            getLogger().warn("ORDER NOT FOUND EXCEPTION {}", id, exception);
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/update")
    public ResponseEntity<OrderDto> update(@RequestParam String order){
        try {
            var orderObject = objectMapper.readValue(order,OrderDto.class);
            var newOrder = new Order();
            modelMapper.map(orderObject,newOrder);
            var updatedOrder = orderService.update(newOrder);
            var orderDto = modelMapper.map(updatedOrder,OrderDto.class);
            return ResponseEntity.ok(orderDto);
        } catch (OrderNtFoundException | IOException exception){
            getLogger().warn("ERROR WHILE UPDATING ORDER {}", exception);
            return ResponseEntity.badRequest().build();
        }
    }
    @DeleteMapping("/delete/{id}")
    public HttpStatus delete(@PathVariable String id){
        try {
            orderService.delete(id);
            return HttpStatus.OK;
        } catch (OrderNtFoundException e){
            getLogger().warn("ERROR WHILE DELETING ORDER {}", e);
            return HttpStatus.BAD_REQUEST;
        }
    }
}
