package book.controller;

import book.common.R;
import book.dto.CategoryDto;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private BookService bookService;

    @Autowired
    private UserService userService;

    @GetMapping("/page")
    public R<Page> page(Integer page, Integer pageSize, String categoryName) {
        Page pageInfo = new Page<>(page, pageSize);
        Page<CategoryDto> categoryDtoPage = new Page<>();

        LambdaQueryWrapper <Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(categoryName != null, Category::getCategoryName, categoryName)
                .orderByDesc(Category::getUpdateTime);

        categoryService.page(pageInfo, queryWrapper);
        BeanUtils.copyProperties(pageInfo, categoryDtoPage, "records");

        List<Category> records = pageInfo.getRecords();
        List<CategoryDto> list = records.stream().map((item) -> {
            CategoryDto categoryDto = new CategoryDto();

            BeanUtils.copyProperties(item, categoryDto);

            categoryDto.setCreateUserName(userService.getById(item.getCreateUser()).getUserName());
            categoryDto.setUpdateUserName(userService.getById(item.getUpdateUser()).getUserName());

            return categoryDto;
        }).collect(Collectors.toList());

        categoryDtoPage.setRecords(list);

        log.info("记录：{}条", pageInfo.getTotal());

        if (pageInfo.getRecords() == null || pageInfo.getRecords().isEmpty()) {
            return R.error("暂无数据");
        }

        return R.success(200, categoryDtoPage);
    }

    @PostMapping
    public R<String> save(@RequestBody Category category) {

        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Category::getCategoryName, category.getCategoryName());
        Category c = categoryService.getOne(queryWrapper);

        if (c != null) {
            return R.error("分类名称已存在");
        }

        categoryService.save(category);

        return R.success(200, "新增分类成功");
    }

    @PutMapping
    public R<String> update(@RequestBody Category category) {

        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Category::getCategoryName, category.getCategoryName());
        Category c = categoryService.getOne(queryWrapper);

        if (c != null) {
            return R.error("分类名称已存在");
        }

        categoryService.updateById(category);

        return R.success(200, "修改分类成功");
    }

    @GetMapping("/{categoryId}")
    public R<Category> getById(@PathVariable Long categoryId) {
        Category category = categoryService.getById(categoryId);
        if (category == null) {
            return R.error("未查询到分类");
        }

        return R.success(200,  category);
    }

    @DeleteMapping("/{categoryId}")
    public R<String> delete(@PathVariable Long categoryId) {

        LambdaQueryWrapper<Book> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Book::getCategoryId, categoryId);
        List<Book> book = bookService.list(queryWrapper);
        log.info("b：{}", book);
        if (!book.isEmpty()) {
            return R.error("该分类下有图书，不可删除");
        }

        categoryService.removeById(categoryId);

        return R.success(200, "删除分类成功");
    }
}
