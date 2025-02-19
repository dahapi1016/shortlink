package com.hapi.shortlink.admin.remote.dto.req;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hapi.shortlink.admin.dao.entity.ShortLinkDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 短链接分页请求参数
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ShortLinkPageReqDTO extends Page<ShortLinkDO> {

    /**
     * 分组标识
     */
    private String gid;
}
