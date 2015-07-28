package com.xingmei.codeengine.code;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.xingmei.codeengine.util.ConstantUtil;
import com.xingmei.codeengine.util.ConstantUtil.TemplateConstant;
import com.xingmei.codeengine.util.db.Column;
import com.xingmei.codeengine.util.db.DBUtil;
import com.xingmei.codeengine.util.freemarker.FreeMarkerUtil;
import freemarker.template.Template;

/**
 *MySQL数据库代码生成工具
 *@author JiangZhiYong
 *@version 2015年6月19日12:11:52
 */
public class DBCodeTool {
	private static final Logger log=LoggerFactory.getLogger(DBCodeTool.class);
	private volatile static DBCodeTool dataBaseTool=null;
	
	
	private DBCodeTool() {
	}
	
	public static DBCodeTool getInstance(){
		if(dataBaseTool==null){
			synchronized (DBCodeTool.class) {
				dataBaseTool=new DBCodeTool();
			}
		}
		return dataBaseTool;
	}
	
	/**生成数据库表对应的hibernate 实体类 源代码*/
	public void generateHibernateBean() throws Exception{
		Connection dbConnection = DBUtil.getInstance().getDBConnection();
		if(dbConnection!=null){
			List<String> list = DBUtil.getTableName(dbConnection);
			if(list!=null){
				for(String str:list){
					List<Column> columnDefines = DBUtil.getColumnDefine(dbConnection, str);
					if(columnDefines!=null){
						String packageStr=System.getProperty("bean.path").replace("\\", ".");	//
						String codePath=System.getProperty("file.dir")+System.getProperty("bean.path")+"\\";	//
						String className=str.replaceFirst(str.substring(0, 1), str.substring(0, 1).toUpperCase()); 	//
						File file = new File(codePath); 
						File parent = file.getParentFile(); 
						if(parent!=null&&!parent.exists()){ 
							file.mkdirs(); 
						} 
						codePath=codePath+className+"Dto.java";
						
						
						
						FileOutputStream fos = new FileOutputStream(new File(codePath));
						Map<String,Object> data = new HashMap<String,Object>();
						data.put(ConstantUtil.PACKAGE, packageStr);
						data.put(ConstantUtil.CLASS_NAME, className);
						List<HashMap<String, String>> pros=new ArrayList<HashMap<String,String>>();
						for(Column column:columnDefines){
						    if(column.getName().equalsIgnoreCase("id")||column.getName().equalsIgnoreCase("ut")||column.getName().equalsIgnoreCase("ct")){
						        continue;
						    }
							HashMap<String, String> pro=new HashMap<String, String>();
							pro.put(ConstantUtil.PRO_TYPE, column.getType());
							pro.put(ConstantUtil.PRO_NAME, column.getName());
							pro.put(ConstantUtil.PRO_DESCRIPTION, column.getDescription());
							pros.add(pro);
						}
						
						data.put(ConstantUtil.PRO_LIST, pros);
						Template template = FreeMarkerUtil.getInstance().getTemplate(TemplateConstant.HibernateBean.getName());
						if(template!=null){
							template.process(data, new OutputStreamWriter(fos, "utf-8"));
							fos.flush();
							fos.close();
							log.info("table[{}] generate java code success",str);
						}else{
							log.error("table[{}] generate java code fail,because template[{}] not find",str,TemplateConstant.HibernateBean.getName());
						}
					}
				}
			}
			
		}else{
			log.error("db connection is null");
		}
	}
	
	/**数据类型转换*/
	public String convertType(String type){
	    if(type.equalsIgnoreCase("timestamp")){
	        return "java.util.Date";
	    }
	    return type;
	}
	
}
