package com.hapi.shortlink.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hapi.shortlink.admin.dao.entity.GroupDO;
import com.hapi.shortlink.admin.dto.req.CreateGroupReqDTO;
import com.hapi.shortlink.admin.dto.req.ShortLinkGroupSortReqDTO;
import com.hapi.shortlink.admin.dto.req.ShortLinkGroupUpdateReqDTO;
import com.hapi.shortlink.admin.dto.resp.ShortLinkGroupRespDTO;

import java.util.List;

public interface GroupService extends IService<GroupDO> {
    /**
     * 创建新分组
     * @param requestParam 分组名称
     */
    void createGroup(CreateGroupReqDTO requestParam);

    /**
     * 获取当前用户的分组列表（有序）
     * @return 分组列表
     */
    List<ShortLinkGroupRespDTO> getGroupList();

    /**
     * 更新分组信息
     */
    void updateGroup(ShortLinkGroupUpdateReqDTO shortLinkGroupUpdateReqDTO);

    /**
     * 删除分组
     * @param gid 分组标识
     */
    void deleteGroup(String gid);

    /**
     * 根据前端数据更新分组顺序
     * @param requestParam 请求参数
     */
    void sortGroup(List<ShortLinkGroupSortReqDTO> requestParam);
}
