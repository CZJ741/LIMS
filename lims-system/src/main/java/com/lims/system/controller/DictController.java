package com.lims.system.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lims.common.exception.BizException;
import com.lims.common.result.Result;
import com.lims.system.audit.AuditLog;
import com.lims.system.entity.SysDictCategory;
import com.lims.system.entity.SysDictItem;
import com.lims.system.mapper.SysDictCategoryMapper;
import com.lims.system.mapper.SysDictItemMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@Tag(name = "字典管理", description = "数据字典分类与字典项维护")
@RestController
@RequestMapping("/api/system/dict")
@RequiredArgsConstructor
public class DictController {

    private final SysDictCategoryMapper dictCategoryMapper;
    private final SysDictItemMapper dictItemMapper;

    @Operation(summary = "查询所有字典分类")
    @SaCheckPermission("system:dict:view")
    @GetMapping("/categories")
    public Result<List<SysDictCategory>> listCategories() {
        return Result.ok(dictCategoryMapper.selectList(new LambdaQueryWrapper<SysDictCategory>()
                .eq(SysDictCategory::getIsDeleted, 0)));
    }

    @Operation(summary = "新增字典分类")
    @SaCheckPermission("system:dict:add")
    @AuditLog(module = "字典管理", operation = "INSERT", description = "创建新字典分类")
    @PostMapping("/category")
    public Result<Long> createCategory(@RequestBody SysDictCategory category) {
        category.setCreateTime(LocalDateTime.now());
        dictCategoryMapper.insert(category);
        return Result.ok(category.getId());
    }

    @Operation(summary = "新增字典项")
    @SaCheckPermission("system:dict:add")
    @AuditLog(module = "字典管理", operation = "INSERT", description = "创建字典明细项")
    @PostMapping("/item")
    public Result<Long> createItem(@RequestBody SysDictItem item) {
        item.setCreateTime(LocalDateTime.now());
        dictItemMapper.insert(item);
        return Result.ok(item.getId());
    }

    @Operation(summary = "根据分类编码获取字典明细列表")
    @GetMapping("/item/by-category/{code}")
    public Result<List<SysDictItem>> getItemsByCategoryCode(@PathVariable String code) {
        SysDictCategory category = dictCategoryMapper.selectOne(new LambdaQueryWrapper<SysDictCategory>()
                .eq(SysDictCategory::getCategoryCode, code)
                .eq(SysDictCategory::getIsDeleted, 0));
        if (category == null) {
            throw new BizException("字典分类不存在: " + code);
        }
        List<SysDictItem> items = dictItemMapper.selectList(new LambdaQueryWrapper<SysDictItem>()
                .eq(SysDictItem::getCategoryId, category.getId())
                .eq(SysDictItem::getIsDeleted, 0)
                .eq(SysDictItem::getStatus, 1)
                .orderByAsc(SysDictItem::getSortOrder));
        return Result.ok(items);
    }
}
