package book.dto;

import book.entity.Category;
import lombok.Data;

@Data
public class CategoryDto extends Category {
    private String createUserName;
    private String updateUserName;
}
