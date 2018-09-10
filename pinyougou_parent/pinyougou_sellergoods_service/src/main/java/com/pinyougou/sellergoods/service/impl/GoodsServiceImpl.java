package com.pinyougou.sellergoods.service.impl;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.mapper.TbGoodsDescMapper;
import com.pinyougou.mapper.TbGoodsMapper;
import com.pinyougou.mapper.TbItemCatMapper;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.mapper.TbSellerMapper;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbGoodsDesc;
import com.pinyougou.pojo.TbGoodsExample;
import com.pinyougou.pojo.TbGoodsExample.Criteria;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.sellergoods.service.GoodsService;

import entity.PageResult;
import groupEntity.Goods;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class GoodsServiceImpl implements GoodsService {

	@Autowired
	private TbGoodsMapper goodsMapper;
	
	@Autowired
	private TbGoodsDescMapper goodsDescMapper;
	
	@Autowired
	private TbItemCatMapper itemCatMapper;
	
	@Autowired
	private TbItemMapper itemMapper;
	
	@Autowired
	private TbBrandMapper brandMapper;
	
	@Autowired
	private TbSellerMapper sellerMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbGoods> findAll() {
		return goodsMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbGoods> page=   (Page<TbGoods>) goodsMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(Goods goods) {
		TbGoods tbGoods = goods.getTbGoods();
		tbGoods.setAuditStatus("0");  //未审核
		tbGoods.setIsMarketable("0"); //未上架
		tbGoods.setIsDelete("0");  //正常 如果 是1代表已删除
		goodsMapper.insert(tbGoods);  //返回主键		
		TbGoodsDesc tbGoodsDesc = goods.getTbGoodsDesc();
		tbGoodsDesc.setGoodsId(tbGoods.getId()); //表示他们的一对一的关系
		goodsDescMapper.insert(tbGoodsDesc);
		if(tbGoods.getIsEnableSpec().equals("1")) { //判断是否启用规格
			List<TbItem> itemList = goods.getItemList();
			for (TbItem tbItem : itemList) {
				
//				{"spec":{"网络":"移动3G","机身内存":"16G"},"price":0,"num":9999,"status":"1","isDefault":"0"}
//				goodsName="小米6X"
//				String title =   "小米6X 移动3G  16G";
				String title = tbGoods.getGoodsName();
				String spec = tbItem.getSpec(); //"{"网络":"移动3G","机身内存":"16G"}"
				Map<String ,String > specMap = JSON.parseObject(spec, Map.class) ;//JSON.parseObject是把字符串转成对象
				for(String key:specMap.keySet()) {
					title+=" "+specMap.get(key);
				}
				tbItem.setTitle(title);
//				  `spec` varchar(200) DEFAULT NULL,
//				`image` varchar(2000) DEFAULT NULL COMMENT '商品图片', 取spu的第一张图片
				tbItem = createTbitem(tbItem, tbGoods, tbGoodsDesc);

				itemMapper.insert(tbItem);
			}
			
		}else { //不启用规格
//			那么应该给TBItem表中插入一条数据
			TbItem tbItem = new TbItem();
			tbItem.setTitle(tbGoods.getGoodsName());
			tbItem = createTbitem(tbItem, tbGoods, tbGoodsDesc);
			itemMapper.insert(tbItem);
		}
		
//		
		
		
	}
	
	private TbItem createTbitem(TbItem tbItem,TbGoods tbGoods,TbGoodsDesc tbGoodsDesc ) {
		
		String itemImages = tbGoodsDesc.getItemImages();//[{color:"",url:''},{},{}]
		List<Map> itemImageMapList  = JSON.parseArray(itemImages, Map.class);
		if(itemImageMapList.size()>0) {
			tbItem.setImage(itemImageMapList.get(0).get("url")+"");
		}
//		`categoryId` bigint(10) NOT NULL COMMENT '所属类目，叶子类目' 取spu的第三级分类Id
		tbItem.setCategoryid(tbGoods.getCategory3Id());
//		  `create_time` datetime NOT NULL COMMENT '创建时间',
		tbItem.setCreateTime(new Date());
//		  `update_time` datetime NOT NULL COMMENT '更新时间',
		tbItem.setUpdateTime(new Date());
//		  `goods_id` bigint(20) DEFAULT NULL,   spuID
		tbItem.setGoodsId(tbGoods.getId());
//		  `seller_id` varchar(30) DEFAULT NULL, 
		tbItem.setSellerId(tbGoods.getSellerId());
//		`category` varchar(200) DEFAULT NULL,  取spu的第三级分类名称
		tbItem.setCategory(itemCatMapper.selectByPrimaryKey(tbGoods.getCategory3Id()).getName());
//		  `brand` varchar(100) DEFAULT NULL,   品牌名称
		tbItem.setBrand(brandMapper.selectByPrimaryKey(tbGoods.getBrandId()).getName());
//		  `seller` varchar(200) DEFAULT NULL,  商家名称
		tbItem.setSeller(sellerMapper.selectByPrimaryKey(tbGoods.getSellerId()).getName());
		return tbItem;
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbGoods goods){
		goodsMapper.updateByPrimaryKey(goods);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbGoods findOne(Long id){
		return goodsMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			goodsMapper.deleteByPrimaryKey(id);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbGoods goods, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbGoodsExample example=new TbGoodsExample();
		Criteria criteria = example.createCriteria();
		if(goods!=null){			
						if(goods.getSellerId()!=null && goods.getSellerId().length()>0){
				criteria.andSellerIdEqualTo(goods.getSellerId());
			}
			if(goods.getGoodsName()!=null && goods.getGoodsName().length()>0){
				criteria.andGoodsNameLike("%"+goods.getGoodsName()+"%");
			}
			if(goods.getAuditStatus()!=null && goods.getAuditStatus().length()>0){
				criteria.andAuditStatusLike("%"+goods.getAuditStatus()+"%");
			}
			if(goods.getIsMarketable()!=null && goods.getIsMarketable().length()>0){
				criteria.andIsMarketableLike("%"+goods.getIsMarketable()+"%");
			}
			if(goods.getCaption()!=null && goods.getCaption().length()>0){
				criteria.andCaptionLike("%"+goods.getCaption()+"%");
			}
			if(goods.getSmallPic()!=null && goods.getSmallPic().length()>0){
				criteria.andSmallPicLike("%"+goods.getSmallPic()+"%");
			}
			if(goods.getIsEnableSpec()!=null && goods.getIsEnableSpec().length()>0){
				criteria.andIsEnableSpecLike("%"+goods.getIsEnableSpec()+"%");
			}
			if(goods.getIsDelete()!=null && goods.getIsDelete().length()>0){
				criteria.andIsDeleteLike("%"+goods.getIsDelete()+"%");
			}
	
		}
		
		Page<TbGoods> page= (Page<TbGoods>)goodsMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

		@Override
		public void updateAuditStatus(String auditStatus, Long[] selectIds) {
			for (Long id : selectIds) {
//				update tb_goods set audit_status=? where id=?
				TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
				tbGoods.setAuditStatus(auditStatus);
				goodsMapper.updateByPrimaryKey(tbGoods);
			}
			
		}
		
		
		@Autowired
		private JmsTemplate jmsTemplate;
		
		@Autowired
		@Qualifier("solrItempageTopic")
		private Destination solrItempageTopic;
		@Autowired
		@Qualifier("solrItempageDeleteTopic")
		private Destination solrItempageDeleteTopic;
		
		@Override
		public void updateIsMarketAble(String marketable, Long[] selectIds) {
//			marketable：1 上架   marketable：2下架
//			调用同步solr索引库的方法 1s
//			调用同步静态页的方法           1s
			
			
			for (Long id : selectIds) {
//				update tb_goods set is_marketable=? where id=?
				TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
				tbGoods.setIsMarketable(marketable);
				goodsMapper.updateByPrimaryKey(tbGoods);
			}
			
			if(marketable.equals("1")) {
//				商品上架后把goodsId放入mq中 同步solr和静态页
				for (Long goodsId : selectIds) {
					jmsTemplate.send(solrItempageTopic, new MessageCreator() {
						@Override
						public Message createMessage(Session session) throws JMSException {
							TextMessage textMessage = session.createTextMessage(goodsId+"");
							return textMessage;
						}
					});
				}
			}
			if(marketable.equals("2")) {
//				商品上架后把goodsId放入mq中 同步solr和静态页
				for (Long goodsId : selectIds) {
					jmsTemplate.send(solrItempageDeleteTopic, new MessageCreator() {
						@Override
						public Message createMessage(Session session) throws JMSException {
							TextMessage textMessage = session.createTextMessage(goodsId+"");
							return textMessage;
						}
					});
				}
			}
			
		}
	
}
