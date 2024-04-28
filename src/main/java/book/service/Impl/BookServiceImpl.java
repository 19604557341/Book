package book.service.Impl;

import book.entity.Book;
import book.mapper.BookMapper;
import book.service.BookService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class BookServiceImpl extends ServiceImpl<BookMapper, Book>implements BookService {
}
