package org.zhangyan.dao;

import java.util.List;
import org.zhangyan.data.StructTreeNodeDO;

public interface StructTreeNodeDao {
    Long upsert(StructTreeNodeDO structTreeNodeDO);

    List<StructTreeNodeDO> getListByIds(List<Long> idList);

    StructTreeNodeDO getByPath(String path);

}
