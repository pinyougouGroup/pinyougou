package com.pinyougou.seckill.service.impl;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.pinyougou.seckill.service.SeckillService;

public class TimedTaskJob extends QuartzJobBean{

	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		//拿到spring容器
		ApplicationContext applicationContext = (ApplicationContext) context.getJobDetail().getJobDataMap().get("applicationContext");
		//拿到orderService实现类,调用clearOrder方法
		try {
			applicationContext.getBean(SeckillService.class).clearOrder();
			System.out.println("定时任务执行了");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
