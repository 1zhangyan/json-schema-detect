package org.zhangyan.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.util.CollectionUtils;

public class StructTreeNodeDao {

    private static List<StructTreeNodeDO> monckStructNodeDataBase = new ArrayList<>();
    private static List<StructTreeDO> monckStructTreeDataBase = new ArrayList();

    public int create(StructTreeNodeDO structTreeNodeDO) {
        monckStructNodeDataBase.add(structTreeNodeDO);
        return monckStructNodeDataBase.size()-1;
    }

    public int create(StructTreeDO structTreeDO) {
        monckStructTreeDataBase.add(structTreeDO);
        return monckStructNodeDataBase.size()-1;
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

}
