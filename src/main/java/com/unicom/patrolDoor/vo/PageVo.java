package com.unicom.patrolDoor.vo;

/**
 * @Author wrk
 * @Date 2021/3/24 16:06
 */
public class PageVo {
    private Integer pageNo;//当前第几页

    private Integer pageNum;//每页显示多少条

    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }
}
