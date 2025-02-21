package com.hapi.shortlink.project.util;

import com.hapi.shortlink.project.dao.entity.ShortLinkDO;

import java.util.Date;

import static com.hapi.shortlink.project.common.constant.ShortLInkConstant.DEFAULT_CACHE_EXPIRE_TIME;

public class ShortLinkUtil {

    /**
     * 返回该链接应在缓存中的TTL
     * @param shortLinkDO 短链接数据实体
     * @return TTL（单位为MS）
     */
    public static Long getExpireTime(ShortLinkDO shortLinkDO) {
        if(shortLinkDO.getHasExpiration() == 0) {
            return DEFAULT_CACHE_EXPIRE_TIME;
        }
        long timeLeft = shortLinkDO.getExpirationDate().getTime() - new Date().getTime();
        return timeLeft < 0 ? -1L : timeLeft;
    }
}
