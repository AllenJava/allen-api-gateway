package com.allen.util;

import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Houjie
 *	Json工具类
 */
public final class JsonUtil {

	private JsonUtil() {}

	private static Logger log = LoggerFactory.getLogger(JsonUtil.class);
	
	private static final ObjectMapper MAPPER = new ObjectMapper();

	static {
		MAPPER.configure(Feature.ALLOW_UNQUOTED_FIELD_NAMES, false);
	}

	private static final JsonFactory JSONFACTORY = new JsonFactory();

	/**
	 * 转换Java Bean 为 json
	 */
	public static String beanToJson(Object o) {
		StringWriter sw = new StringWriter(300);
		JsonGenerator jsonGenerator = null;
		String result = "";
		try {
			jsonGenerator = JSONFACTORY.createGenerator(sw);
			MAPPER.writeValue(jsonGenerator, o);
			result = sw.toString();
		} catch (Exception e) {
			log.error("error", e);
		} finally {
			if (jsonGenerator != null) {
				try {
					jsonGenerator.close();
				} catch (Exception e) {
					log.error("error", e);
				}
			}
		}
		
		return result;
	}

	/**
	 * 转换Java Bean 为 json 附加了个时间格式参数
	 */
	public static String beanToJson(Object o, SimpleDateFormat dateFormat) {
		StringWriter sw = new StringWriter(300);
		JsonGenerator jsonGenerator = null;
		String result = "";
		try {
			MAPPER.setDateFormat(dateFormat);
			jsonGenerator = JSONFACTORY.createGenerator(sw);
			MAPPER.writeValue(jsonGenerator, o);
			result = sw.toString();
			MAPPER.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
		} catch (Exception e) {
			log.error("error", e);
		} finally {
			if (jsonGenerator != null) {
				try {
					jsonGenerator.close();
				} catch (Exception e) {
					log.error("error", e);
				}
			}
		}
		return result;
	}

	/**
	 * json 转 javabean
	 */
	public static <T> T jsonToBean(String json, Class<T> clazz) {
		try {
			return MAPPER.readValue(json, clazz);
		} catch (Exception e) {
			log.error("error", e);
			return null;
		}
	}

	/**
	 * json 转 javabean 附加了个时间格式参数
	 */
	public static <T> T jsonToBean(String json, Class<T> clazz,	DateFormat dateFormat) {
		try {
			MAPPER.setDateFormat(dateFormat);
			T s = MAPPER.readValue(json, clazz);
			MAPPER.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
			return s;
		} catch (Exception e) {
			log.error("error", e);
			return null;
		}
	}
	
	/**
	 * 
	* @Title: jsonToBean
	* @Description: json转换为javabean
	* @param @param src
	* @param @param clazz
	* @param @return
	* @return T
	* @throws
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> T jsonsToBean(String src,Class clazz){
		return (T) JSON.parseObject(src, clazz);
	}

	/**
	 * 转换Java Bean 为 HashMap
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> beanToMap(Object o) {
		try {
			return MAPPER.readValue(beanToJson(o), HashMap.class);
		} catch (Exception e) {
			log.error("error", e);
			return null;
		}
	}

	/**
	 * 转换Json String 为 HashMap
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> jsonToMap(String json, boolean collToString) {
		try {
			JSONObject jsonObject = JSONObject.parseObject(json);
			Map<String, Object> map = MAPPER.readValue(jsonObject.toJSONString(), HashMap.class);
			if (collToString) {
				for (Map.Entry<String, Object> entry : map.entrySet()) {
					if (entry.getValue() instanceof Collection || entry.getValue() instanceof Map) {
						entry.setValue(beanToJson(entry.getValue()));
					}
				}
			}
			return map;
		} catch (Exception e) {
			log.error("error", e);
			return null;
		}
	}
	
	/**
	 * 转换Json String 为 HashMap
	 */
	public static Map<String, String> jsonToSimpleMap(String json) {
		try {
			JSONObject jsonObject = JSONObject.parseObject(json);
			Set<String> keys = jsonObject.keySet();
			Map<String, String> map = new LinkedHashMap<>();
			for(String key : keys) {
				map.put(key, jsonObject.getString(key));
			}			
			return map;
		} catch (Exception e) {
			log.error("error", e);
			return null;
		}
	}

	/**
	 * List 转换成json
	 */
	public static String listToJson(List<?> list) {
		JsonGenerator jsonGenerator = null;
		StringWriter sw = new StringWriter();
		try {
			jsonGenerator = JSONFACTORY.createGenerator(sw);
			new ObjectMapper().writeValue(jsonGenerator, list);
			jsonGenerator.flush();
			return sw.toString();
		} catch (Exception e) {
			log.error("error", e);
			return null;
		} finally {
			if (jsonGenerator != null) {
				try {
					jsonGenerator.flush();
					jsonGenerator.close();
				} catch (Exception e) {
					log.error("error", e);
				}
			}
		}
	}
	
	/**
	 * 
	* @Title: jsonToList
	* @Description: json转List
	* @param @param json
	* @param @param clazz
	* @param @return
	* @param @throws Exception
	* @return List<T>
	* @throws
	 */
	public static <T> List<T> jsonToList(String json,Class<T> clazz) throws Exception{
		if(StringUtils.isEmpty(json))
			throw new Exception("json can not be null!");
		JSONArray jsonArray = JSONArray.parseArray(json);
		List<T> list=jsonArray.toJavaList(clazz);
		return list;
	}

}
