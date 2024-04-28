package book.service;

import book.dto.OrderDto;
import book.entity.Order;
import book.entity.OrderDetails;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface OrderService extends IService<Order> {

    List<OrderDetails> getByIdWithBook(Long orderId);

    void saveWithBook(OrderDto orderDto);
}
