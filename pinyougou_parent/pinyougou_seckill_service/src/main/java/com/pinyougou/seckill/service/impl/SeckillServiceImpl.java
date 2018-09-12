package com.pinyougou.seckill.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.wxpay.sdk.WXPayUtil;
import com.pinyougou.mapper.TbSeckillGoodsMapper;
import com.pinyougou.mapper.TbSeckillOrderMapper;
import com.pinyougou.pojo.TbPayLog;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.pojo.TbSeckillOrder;
import com.pinyougou.pojo.TbSeckillOrderExample;
import com.pinyougou.seckill.service.SeckillService;
import com.pinyougou.seckill.thread.CreateOrder;

import entity.UserIdAndSeckillGoodsId;
import util.HttpClient;
import util.IdWorker;

@Service
public class SeckillServiceImpl implements SeckillService{
	
	@Value("${appid}")
	private String appid;
	
	@Value("${partner}")
	private String partner;//商户号
	
	@Value("${partnerkey}")
	private String partnerkey;
	
	@Value("${notifyurl}")
	private String notifyurl;
	
	@Autowired
	private RedisTemplate  redisTemplate;
	@Autowired
	private IdWorker  idWorker;
	@Autowired
	private TbSeckillOrderMapper  seckillOrderMapper;
	@Autowired
	private TbSeckillGoodsMapper  seckillGoodsMapper;
	@Autowired
	private ThreadPoolTaskExecutor executor;
	@Autowired
	private CreateOrder createOrder;
	@Override
	public List<TbSeckillGoods> findAllFromRedis() {
		return redisTemplate.boundHashOps("seckill_goods").values();
	}

	@Override
	public TbSeckillGoods findFromRedis(Long id) {
		return  (TbSeckillGoods) redisTemplate.boundHashOps("seckill_goods").get(id);
	}

	@Override
	public void saveSeckillOrder(Long id, String userId) {
//		 `id` bigint(20) NOT NULL COMMENT '主键',
//		 `money` decimal(10,2) DEFAULT NULL COMMENT '支付金额',
//		 `user_id` varchar(50) DEFAULT NULL COMMENT '用户',
//		 `seller_id` varchar(50) DEFAULT NULL COMMENT '商家',
//		 `create_time` datetime DEFAULT NULL COMMENT '创建时间', 
//		 `status` varchar(1) DEFAULT NULL COMMENT '状态',
		
		 Object object = redisTemplate.boundHashOps("seckill_order").get(userId);//待支付的秒杀的订单
		 if(object!=null) {// 意味着此人有未付款的秒杀订单，不让重复购买
			 throw new RuntimeException("您有未支付的订单");
		 }
		
		Object rightPop = redisTemplate.boundListOps("seckill_goods_num"+id).rightPop(); // rightPop从右侧出栈
		if(rightPop==null) { //代表商品id已全部出栈
			throw new RuntimeException("已售罄");
		}
		
		redisTemplate.boundListOps("seckill_order_num").leftPush(new UserIdAndSeckillGoodsId(userId, id));
		
		
		executor.execute(createOrder);
		
		
//		 if(seckillGoods==null) {
//			throw new RuntimeException("已售罄");
//		 }
		

		 
	}

	@Override
	public void clearOrder() throws Exception {
		
		TbSeckillOrderExample example =new TbSeckillOrderExample();
		example.createCriteria().andStatusEqualTo("0");
		List<TbSeckillOrder> seckillOrder = seckillOrderMapper.selectByExample(example );
	
		for (TbSeckillOrder tbSeckillOrder : seckillOrder) {
			Date createTime = tbSeckillOrder.getCreateTime();
			long createTimeSeconds = createTime.getTime();//创建订单时间的毫秒数
			//当前时间的毫秒数
			long nowTimeSeconds = new Date().getTime();
			//tbSeckillGoods.get
			if((nowTimeSeconds-createTimeSeconds)>=1*60*1000) {
				//调用微信取消订单接口
				HttpClient httpClient=new HttpClient("https://api.mch.weixin.qq.com/pay/closeorder");
				Map<String, String> map=new HashMap<String, String>();
//				公众账号ID	appid	是	String(32)	wx8888888888888888	微信分配的公众账号ID（企业号corpid即为此appId）
				map.put("appid", appid);
//				商户号	mch_id	是	String(32)	1900000109	微信支付分配的商户号
				map.put("mch_id", partner);
				//商户订单号	out_trade_no	是	String(32)	1217752501201407033233368018	商户系统内部订单号，要求32个字符内，只能是数字、大小写字母_-|*@ ，且在同一个商户号下唯一。
				//获取支付日志表 pagLog
				TbPayLog payLog = (TbPayLog) redisTemplate.boundHashOps("payLog").get(tbSeckillOrder.getUserId());
				if(payLog!=null) {
					map.put("out_trade_no", payLog.getOutTradeNo());
				}
				//随机字符串	nonce_str	是	String(32)	5K8264ILTKCH16CQ2502SI8ZNMTM67VS	随机字符串，不长于32位。推荐随机数生成算法
				String nonceStr = WXPayUtil.generateNonceStr();
				map.put("nonce_str", nonceStr);
				//签名	sign	是	String(32)	C380BEC2BFD727A4B6845133519F3AD6	签名，详见签名生成算法
				String generateSignedXml = WXPayUtil.generateSignedXml(map, partnerkey);
				//调用远程服务
				httpClient.setXmlParam(generateSignedXml);
				httpClient.post();
				//取消订单.移除redis里的数据
				redisTemplate.boundHashOps("seckill_order").delete(tbSeckillOrder.getUserId());
				//修改订单,更新数据库
				//tbSeckillOrder.setCreateTime(new Date());
				tbSeckillOrder.setStatus("2");//0表示未支付,1表示已支付,2表示订单已取消
				seckillOrderMapper.updateByPrimaryKey(tbSeckillOrder);
				System.out.println("任务执行了");
			}
		}
		
	}
	
}
