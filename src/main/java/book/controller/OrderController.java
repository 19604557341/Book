package book.controller;

import book.common.R;
import book.dto.OrderDto;
import book.entity.Book;
import book.entity.Order;
import book.entity.OrderDetails;
import book.service.BookService;
import book.service.OrderService;
import book.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private BookService bookService;

    @Autowired
    private UserService userService;

    @GetMapping("/page")
    public R<Page> page(Integer page, Integer pageSize, Long orderId, Long createUser) {
        Page pageInfo = new Page<>(page, pageSize);
        Page<OrderDto> orderDtoPage = new Page<>();

        log.info("{}",createUser);

        LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(orderId != null, Order::getOrderId, orderId)
                .eq(createUser != null, Order::getCreateUser, createUser)
                .orderByDesc(Order::getCreateTime);

        orderService.page(pageInfo, queryWrapper);

        BeanUtils.copyProperties(pageInfo, orderDtoPage,"records");

        List<Order> records = pageInfo.getRecords();
        List<OrderDto> list = records.stream().map((item) -> {
            OrderDto orderDto = new OrderDto();

            BeanUtils.copyProperties(item, orderDto);

            Long userId = item.getCreateUser();
            String userName = userService.getById(userId).getUserName();
            orderDto.setCreateUserName(userName);
            List<OrderDetails> orderDetails = orderService.getByIdWithBook(item.getOrderId());
            orderDto.setOrderDetails(orderDetails);

            return orderDto;
        }).collect(Collectors.toList());

        orderDtoPage.setRecords(list);

        return R.success(200, orderDtoPage);
    }

    @PostMapping()
    public R<String> save(@RequestBody OrderDto orderDto) {
        // 用于收集库存不足的书名
        List<String> outOfStockBooks = new ArrayList<>();

        // 遍历订单详情，检查库存
        orderDto.getOrderDetails().forEach(item -> {
            Book book = new Book();
            BeanUtils.copyProperties(item, book);

            LambdaQueryWrapper<Book> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Book::getBookName, item.getBookName());
            Book existingBook = bookService.getOne(queryWrapper);

            if (existingBook == null) {
                // 如果找不到书，可以抛出异常或记录错误
                outOfStockBooks.add(item.getBookName());
            } else {
                int inventory = existingBook.getInventory() - item.getQuantity();
                if (inventory < 0) {
                    // 如果库存小于0，记录书名
                    outOfStockBooks.add(item.getBookName());
                } else {
                    // 更新库存和其他信息
                    book.setInventory(inventory);
                    if (inventory == 0) {
                        book.setStatus(0);
                    }
                    book.setBookId(existingBook.getBookId());
                    // 假设有一个方法用于保存或更新书籍信息
                    bookService.updateById(book);
                }
            }
        });

        // 检查是否有库存不足的书
        if (!outOfStockBooks.isEmpty()) {
            // 返回错误信息，包含所有库存不足的书名
            return R.error("库存不足，以下书籍无法购买：" + String.join(", ", outOfStockBooks));
        }

        // 如果没有库存问题，保存订单
        orderService.saveWithBook(orderDto);
        return R.success(200, "新增订单成功");
    }

    @PutMapping()
    public R<String> update(@RequestBody Order order) {

        order.setPaymentTime(LocalDateTime.now());
        orderService.updateById(order);

        return R.success(200, "修改成功");
    }
}
