package com.hapi.shortlink.project.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ShortLinkCreateRespDTO {
    /**
     * 源地址
     */
    private String originalUrl;

    /**
     * 短链接地址
     */
    private String fullShortUrl;

    /**
     * 所属分组id
     */
    private String gid;
}
