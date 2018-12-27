package com.ferraborghini.jdbctest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.*;

public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);

    private Connection con = null;

    private CountDownLatch latch;

    private ExecutorService executorService = Executors.newFixedThreadPool(100);

    public Main() {
        init();
    }

    public void init() {
        String dbUrl1 = "jdbc:mysql://localhost:3306/test?useUnicode=true&characterEncoding=UTF-8&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
        //用户名
        String dbUserName = "root";
        //密码
        String dbPassword = "123456";
        //驱动名称
        String jdbcName = "com.mysql.jdbc.Driver";
        try {
            Class.forName(jdbcName);
            logger.info("加载驱动成功！");
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            logger.info("加载驱动失败！");
        }

        try {
            //获取数据库连接
            con = DriverManager.getConnection(dbUrl1, dbUserName, dbPassword);
        } catch (SQLException e) {
            e.printStackTrace();
            logger.info("获取数据库连接失败！");
        }
    }

    public void insertData() {
        logger.info("获取数据库连接成功！");
        while (true) {
            try {
                Statement statement = con.createStatement();
                statement.addBatch("insert into lock_test values(10, '" + Thread.currentThread().getName() + "')");
                statement.executeBatch();
                logger.info("insert success, start sleep 1s, thread name[{}]", Thread.currentThread().getName());
//                Thread.sleep(1000);
                statement.clearBatch();
                statement.addBatch("truncate  lock_test");
                logger.info("start delete, thread name[{}]", Thread.currentThread().getName());
                statement.executeBatch();
                logger.info("delete success, thread name[{}]", Thread.currentThread().getName());
                break;

            } catch (Exception e) {
//                try {
//                    Thread.sleep(2000);
//                } catch (InterruptedException e1) {
//                    e1.printStackTrace();
//                }
                logger.warn("insert data failed {}, thread name[{}]", e.getMessage(), Thread.currentThread().getName());
            }
        }
    }

    public void send() {

        Future<?> future = executorService.submit(
                () ->insertData()
        );

        try {
            future.get(10, TimeUnit.SECONDS);
            logger.info("result: {}", "success");
        } catch (Exception e) {
            future.cancel(true);
            logger.info("超时了!");
        }
    }

    public void get() {
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Main test = new Main();
        CountDownLatch latch = new CountDownLatch(10);
        for (int i = 0; i < 10; i++) {
            new Thread() {
                @Override
                public void run() {
                    logger.info("current thread name[{}] start", Thread.currentThread().getName());
                    test.send();
                    latch.countDown();
                    logger.info("current thread num[{}]", latch.getCount());
                }
            }.start();
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
