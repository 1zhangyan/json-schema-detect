package org.zhangyan.dao;

import org.zhangyan.data.StructTreeDO;

public interface StructTreeDao {

    public Long create(StructTreeDO structTreeDO);

    public void update(StructTreeDO structTreeDO);

    public StructTreeDO getByPath(String path);
}
