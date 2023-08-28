package org.zhangyan.dao;

import static org.zhangyan.constant.SchemaDetectConstant.monckStructNodeDataBase;
import static org.zhangyan.constant.SchemaDetectConstant.monckStructTreeDataBase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

public class StructTreeNodeDao {


    public Long create(StructTreeNodeDO structTreeNodeDO) {
        Long pos = Long.valueOf(monckStructNodeDataBase.size());
        structTreeNodeDO.setId(pos);
        monckStructNodeDataBase.add(structTreeNodeDO);
        return pos;
    }

    public Long create(StructTreeDO structTreeDO) {
        Long pos = Long.valueOf(monckStructNodeDataBase.size());
        structTreeDO.setId(pos);
        monckStructTreeDataBase.add(structTreeDO);
        return pos;
    }

    public void update(StructTreeNodeDO structTreeNodeDO) {
        monckStructNodeDataBase.set(structTreeNodeDO.getId().intValue(), structTreeNodeDO);
    }

    public void update(StructTreeDO structTreeDO) {
        monckStructTreeDataBase.set(structTreeDO.getId().intValue(), structTreeDO);
    }

    public List<StructTreeNodeDO> getListByIds(List<Long> childrenList) {
        if (CollectionUtils.isEmpty(childrenList)) {
            return Collections.emptyList();
        }
        Collections.sort(childrenList);
        List<StructTreeNodeDO> nodeDOList = new ArrayList<>();
        for (Long aLong : childrenList) {
            if (aLong > 0 && aLong < nodeDOList.size()) {
                nodeDOList.add(monckStructNodeDataBase.get(aLong.intValue()));
            }
        }
        return nodeDOList;
    }

    public StructTreeDO getByPath(String path) {
        if (StringUtils.isEmpty(path)) {
            return null;
        }
        return monckStructTreeDataBase.stream()
                .filter(it -> it.getStructName().equals(path))
                .findFirst().get();
    }

}
