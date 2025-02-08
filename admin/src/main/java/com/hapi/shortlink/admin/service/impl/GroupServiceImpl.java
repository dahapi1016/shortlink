package com.hapi.shortlink.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hapi.shortlink.admin.common.convention.exception.ServiceException;
import com.hapi.shortlink.admin.common.user.UserContext;
import com.hapi.shortlink.admin.common.utils.RandomStringGenerator;
import com.hapi.shortlink.admin.dao.entity.GroupDO;
import com.hapi.shortlink.admin.dao.mapper.GroupMapper;
import com.hapi.shortlink.admin.dto.req.CreateGroupReqDTO;
import com.hapi.shortlink.admin.dto.req.ShortLinkGroupSortReqDTO;
import com.hapi.shortlink.admin.dto.req.ShortLinkGroupUpdateReqDTO;
import com.hapi.shortlink.admin.dto.resp.ShortLinkGroupRespDTO;
import com.hapi.shortlink.admin.service.GroupService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class GroupServiceImpl extends ServiceImpl<GroupMapper, GroupDO> implements GroupService {
    @Override
    public void createGroup(CreateGroupReqDTO requestParam) {
        if(hasGroupName(requestParam.getGroupName())) {
            throw new ServiceException("分组名重复！");
        }
        String gid;
        do{
            gid = RandomStringGenerator.generateSixDigitRandomString();
        } while (hasGid(gid));
        GroupDO groupDO = GroupDO.builder()
                .gid(gid)
                .name(requestParam.getGroupName())
                .username(UserContext.getUsername())
                .sortOrder(0)
                .build();
        baseMapper.insert(groupDO);
    }

    @Override
    public List<ShortLinkGroupRespDTO> getGroupList() {
        LambdaQueryWrapper<GroupDO> wrapper = Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getUsername, UserContext.getUsername())
                .eq(GroupDO::getDelFlag, 0)
                .orderByDesc(GroupDO::getSortOrder);
        List<GroupDO> groupDOList = baseMapper.selectList(wrapper);
        return BeanUtil.copyToList(groupDOList, ShortLinkGroupRespDTO.class);
    }

    @Override
    public void updateGroup(ShortLinkGroupUpdateReqDTO shortLinkGroupUpdateReqDTO) {

        LambdaUpdateWrapper<GroupDO> wrapper = Wrappers.lambdaUpdate(GroupDO.class)
                .eq(GroupDO::getDelFlag, 0)
                .eq(GroupDO::getUsername, UserContext.getUsername())
                .eq(GroupDO::getGid, shortLinkGroupUpdateReqDTO.getGid());

        GroupDO groupDO = GroupDO.builder()
                .name(shortLinkGroupUpdateReqDTO.getName()).build();

        baseMapper.update(groupDO, wrapper);
    }

    @Override
    public void deleteGroup(String gid) {
        LambdaUpdateWrapper<GroupDO> wrapper = Wrappers.lambdaUpdate(GroupDO.class)
                .eq(GroupDO::getUsername, UserContext.getUsername())
                .eq(GroupDO::getGid, gid)
                .eq(GroupDO::getDelFlag, 0);

        GroupDO groupDO = new GroupDO();
        groupDO.setDelFlag(1);

        baseMapper.update(groupDO, wrapper);
    }

    @Override
    @Transactional
    public void sortGroup(List<ShortLinkGroupSortReqDTO> requestParam) {
        Map<String, Integer> updateMap = requestParam.stream()
                .collect(Collectors.toMap(ShortLinkGroupSortReqDTO::getGid, ShortLinkGroupSortReqDTO::getSortOrder));
        List<GroupDO> list = this.lambdaQuery()
                .eq(GroupDO::getDelFlag, 0)
                .eq(GroupDO::getUsername, UserContext.getUsername())
                .in(GroupDO::getGid,updateMap.keySet())
                .list();
        list.forEach(groupDO -> groupDO.setSortOrder(updateMap.getOrDefault(groupDO.getGid(), 0)));

        this.updateBatchById(list);
    }

    boolean hasGid(String gid) {
        LambdaQueryWrapper<GroupDO> wrapper = Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getGid, gid)
                .eq(GroupDO::getUsername, UserContext.getUsername());
        GroupDO groupDO = baseMapper.selectOne(wrapper);
        return groupDO != null;
    }

    boolean hasGroupName(String groupName) {
        LambdaQueryWrapper<GroupDO> wrapper = Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getUsername, UserContext.getUsername())
                .eq(GroupDO::getName, groupName)
                .eq(GroupDO::getDelFlag, 0);
        return baseMapper.selectOne(wrapper) != null;
    }
}
