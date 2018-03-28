package com.allen.filter;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.allen.constant.Constant;
import com.allen.model.Result;
import com.allen.util.JsonUtil;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

/**
 * 
* @ClassName: AccessFilter
* @Description: 自定义zuul网关过滤器
* @author chenliqiao
* @date 2018年3月26日 下午5:09:45
*
 */
public class AccessFilter extends ZuulFilter{
	
	private static Logger log = LoggerFactory.getLogger(AccessFilter.class);

	/**
	 * 过滤器的类型，它决定过滤器在请求的哪个生命周期中执行
	 */
	@Override
	public String filterType() {
		// TODO Auto-generated method stub
		//pre表示前置，即请求被路由之前
		return "pre";
	}
	
	/**
	 * 过滤器的执行顺序。当请求在一个阶段中存在多个过滤器时，需要根据该方法返回的值来依次执行。
	 */
	@Override
	public int filterOrder() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	/**
	 * 判断该过滤器是否需要被执行。这里我们直接返回了true，因此该过滤器对所有请求都会生效。实际运用中我们可以利用该函数来指定过滤器的有效范围。
	 */
	@Override
	public boolean shouldFilter() {
		// TODO Auto-generated method stub
		return true;
	}
	
	/**
	 * 过滤器具体逻辑
	 * 通过ctx.setSendZuulResponse(false)令zuul过滤该请求，不对其进行路由，
	 * 然后通过ctx.setResponseStatusCode(401)设置了其返回的错误码,
	 * 当然我们也可以进一步优化我们的返回，比如，通过ctx.setResponseBody(body)对返回body内容进行编辑等。
	 */
	@Override
	public Object run() {
		// TODO Auto-generated method stub
		RequestContext ctx=RequestContext.getCurrentContext();
		HttpServletRequest request=ctx.getRequest();
		
		String accessToken=request.getParameter(Constant.ACCESS_TOKEN);
		if(StringUtils.isEmpty(accessToken)){
			log.warn("access token is empty");
			ctx.setSendZuulResponse(false);
			Result<Object> result=new Result<>();
			result.setCode(401);
			result.setMessage("access token is empty");
			ctx.setResponseBody(JsonUtil.beanToJson(result));
			return null;
		}
		log.info("access token is ok~");
		return null;
	}

}
