package com.crawlar;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.jsoup.nodes.Document;
import org.springframework.jdbc.core.JdbcTemplate;

import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.plugin.berkeley.BreadthCrawler;

import com.jdbc.JDBCHelper;

/**
 *
 * @author hwm
 */
public class NewsCrawler extends BreadthCrawler {

	
	private JdbcTemplate jdbcTemplate = null;
	
	private Connection conn = null;
	
    public NewsCrawler(String crawlPath, boolean autoParse) {
        super(crawlPath, autoParse);
        /*种子页面*/
        this.addSeed("http://news.hfut.edu.cn/list-1-1.html");

        /*正则规则设置*/
        /*爬取符合 http://news.hfut.edu.cn/show-xxxxxxhtml的URL*/
        this.addRegex("http://news.hfut.edu.cn/show-.*html");
        /*不要爬取 jpg|png|gif*/
        this.addRegex("-.*\\.(jpg|png|gif).*");
        /*不要爬取包含 # 的URL*/
        this.addRegex("-.*#.*");
    }

    @Override
    public void visit(Page page, CrawlDatums next) {
        String url = page.getUrl();
        /*判断是否为新闻页，通过正则可以轻松判断*/
        if (page.matchUrl("http://news.hfut.edu.cn/show-.*html")) {
//            Document doc = page.getDoc();

            String title = page.select("div[id=Article]>h2").first().text();
            String content = page.select("div#artibody", 0).text();
            
            String sql = "insert into tb_content(C_Title,C_Url,C_Content,C_AddTime) value(?,?,?,?)";
            if (jdbcTemplate != null) {
            	try {
            		conn = jdbcTemplate.getDataSource().getConnection();
            		PreparedStatement pstmt = conn.prepareStatement(sql);
					pstmt.setString(1, title);
					pstmt.setString(2, url);
					pstmt.setString(3, content);
					pstmt.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
					pstmt.executeUpdate();
					pstmt.close();
					conn.close();
            	} catch (SQLException e) {
					e.printStackTrace();
				}
    		}else{
    			jdbcTemplate = JDBCHelper.createMysqlTemplate("mysql1",
			            "jdbc:mysql://localhost:3306/testdb?useUnicode=true&characterEncoding=utf8",
			            "root", "root", 5, 30);
    		}
            
            /*如果你想添加新的爬取任务，可以向next中添加爬取任务，
               这就是上文中提到的手动解析*/
            /*WebCollector会自动去掉重复的任务(通过任务的key，默认是URL)，
              因此在编写爬虫时不需要考虑去重问题，加入重复的URL不会导致重复爬取*/
            /*如果autoParse是true(构造函数的第二个参数)，爬虫会自动抽取网页中符合正则规则的URL，
              作为后续任务，当然，爬虫会去掉重复的URL，不会爬取历史中爬取过的URL。
              autoParse为true即开启自动解析机制*/
            //next.add("http://xxxxxx.com");
        }
    }

    public static void main(String[] args) throws Exception {
        NewsCrawler crawler = new NewsCrawler("crawl", true);
        /*线程数*/
        crawler.setThreads(5);
        /*设置每次迭代中爬取数量的上限*/
        crawler.setTopN(50);
        crawler.start(4);
    }

}