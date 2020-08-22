package com.hao.core.service;
import java.util.List;

public interface SolrManagerService {
    public void saveItemToSolr(List list);
    public void deleteItemByGoodsId(List goodsId);
}
