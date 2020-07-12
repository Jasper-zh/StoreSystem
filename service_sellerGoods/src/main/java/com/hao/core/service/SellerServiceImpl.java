package com.hao.core.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.hao.core.dao.seller.SellerDao;
import com.hao.core.pojo.entity.PageResult;
import com.hao.core.pojo.entity.Result;
import com.hao.core.pojo.seller.Seller;
import com.hao.core.pojo.seller.SellerQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.tags.ParamAware;

import java.util.Date;

@Service
@Transactional
public class SellerServiceImpl implements SellerService {
    @Autowired
    SellerDao sellerDao;
    @Override
    public void add(Seller seller) {
        seller.setCreateTime(new Date());
        seller.setStatus("0");
        sellerDao.insertSelective(seller);
    }

    @Override
    public PageResult getPage(Integer page, Integer pageSize, Seller seller) {
        PageHelper.startPage(page,pageSize);
        SellerQuery sellerQuery = new SellerQuery();
        sellerQuery.setOrderByClause("create_time desc");
        System.out.println("============================");
        System.out.println(seller.getNickName());
        if(seller!=null){
            SellerQuery.Criteria criteria = sellerQuery.createCriteria();
            if(seller.getStatus()!=null&&!"".equals(seller.getStatus())){
                criteria.andStatusEqualTo(seller.getStatus());
            }
            if(seller.getName()!=null&&!"".equals(seller.getName())){
                criteria.andNameLike("%"+seller.getName()+"%");
            }
            if(seller.getNickName()!=null&&!"".equals(seller.getNickName())){
                criteria.andNickNameLike("%"+seller.getNickName()+"%");
            }
        }
        Page<Seller> sellerPage = (Page<Seller>) sellerDao.selectByExample(sellerQuery);
        return new PageResult(sellerPage.getTotal(),sellerPage.getResult());

    }

    @Override
    public Result updateStatus(Seller seller) {
        try{
            sellerDao.updateByPrimaryKeySelective(seller);
            return new Result(true,"状态更新成功");
        }catch (Exception e){
            return new Result(false,"状态更新失败");
        }

    }

    @Override
    public Seller getSeller(String username) {
        return sellerDao.selectByPrimaryKey(username);
    }
}
