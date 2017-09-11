package com.example.a25737;

import java.util.List;

/**
 * Author:  王强
 * Version:  1.0
 * Date:    2017/8/28
 * Modify:
 * Description: //TODO
 * Copyright notice:
 */

public class MyBean {


    /**
     * retcode : 200
     * data : [{"id":10000,"title":"新闻","type":1,"des":"这是一条有内涵的新闻1111"},{"id":10002,"title":"专题","type":10,"des":"这是一条有内涵的新闻222222"},{"id":10003,"title":"组图2","type":2,"des":"这是一条有内涵的新闻333333"},{"id":10006,"title":"组图4","type":2,"des":"这是一条有内涵的新闻333333"},{"id":10008,"title":"组图5","type":2,"des":"这是一条有内涵的新闻333333"},{"id":10003,"title":"组图6","type":2,"des":"这是一条有内涵的新闻ddddd33"},{"id":10003,"title":"组图7","type":2,"des":"这是一条有内涵的新闻3ssss33333"},{"id":10003,"title":"组图8","type":2,"des":"这是一条有内涵的新闻33dddd33333"},{"id":10004,"title":"互动","type":3,"des":"这是一条有内涵的新闻444444"}]
     * header : http://10.0.0.2:8080/aa.jpg
     */

    private int retcode;
    private String header;
    private List<DataBean> data;

    public int getRetcode() {
        return retcode;
    }

    public void setRetcode(int retcode) {
        this.retcode = retcode;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * id : 10000
         * title : 新闻
         * type : 1
         * des : 这是一条有内涵的新闻1111
         */

        private int id;
        private String title;
        private int type;
        private String des;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getDes() {
            return des;
        }

        public void setDes(String des) {
            this.des = des;
        }
    }
}
