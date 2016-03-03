package com.jdbc;

import org.springframework.jdbc.core.JdbcTemplate;

import cn.edu.hfut.dmic.webcollector.model.Page;

public class InitJdbcTemplate {
	
	private static JdbcTemplate jdbcTemplate = null;

	public static void startJdbcTemplate(String title,Page page){
		if (jdbcTemplate != null) {
		    int updates=jdbcTemplate.update("insert into tb_content"
		        +" (title,url,html) value(?,?,?)",
		            title, page.getUrl(), page.getHtml());
		    if(updates==1){
		        System.out.println("mysql插入成功");
		    }
		}else{
			try {
			    jdbcTemplate = JDBCHelper.createMysqlTemplate("mysql1",
			            "jdbc:mysql://localhost:3306/testdb?useUnicode=true&characterEncoding=utf8",
			            "root", "root", 5, 30);

			    /*创建数据表*/
			    jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS tb_content ("
			            + "id int(11) NOT NULL AUTO_INCREMENT,"
			            + "title varchar(50),url varchar(200),html longtext,"
			            + "PRIMARY KEY (id)"
			            + ") ENGINE=MyISAM DEFAULT CHARSET=utf8;");
			    System.out.println("成功创建数据表 tb_content");
			} catch (Exception ex) {
			    jdbcTemplate = null;
			    System.out.println("mysql未开启或JDBCHelper.createMysqlTemplate中参数配置不正确!");
			}
		}
	}
	
	public static void main(String[] args) {
		InitJdbcTemplate.startJdbcTemplate("", null);
	}

}
