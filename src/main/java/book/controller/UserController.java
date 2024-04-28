package book.controller;

import book.common.JwtUtils;
import book.common.R;
import book.entity.User;
import book.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public R<String> login(HttpServletRequest request, @RequestBody User user) {
        JwtUtils jwtUtils = new JwtUtils();

        String password = user.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

         LambdaQueryWrapper  <User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getAccount,user.getAccount());
        User u = userService.getOne(queryWrapper);

        if (u == null) {
            return R.error("用户不存在");
        }

        if(!u.getPassword().equals(password)) {
            return R.error("密码错误");
        }

        if(u.getStatus() == 0){
            return R.error("账号已禁用");
        }

        String jwtToken = jwtUtils.getToken(u.getUserId());
        jwtToken = jwtToken + "." + u.getIdentity() + "-" + u.getUserName();
        log.info(jwtToken);
        return R.success(200, jwtToken);
    }

    @GetMapping("/page")
    public R<Page> page(Integer page, Integer pageSize, String userName) {
        Page pageInfo = new Page(page, pageSize);

        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(userName != null, User::getUserName, userName)
                .orderByAsc(User::getIdentity)
                .orderByDesc(User::getUpdateTime);

        userService.page(pageInfo, queryWrapper);

        log.info("记录：{}条", pageInfo.getTotal());

        if (pageInfo.getRecords() == null || pageInfo.getRecords().isEmpty()) {
            return R.error("暂无数据");
        }

        return R.success(200,  pageInfo);
    }

    @PostMapping
    public R<String> save(@RequestBody User user) {

        LambdaQueryWrapper<User> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.eq(User::getAccount, user.getAccount());
        User uAccount = userService.getOne(queryWrapper1);

        if (uAccount != null) {
            return R.error("账号已存在");
        }

        LambdaQueryWrapper<User> queryWrapper2 = new LambdaQueryWrapper<>();
        queryWrapper2.eq(User::getNumber, user.getNumber());
        User uNumber = userService.getOne(queryWrapper2);

        if (uNumber != null) {
            return R.error("手机号已存在");
        }

        user.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        user.setStatus(1);
        userService.save(user);

        return R.success(200, "新增员工成功");
    }

    @PutMapping
    public R<String> update(@RequestBody User user) {

        log.info("{}", user);

        LambdaQueryWrapper<User> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.eq(User::getAccount, user.getAccount());
        User uAccount = userService.getOne(queryWrapper1);

        if (uAccount != null) {
            if (Objects.equals(uAccount.getUserId(), user.getUserId())) {
                userService.updateById(user);
                return R.success(200, "修改成功");
            }
            return R.error("账号已存在");
        }

        LambdaQueryWrapper<User> queryWrapper2 = new LambdaQueryWrapper<>();
        queryWrapper2.eq(User::getNumber, user.getNumber());
        User uNumber = userService.getOne(queryWrapper2);

        if (uNumber != null) {
            if (Objects.equals(uNumber.getUserId(), user.getUserId())) {
                userService.updateById(user);
                return R.success(200, "修改成功");
            }
            return R.error("手机号已存在");
        }

        userService.updateById(user);

        return R.success(200, " 修改员工信息成功" );
    }

    @GetMapping("/{userId}")
    public R<User> getById(@PathVariable Long userId) {
        User user = userService.getById(userId);
        if (user == null) {
            return R.error("未查询到员工");
        }

        return R.success(200,  user);
    }

    @DeleteMapping("/{userId}")
    public R<String> delete( @PathVariable Long userId) {

        userService.removeById(userId);

        return R.success(200, "删除成功");
    }
}
