package com.hapi.shortlink.admin.dto.req;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 创建分组请求DTO
 */
@Data
@AllArgsConstructor
public class CreateGroupReqDTO {

    String groupName;
}
