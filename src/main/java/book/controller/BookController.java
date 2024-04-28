package book.controller;

import book.common.R;
import book.dto.BookDto;
import book.entity.Book;
import book.entity.Category;
import book.entity.User;
import book.service.BookService;
import book.service.CategoryService;
import book.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/book")
public class BookController {

    @Autowired
    private BookService bookService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private UserService userService;


    @GetMapping("/page")
    public R<Page> page(Integer page, Integer pageSize, String bookName, Long categoryId) {
        Page pageInfo = new Page<>(page, pageSize);
        Page<BookDto> bookDtoPage = new Page<>();

        LambdaQueryWrapper<Book> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(bookName != null, Book::getBookName, bookName)
                .eq(categoryId != null, Book::getCategoryId, categoryId)
                .orderByDesc(Book::getStatus)
                .orderByDesc(Book::getUpdateTime);

        bookService.page(pageInfo, queryWrapper);

        BeanUtils.copyProperties(pageInfo, bookDtoPage, "records");


        List<Book> records = pageInfo.getRecords();
        List<BookDto> list = records.stream().map((item) -> {
            BookDto bookDto = new BookDto();

            BeanUtils.copyProperties(item, bookDto);

            bookDto.setCategoryName(categoryService.getById(item.getCategoryId()).getCategoryName());
            bookDto.setUpdateUserName(userService.getById(item.getUpdateUser()).getUserName());
            bookDto.setCreateUserName(userService.getById(item.getCreateUser()).getUserName());

            return bookDto;
        }).collect(Collectors.toList());

        bookDtoPage.setRecords(list);

        log.info("记录：{}条", pageInfo.getTotal());

        if (pageInfo.getRecords() == null || pageInfo.getRecords().isEmpty()) {
           return R.error("暂无数据");
        }

        return R.success(200, bookDtoPage);
    }

    @PostMapping
    public R<String> save(@RequestBody Book book) {

        book.setStatus(1);

        LambdaQueryWrapper<Book> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Book::getBookName, book.getBookName());
        Book b = bookService.getOne(queryWrapper);

        if (b != null) {
            b.setInventory(b.getInventory() + book.getInventory());
            bookService.updateById(b);
            return R.success(200, "图书存在，已增加库存数量");
        }

        bookService.save(book);
        return R.success(200, "新增图书成功");
    }

    @PutMapping
    public R<String> update(@RequestBody Book book) {

        LambdaQueryWrapper<Book> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Book::getBookName, book.getBookName());
        Book b = bookService.getOne(queryWrapper);

        if (b != null) {
            if (Objects.equals(book.getBookId(), b.getBookId())) {
                if (book.getInventory() == 0) {
                    book.setStatus(0);
                }
                bookService.updateById(book);
                return R.success(200, "修改图书成功");
            }
            return R.error("图书名称已存在，无法更改");
        }

        bookService.updateById(book);
        return R.success(200, "修改图书成功");
    }

    @GetMapping("/{bookId}")
    public R<Book> getById(@PathVariable Long bookId) {
        Book book = bookService.getById(bookId);
        if (book == null) {
            return R.error("未查询到图书");
        }

        return R.success(200,  book);
    }

    @DeleteMapping("/{bookId}")
    public R<String> delete(@PathVariable Long bookId) {

        bookService.removeById(bookId);

        return R.success(200, "删除图书成功");
    }
}
