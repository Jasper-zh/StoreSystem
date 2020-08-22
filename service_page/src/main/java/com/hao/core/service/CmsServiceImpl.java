package com.hao.core.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.hao.core.dao.good.GoodsDao;
import com.hao.core.dao.good.GoodsDescDao;
import com.hao.core.dao.item.ItemCatDao;
import com.hao.core.dao.item.ItemDao;
import com.hao.core.pojo.good.Goods;
import com.hao.core.pojo.good.GoodsDesc;
import com.hao.core.pojo.item.Item;
import com.hao.core.pojo.item.ItemCat;
import com.hao.core.pojo.item.ItemQuery;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class CmsServiceImpl implements CmsService, ServletContextAware {
    @Autowired
    private GoodsDao goodsDao;
    @Autowired
    private GoodsDescDao descDao;
    @Autowired
    private ItemDao itemDao;
    @Autowired
    private ItemCatDao catDao;
    private ServletContext servletContext;
    @Autowired
    private FreeMarkerConfigurer freemarkerConfig;

    @Override
    public Map<String, Object> findGoodsData(Long goodsId) {
        Map<String, Object> resultMap = new HashMap<>();
        //1. 获取商品数据
        Goods goods = goodsDao.selectByPrimaryKey(goodsId);
        //2. 获取商品详情数据
        GoodsDesc goodsDesc = descDao.selectByPrimaryKey(goodsId);
        //3. 获取库存集合数据
        ItemQuery query = new ItemQuery();
        ItemQuery.Criteria criteria = query.createCriteria();
        criteria.andGoodsIdEqualTo(goodsId);
        List<Item> itemList = itemDao.selectByExample(query);

        //4. 获取商品对应的分类数据
        if (goods != null) {
            ItemCat itemCat1 = catDao.selectByPrimaryKey(goods.getCategory1Id());
            ItemCat itemCat2 = catDao.selectByPrimaryKey(goods.getCategory2Id());
            ItemCat itemCat3 = catDao.selectByPrimaryKey(goods.getCategory3Id());
            resultMap.put("itemCat1", itemCat1.getName());
            resultMap.put("itemCat2", itemCat2.getName());
            resultMap.put("itemCat3", itemCat3.getName());
        }
        //5. 将商品所有数据封装成Map返回
        resultMap.put("goods", goods);
        resultMap.put("goodsDesc", goodsDesc);
        resultMap.put("itemList", itemList);
        System.out.println("----------内容提供---详情-----:"+goodsDesc.getSpecificationItems());
        return resultMap;
    }

    @Override
    public void createStaticPage(Long goodsId) throws Exception {
        Map<String, Object> goodsData = this.findGoodsData(goodsId);
        //1. 获取模板的初始化对象
        Configuration configuration = freemarkerConfig.getConfiguration();
        //2. 获取模板对象
        Template template = configuration.getTemplate("item.ftl");
        //3. 创建输出流, 指定生成静态页面的位置和名称
        String path = goodsId + ".html";
        System.out.println("===path====" + path);
        String realPath = this.servletContext.getRealPath(path);
        Writer out = new OutputStreamWriter(new FileOutputStream(new File(realPath)), "utf-8");
        //4. 生成
        template.process(goodsData, out);
        //5.关闭流
        out.close();
    }

    /**
     * 当前是service项目, 没有配置springMvc所以没有初始化servletContext对象,
     * 项目配置了spring, spring中有servletContextAware接口, 这个接口中用servletContext对象
     *我们实现servletContextAware接口, 目的是使用里面的servletContext对象给
     * 当前类上的servletContext对象赋值
     */
    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }
}
