package com.lims.common.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

/**
 * 业务编号生成器
 * 规则：{前缀}-{YYYYMMDD}-{4位自增序号}
 * 如：HT-20260921-0001, WT-20260921-0001, CY-20260921-0001, JC-20260921-0001, BG-20260921-0001
 * 底层基于 Redis INCR 实现分布式并发安全计数
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BusinessNumberGenerator {

    private final StringRedisTemplate stringRedisTemplate;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final String REDIS_BIZ_NO_PREFIX = "lims:biz:no:";

    @Getter
    @AllArgsConstructor
    public enum BusinessType {
        CONTRACT("HT", "合同编号"),
        ENTRUST("WT", "委托单号"),
        SAMPLING("CY", "采样任务号"),
        DETECTION("JC", "检测任务号"),
        REPORT("BG", "报告编号"),
        SETTLEMENT("JS", "结算单号"),
        SAMPLE("YP", "样品唯一码"),
        SUBCONTRACT("FB", "分包编号");

        private final String prefix;
        private final String description;
    }

    /**
     * 生成业务唯一流水编号
     *
     * @param businessType 业务类型
     * @return 格式化后的业务流水编号 (例如 HT-20260921-0001)
     */
    public String generate(BusinessType businessType) {
        String today = LocalDate.now().format(DATE_FORMATTER);
        String redisKey = REDIS_BIZ_NO_PREFIX + businessType.getPrefix() + ":" + today;

        Long sequence = stringRedisTemplate.opsForValue().increment(redisKey);
        if (sequence != null && sequence == 1L) {
            // 设置 48 小时过期时间，保障容灾与清理
            stringRedisTemplate.expire(redisKey, 48, TimeUnit.HOURS);
        }

        long seqValue = sequence != null ? sequence : 1L;
        return String.format("%s-%s-%04d", businessType.getPrefix(), today, seqValue);
    }
}
