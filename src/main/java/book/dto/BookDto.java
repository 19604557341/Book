package book.dto;

import book.entity.Book;
import lombok.Data;

@Data
public class BookDto extends Book {
    private String categoryName;
    private String updateUserName;
    private String createUserName;
}
