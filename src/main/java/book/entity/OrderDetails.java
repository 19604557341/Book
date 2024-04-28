package book.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class OrderDetails{
    @TableId
    private Long orderDetailsId;
    private Long orderId;
    private String bookName;
    private Integer quantity;
    private Integer price;
}
