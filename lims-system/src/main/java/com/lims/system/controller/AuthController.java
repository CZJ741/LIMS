package com.lims.system.controller;

import cn.dev33.satoken.secure.BCrypt;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import com.lims.common.exception.BizException;
import com.lims.common.result.Result;
import com.lims.system.audit.AuditLog;
import com.lims.system.context.UserContext;
import com.lims.system.dto.CurrentUserResp;
import com.lims.system.dto.LoginReq;
import com.lims.system.entity.SysMenu;
import com.lims.system.entity.SysUser;
import com.lims.system.service.IPermissionService;
import com.lims.system.service.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Tag(name = "认证中心", description = "登录登出与当前用户信息")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final IUserService userService;
    private final IPermissionService permissionService;
    private final StringRedisTemplate stringRedisTemplate;

    private static final String LOGIN_FAIL_PREFIX = "lims:auth:login_fail:";
    private static final int MAX_RETRY_COUNT = 5;
    private static final long LOCK_MINUTES = 30;

    @Operation(summary = "用户名密码登录")
    @PostMapping("/login")
    public Result<SaTokenInfo> login(@Valid @RequestBody LoginReq req) {
        String lockKey = LOGIN_FAIL_PREFIX + req.getUsername();
        String failCountStr = stringRedisTemplate.opsForValue().get(lockKey);
        int failCount = failCountStr != null ? Integer.parseInt(failCountStr) : 0;

        if (failCount >= MAX_RETRY_COUNT) {
            throw new BizException("密码连续输错达 5 次，账号已被锁定 30 分钟，请稍后再试");
        }

        SysUser user = userService.getByUsername(req.getUsername());
        if (user == null || !BCrypt.checkpw(req.getPassword(), user.getPassword())) {
            failCount++;
            stringRedisTemplate.opsForValue().set(lockKey, String.valueOf(failCount), LOCK_MINUTES, TimeUnit.MINUTES);
            int remain = MAX_RETRY_COUNT - failCount;
            if (remain > 0) {
                throw new BizException("用户名或密码错误，剩余重试次数: " + remain);
            } else {
                throw new BizException("密码已连续输错 5 次，账号已锁定 30 分钟");
            }
        }

        if (user.getStatus() != 1) {
            throw new BizException("该账号已被停用，请联系管理员");
        }

        // 登录成功清除输错计数器
        stringRedisTemplate.delete(lockKey);

        // Sa-Token 登录
        StpUtil.login(user.getId());
        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
        return Result.ok("登录成功", tokenInfo);
    }

    @Operation(summary = "退出登录")
    @PostMapping("/logout")
    @AuditLog(module = "认证中心", operation = "LOGOUT", description = "用户登出退出")
    public Result<Void> logout() {
        if (StpUtil.isLogin()) {
            StpUtil.logout();
        }
        return Result.ok();
    }

    @Operation(summary = "获取当前登录用户详情与权限")
    @GetMapping("/current-user")
    public Result<CurrentUserResp> getCurrentUser() {
        Long userId = StpUtil.getLoginIdAsLong();
        UserContext context = permissionService.loadUserContext(userId);
        List<SysMenu> menuTree = permissionService.getMenuTreeByUserId(userId);

        CurrentUserResp resp = CurrentUserResp.builder()
                .user(context)
                .menus(menuTree)
                .permissions(context != null ? context.getPermissions() : List.of())
                .roles(context != null ? context.getRoles() : List.of())
                .positions(context != null ? context.getPositions() : List.of())
                .build();

        return Result.ok(resp);
    }
}
