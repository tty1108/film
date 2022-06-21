package com.cskt.dao;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

/**
 * 连接数据库的工具类
 */
public class BaseDao {
    private static String driver;//驱动名
    private static String url;//连接字符串
    private static String user;//用户名
    private static String password;//密码

//    static{
//        init();//静态态块中调用静态方法，完成对静态属性的初始化
//    }

    /**
     * 获取连接对象
     * @return
     */
    public Connection getConnection(){
        Connection conn=null;
        try {
//            Class.forName(driver);
//            conn= DriverManager.getConnection(url,user,password);
            Context ctx=new InitialContext();
            DataSource ds=(DataSource)ctx.lookup("java:comp/env/jdbc/test");
            conn=ds.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conn;
    }

    /**
     *统一关闭数据库相关的资源
     * @param rs
     * @param st
     * @param conn
     */
    public void closeAll(ResultSet rs, Statement st, Connection conn){
        if(rs!=null){
            try {
                rs.close();//关闭结果集
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        if(st!=null){
            try {
                st.close();//关闭语句对象
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        if(conn!=null){
            try {
                conn.close();//关闭连接
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

    /**
     * 统一执行增删改
     * @param sql 增删改的语句
     * @param params 给?传参的值数组
     * @return 返回所影响的行数
     */
    public int executeUpdate(String sql,Object[] params){
         int n=0;
         Connection conn=null;
         PreparedStatement pstat=null;
        try {
            conn=this.getConnection();//获取连接
            pstat=conn.prepareStatement(sql);//创建预处理语句对象
            //给sql赋值
            if(params!=null) {
                for (int i = 0; i < params.length; i++) {
                    pstat.setObject(i + 1, params[i]);//从1开始，给?赋值
                }
            }
            n=pstat.executeUpdate();//执行增删改
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }finally {
            this.closeAll(null,pstat,conn);
        }
        return n;
    }

    public static void main(String[] args) {
        BaseDao baseDao=new BaseDao();
        Connection conn=baseDao.getConnection();
        if(conn!=null){
            System.out.println("获取连接成功");
        }
    }

    /**
     * 读取外部properties文件，初始化数据库连接属性
     */
//    public static void init(){
//        InputStream is=BaseDao.class.getClassLoader()
//                .getResourceAsStream("db.properties");
//        Properties prop=new Properties();//创建一个属性集合对象
//        try {
//            prop.load(is);
//            driver=prop.getProperty("driver");
//            url=prop.getProperty("url");
//            user=prop.getProperty("user");
//            password=prop.getProperty("password");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    /**
     * 查询操作
     *
     * @param sql
     *            sql语句
     * @param params
     *            参数数组
     * @return 查询结果集
     */
    protected ResultSet executeQuery(String sql, Object... params) {
        Connection conn = this.getConnection();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = conn.prepareStatement(sql);
            if(params!=null) {
                for (int i = 0; i < params.length; i++) {
                    pstmt.setObject(i + 1, params[i]);
                }
            }
            rs = pstmt.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rs;
    }


}
