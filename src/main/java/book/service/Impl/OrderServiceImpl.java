package book.service.Impl;

import book.dto.OrderDto;
import book.entity.Order;
import book.entity.OrderDetails;
import book.entity.User;
import book.mapper.OrderMapper;
import book.service.OrderDetailsService;
import book.service.OrderService;
import book.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    @Autowired
    private OrderDetailsService orderDetailsService;

    @Autowired
    private UserService userService;

    @Override
    public List<OrderDetails> getByIdWithBook(Long orderId) {

        Order order = this.getById(orderId);
        User user = userService.getById(order.getCreateUser());

        List<OrderDetails> orderDto = new ArrayList<>();
        BeanUtils.copyProperties(order, orderDto);

        LambdaQueryWrapper<OrderDetails> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderDetails::getOrderId, order.getOrderId());

        return orderDetailsService.list(queryWrapper);
    }

    @Override
    public void saveWithBook(OrderDto orderDto) {
        if (orderDto.getStatus() == 1) {
            orderDto.setPaymentTime(LocalDateTime.now());
        }
        this.save(orderDto);
        Long orderId = orderDto.getOrderId();

        List<OrderDetails> orderDetails = orderDto.getOrderDetails();
        orderDetails = orderDetails.stream().map((item) -> {
            item.setOrderId(orderId);
            return item;
        }).collect(Collectors.toList());

        orderDetailsService.saveBatch(orderDetails);
    }
}
