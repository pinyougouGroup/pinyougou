package com.pinyougou.content.service.impl;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.content.service.ContentService;
import com.pinyougou.mapper.TbContentMapper;
import com.pinyougou.pojo.TbContent;
import com.pinyougou.pojo.TbContentExample;
import com.pinyougou.pojo.TbContentExample.Criteria;

import entity.PageResult;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class ContentServiceImpl implements ContentService {

	
	@Autowired
	private RedisTemplate  redisTemplate;
	
	
	@Autowired
	private TbContentMapper contentMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbContent> findAll() {
		return contentMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbContent> page=   (Page<TbContent>) contentMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbContent content) {
		contentMapper.insert(content);	
		redisTemplate.boundHashOps("content").delete(content.getCategoryId()); 		  //重新清理redis数据

		
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbContent content){
//		考虑：广告从轮播图类型改为了今日推荐类型的情况
		TbContent beforeUpdateContent = contentMapper.selectByPrimaryKey(content.getId());
		Long categoryId = beforeUpdateContent.getCategoryId();
		contentMapper.updateByPrimaryKey(content);
		redisTemplate.boundHashOps("content").delete(content.getCategoryId()); 		  //重新清理今日推荐类型数据
		if(content.getCategoryId()!=categoryId) {//如果两个类型不相等 
			redisTemplate.boundHashOps("content").delete(categoryId); 		  //重新清理轮播图类型数据
		}
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbContent findOne(Long id){
		return contentMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		
		
		for(Long id:ids){
			
			Long categoryId = contentMapper.selectByPrimaryKey(id).getCategoryId();
			redisTemplate.boundHashOps("content").delete(categoryId); 		  //重新清理redis数据
			contentMapper.deleteByPrimaryKey(id);
			
		}		
	}
	
	
		@Override
	public PageResult findPage(TbContent content, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbContentExample example=new TbContentExample();
		Criteria criteria = example.createCriteria();
		
		if(content!=null){			
						if(content.getTitle()!=null && content.getTitle().length()>0){
				criteria.andTitleLike("%"+content.getTitle()+"%");
			}
			if(content.getUrl()!=null && content.getUrl().length()>0){
				criteria.andUrlLike("%"+content.getUrl()+"%");
			}
			if(content.getPic()!=null && content.getPic().length()>0){
				criteria.andPicLike("%"+content.getPic()+"%");
			}
			if(content.getStatus()!=null && content.getStatus().length()>0){
				criteria.andStatusLike("%"+content.getStatus()+"%");
			}
	
		}
		
		Page<TbContent> page= (Page<TbContent>)contentMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

		
		
		@Override
		public List<TbContent> findByCategoryId(Long categoryId) {
//			select * from tb_content where category_id=? order by sort_order
			List<TbContent> list = (List<TbContent>) redisTemplate.boundHashOps("content").get(categoryId);
			
			if(list==null||list.size()==0) {
				System.out.println("数据从mysql中获取");
//				先从mysql数据库中获取
				TbContentExample example = new TbContentExample();
				example.createCriteria().andCategoryIdEqualTo(categoryId).andStatusEqualTo("1");
				example.setOrderByClause("sort_order"); // asc
				list = contentMapper.selectByExample(example );
				redisTemplate.boundHashOps("content").put(categoryId, list);
//				存到redis中
			}else {
				System.out.println("数据从REDIS中获取");
			}
			return list;
			
		}
	
}
