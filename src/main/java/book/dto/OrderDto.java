package book.dto;

import book.entity.Order;
import book.entity.OrderDetails;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class OrderDto extends Order {
    private String createUserName;
    private List<OrderDetails> orderDetails = new ArrayList<>();
}
