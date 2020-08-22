package com.hao.core.service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.hao.core.dao.good.BrandDao;
import com.hao.core.dao.good.GoodsDao;
import com.hao.core.dao.good.GoodsDescDao;
import com.hao.core.dao.item.ItemCatDao;
import com.hao.core.dao.item.ItemDao;
import com.hao.core.dao.seller.SellerDao;
import com.hao.core.pojo.entity.GoodsEntity;
import com.hao.core.pojo.entity.PageResult;
import com.hao.core.pojo.good.Brand;
import com.hao.core.pojo.good.Goods;
import com.hao.core.pojo.good.GoodsDesc;
import com.hao.core.pojo.good.GoodsQuery;
import com.hao.core.pojo.item.Item;
import com.hao.core.pojo.item.ItemCat;
import com.hao.core.pojo.item.ItemQuery;
import com.hao.core.pojo.seller.Seller;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.transaction.annotation.Transactional;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.math.BigDecimal;
import java.util.*;

@Service
@Transactional
public class GoodsServiceImpl implements GoodsService {
    @Autowired
    private GoodsDescDao descDao;
    @Autowired
    private ItemDao itemDao;
    @Autowired
    private ItemCatDao catDao;
    @Autowired
    private BrandDao brandDao;
    @Autowired
    private SellerDao sellerDao;
    @Autowired
    private GoodsDao goodsDao;

    @Autowired
    private JmsTemplate jmsTemplate;
    //为商品上架使用
    @Autowired
    private ActiveMQTopic topicPageAndSolrDestination;
    //为商品下架使用
    @Autowired
    private ActiveMQQueue queueSolrDeleteDestination;



    public void add(GoodsEntity goodsEntity) {
        //1. 保存商品对象
        //刚添加的商品状态默认为0 未审核
        goodsEntity.getGoods().setAuditStatus("0");
        //添加商品
        goodsDao.insertSelective(goodsEntity.getGoods());
        //2. 保存商品详情对象
        //商品主键作为商品详情的主键
        goodsEntity.getGoodsDesc().setGoodsId(goodsEntity.getGoods().getId());
        //添加商品详情
        descDao.insertSelective(goodsEntity.getGoodsDesc());
        //3.添加库存,商品规格
        insertItem(goodsEntity);
    }
    public void insertItem(GoodsEntity goodsEntity) {
            //有勾选规格复选框
            if (goodsEntity.getItemList() != null) {
                BigDecimal price = goodsEntity.getGoods().getPrice();
                System.out.println("价格："+price);
                for (Item item : goodsEntity.getItemList()) {
                    //库存标题, 由商品名 + 规格组成具体的库存标题, 供消费者搜索使用, 可以搜索的更精确
                    String title = goodsEntity.getGoods().getGoodsName();

                    //从库存对象中获取前端传入的json格式规格字符串, 例如: {"机身内存":"16G","网络":"联通3G"}
                    String specJsonStr = item.getSpec();
                    //将json格式字符串转换成对象
                    Map speMap = JSON.parseObject(specJsonStr, Map.class);
                    //获取map中的value集合
                    Collection<String> values = speMap.values();
                    for (String value : values) {
                        title += " " + value;
                    }
                    //设置标题
                    item.setTitle(title);
                    //设置价格
                    item.setPrice(price);
                    //设置库存对象的属性值
                    setItemValue(goodsEntity, item);
                    itemDao.insertSelective(item);
                }

        }
    }
    private Item setItemValue(GoodsEntity goodsEntity, Item item) {
        //商品id
        item.setGoodsId(goodsEntity.getGoods().getId());
        //创建时间
        item.setCreateTime(new Date());
        //更新时间
        item.setUpdateTime(new Date());
        //库存状态, 默认为0, 未审核
        item.setStatus("0");
        //分类id, 库存使用商品的第三级分类最为库存分类
        item.setCategoryid(goodsEntity.getGoods().getCategory3Id());
        //分类名称
        ItemCat itemCat = catDao.selectByPrimaryKey(goodsEntity.getGoods().getCategory3Id());
        item.setCategory(itemCat.getName());
        //品牌名称
        Brand brand = brandDao.selectByPrimaryKey(goodsEntity.getGoods().getBrandId());
        item.setBrand(brand.getName());
        //卖家名称
        Seller seller = sellerDao.selectByPrimaryKey(goodsEntity.getGoods().getSellerId());
        item.setSeller(seller.getName());
        //示例图片
        String itemImages = goodsEntity.getGoodsDesc().getItemImages();
        List<Map> maps = JSON.parseArray(itemImages, Map.class);
        if (maps != null && maps.size() > 0) {
            String url = String.valueOf(maps.get(0).get("url"));
            item.setImage(url);
        }
        return item;
    }
    @Override
    public PageResult findPage(Goods goods, Integer page, Integer rows) {
        PageHelper.startPage(page, rows);
        GoodsQuery query = new GoodsQuery();
        GoodsQuery.Criteria criteria = query.createCriteria();
        if (goods != null) {
            if (goods.getGoodsName() != null && !"".equals(goods.getGoodsName())) {
                criteria.andGoodsNameLike("%"+goods.getGoodsName()+"%");
            }
            if (goods.getAuditStatus() != null && !"".equals(goods.getAuditStatus())) {
                criteria.andAuditStatusEqualTo(goods.getAuditStatus());
            }
            if (goods.getSellerId() != null && !"".equals(goods.getSellerId())
                    && !"admin".equals(goods.getSellerId()) && !"wc".equals(goods.getSellerId())) {
                criteria.andSellerIdEqualTo(goods.getSellerId());
            }
        }
        criteria.andIsDeleteNotEqualTo("1");
        GoodsQuery.Criteria criteria2 = query.createCriteria();
        criteria2.andIsDeleteIsNull();
        query.or(criteria2);
        Page<Goods> goodsList = (Page<Goods>)goodsDao.selectByExample(query);
        return new PageResult(goodsList.getTotal(), goodsList.getResult());
    }

    @Override
    public GoodsEntity findOne(Long id) {
        //1. 根据商品id查询商品对象
        Goods goods = goodsDao.selectByPrimaryKey(id);
        //2. 根据商品id查询商品详情对象
        GoodsDesc goodsDesc = descDao.selectByPrimaryKey(id);

        //3. 根据商品id查询库存集合对象
        ItemQuery query = new ItemQuery();
        ItemQuery.Criteria criteria = query.createCriteria();
        criteria.andGoodsIdEqualTo(id);
        List<Item> items = itemDao.selectByExample(query);

        //4. 将以上查询到的对象封装到GoodsEntity中返回
        GoodsEntity goodsEntity = new GoodsEntity();
        goodsEntity.setGoods(goods);
        goodsEntity.setGoodsDesc(goodsDesc);
        goodsEntity.setItemList(items);
        return goodsEntity;
    }
    @Override
    public void update(GoodsEntity goodsEntity) {
        //1. 修改商品对象
        goodsDao.updateByPrimaryKeySelective(goodsEntity.getGoods());
        //2. 修改商品详情对象
        descDao.updateByPrimaryKeySelective(goodsEntity.getGoodsDesc());
        //3. 根据商品id删除对应的库存集合数据
        ItemQuery query = new ItemQuery();
        ItemQuery.Criteria criteria = query.createCriteria();
        criteria.andGoodsIdEqualTo(goodsEntity.getGoods().getId());
        itemDao.deleteByExample(query);
        //4. 添加库存集合数据
        insertItem(goodsEntity);
    }
    public void delete(Long[] ids) {
        if (ids != null) {
            for (final Long id : ids) {
                // 1. 到数据库中对商品进行逻辑删除
                Goods goods = new Goods();
                goods.setId(id);
                goods.setIsDelete("1");
                goodsDao.updateByPrimaryKeySelective(goods);
                //2 将商品id作为消息发送给消息服务器
                jmsTemplate.send(queueSolrDeleteDestination, new MessageCreator() {
                    @Override
                    public Message createMessage(Session session) throws JMSException {
                        TextMessage textMessage = session.createTextMessage(String.valueOf(id));
                        return textMessage;
                    }
                });

            }
        }
    }
    @Override
    public void updateStatus(Long[] ids, String status) {
        if (ids != null) {
            for (final Long id : ids) {
                //1. 根据商品id修改商品对象状态码
                Goods goods  = new Goods();
                goods.setId(id);
                goods.setAuditStatus(status);
                goodsDao.updateByPrimaryKeySelective(goods);
                //2. 根据商品id修改库存集合对象状态码
                Item item = new Item();
                item.setStatus(status);
                ItemQuery query = new ItemQuery();
                ItemQuery.Criteria criteria = query.createCriteria();
                criteria.andGoodsIdEqualTo(id);
                itemDao.updateByExampleSelective(item, query);
                /**
                 * 将商品id作为消息发送给消息服务器
                 */
                if ("2".equals(status)) {
                    jmsTemplate.send(topicPageAndSolrDestination, new MessageCreator() {
                        @Override
                        public Message createMessage(Session session) throws JMSException {
                            TextMessage textMessage = session.createTextMessage(String.valueOf(id));
                            return textMessage;
                        }
                    });
                }
            }
        }
    }
    /**
     * 根据商品id和状态查询库存
     * @param ids 商品id集合
     * @param status  商品状态
     * @return
     */
    public List<Item> findItemByGoodsIdAndStatus(Long[]ids,String status){
        ItemQuery query = new ItemQuery();
        ItemQuery.Criteria criteria = query.createCriteria();
        criteria.andStatusEqualTo(status);
        criteria.andGoodsIdIn(Arrays.asList(ids));
        List<Item> items = itemDao.selectByExample(query);
        return items;
    }

}

