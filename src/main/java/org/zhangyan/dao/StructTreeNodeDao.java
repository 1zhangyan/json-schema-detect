package org.zhangyan.dao;

import java.util.List;
import org.zhangyan.data.StructTreeNodeDO;

public interface StructTreeNodeDao {

    Long create(StructTreeNodeDO structTreeNodeDO);

    void update(StructTreeNodeDO structTreeNodeDO);

    List<StructTreeNodeDO> getListByIds(List<Long> childrenList);

    StructTreeNodeDO getByPath(String path);



}
