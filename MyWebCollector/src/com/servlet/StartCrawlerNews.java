package com.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.crawlar.NewsCrawler;

public class StartCrawlerNews extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public StartCrawlerNews() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/html;charset=UTF-8");
		PrintWriter out = response.getWriter();
		NewsCrawler nc = new NewsCrawler("nc",true);
		nc.setThreads(5);
		nc.setTopN(50);
		try {
			nc.start(5);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			out.print("捉取完毕！");
		}
		
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.doGet(request, response);
	}

}
