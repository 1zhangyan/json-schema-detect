package org.zhangyan.dao;

import static org.zhangyan.constant.SchemaDetectConstant.ILLEGAL_ID;
import static org.zhangyan.constant.SchemaDetectConstant.monckStructNodeDataBase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.zhangyan.data.StructTreeNodeDO;

@Service
public class StructTreeNodeDaoImpl implements StructTreeNodeDao {

    @Override
    public Long upsert(StructTreeNodeDO structTreeNodeDO) {
        if (StringUtils.isEmpty(structTreeNodeDO.getPath())) {
            return ILLEGAL_ID;
        }
        Optional<StructTreeNodeDO> optional =  monckStructNodeDataBase.stream()
                .filter(it -> it.getPath().equals(structTreeNodeDO.getPath())).findFirst();
        if (optional.isPresent()) {
            StructTreeNodeDO nodeDO = optional.get();
            structTreeNodeDO.setId(nodeDO.getId());
            monckStructNodeDataBase.set(nodeDO.getId().intValue(), structTreeNodeDO);
            return nodeDO.getId();
        } else {
            return create(structTreeNodeDO);
        }
    }

    private Long create(StructTreeNodeDO structTreeNodeDO) {
        Long pos = Long.valueOf(monckStructNodeDataBase.size());
        structTreeNodeDO.setId(pos);
        monckStructNodeDataBase.add(structTreeNodeDO);
        return pos;
    }

    @Override
    public List<StructTreeNodeDO> getListByIds(List<Long> idList) {
        if (CollectionUtils.isEmpty(idList)) {
            return Collections.emptyList();
        }
        Collections.sort(idList);
        List<StructTreeNodeDO> nodeDOList = new ArrayList<>();
        for (Long aLong : idList) {
            if (aLong >= 0L && aLong < monckStructNodeDataBase.size()) {
                nodeDOList.add(monckStructNodeDataBase.get(aLong.intValue()));
            }
        }
        return nodeDOList;
    }

    @Override
    public StructTreeNodeDO getByPath(String path) {
        if (StringUtils.isEmpty(path)) {
            return null;
        }
        Optional<StructTreeNodeDO> optionalStructTreeNodeDO = monckStructNodeDataBase.stream()
                .filter(it -> path.equals(it.getPath()))
                .findFirst();
        return optionalStructTreeNodeDO.isPresent()?optionalStructTreeNodeDO.get():null;
    }

}
