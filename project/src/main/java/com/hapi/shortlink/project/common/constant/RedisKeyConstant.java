package com.hapi.shortlink.project.common.constant;

public class RedisKeyConstant {

    /**
     * 短链接跳转缓存键
     */
    public static final String SHORT_LINK_GOTO_CACHE_KEY = "short-link:goto:%s";

    /**
     * 短链接跳转缓存分布式锁
     */
    public static final String SHORT_LINK_GOTO_LOCK_KEY = "short-link:lock:goto:%s";

    /**
     * 短链接不存在（用于解决缓存穿透）
     */
    public static final String SHORT_LINK_GOTO_NULL_KEY = "short-link:goto:empty:%s";
}
