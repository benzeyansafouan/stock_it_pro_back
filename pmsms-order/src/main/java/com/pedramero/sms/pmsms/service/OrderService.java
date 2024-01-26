package com.pedramero.sms.pmsms.service;

import com.pedramero.sms.pmsms.exception.OrderNtFoundException;
import com.pedramero.sms.pmsms.model.Order;
import com.pedramero.sms.pmsms.repository.OrderRepository;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    @Autowired
    OrderRepository orderRepository;

    public Page<Order> getAllByPage(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }

    public List<Order> getAll() {
        return StreamSupport.stream(orderRepository.findAll().spliterator(), false)
            .collect(Collectors.toList());
    }

    public List<Order> search(Integer term){
        return orderRepository.findAllByOrderNumberContaining(term);
    }

    public Order save(Order order){
        order.setId(new ObjectId().toString());
        return orderRepository.save(order);
    }

    public Order update(Order order)
        throws OrderNtFoundException {
        var orderToUpdate = orderRepository.findById(order.getId()).orElseThrow(
            OrderNtFoundException::new);
        return orderRepository.save(order);
    }

    public Order findById(String id) throws OrderNtFoundException {
        var order = orderRepository.findById(id).orElseThrow(OrderNtFoundException::new);
        return order;
    }

    public void delete(String id) throws OrderNtFoundException {
        var supplier = orderRepository.findById(id).orElseThrow(OrderNtFoundException::new);
        orderRepository.delete(supplier);
    }
}
