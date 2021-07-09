package com.kfgs.aotc.common.pojo;

import com.kfgs.aotc.util.CopyUtil;
import lombok.Data;
import org.hibernate.query.internal.NativeQueryImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.repository.support.PageableExecutionUtils;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.LinkedList;
import java.util.List;

/**
 * 分页对象（参考JqGrid插件）
 */
@Data
public class PageInfo<M> {
    private int page;//当前页码
    private int pageSize;//页面大小
    private String sidx;//排序字段
    private String sord;//排序方式

    private List<M> rows;//分页结果
    private int records;//总记录数
    private int total;//总页数

    /**
     * 获取统一分页结果
     */
    public static <M> PageInfo<M> of(Page page, Class<M> entityModelClass) {
        int records = (int) page.getTotalElements();
        int pageSize = page.getSize();
        int total = records % pageSize == 0 ? records / pageSize : records / pageSize + 1;

        PageInfo<M> pageInfo = new PageInfo<>();
        pageInfo.setPage(page.getNumber() + 1);//页码
        pageInfo.setPageSize(pageSize);//页面大小
        pageInfo.setRows(CopyUtil.copyList(page.getContent(), entityModelClass));//分页结果
        pageInfo.setRecords(records);//总记录数
        pageInfo.setTotal(total);//总页数
        return pageInfo;
    }

    /**
     *
     * @param list 分页数据
     * @param currentPage 当前页码
     * @param pagesize 行数
     * @param records 总记录数
     * @param total 总页数
     * @return
     */
    public static PageInfo list2Page(List list,int currentPage,int pagesize,int records,int total){
        PageInfo<List> pageInfo = new PageInfo<>();
        pageInfo.setPage(currentPage);
        pageInfo.setPageSize(pagesize);
        pageInfo.setRows(list); //分页结果
        pageInfo.setRecords(records);
        pageInfo.setTotal(total);
        return pageInfo;
    }

    public static PageInfo  ofMap(Page page, List list) {
        int records = (int) page.getTotalElements();
        int pageSize = page.getSize();
        int total = records % pageSize == 0 ? records / pageSize : records / pageSize + 1;

        PageInfo<List> pageInfo = new PageInfo<>();
        pageInfo.setPage(page.getNumber() + 1);//页码
        pageInfo.setPageSize(pageSize);//页面大小
        // 将linkedHashMap转为List
        //List list = JSON.parseObject(JSON.toJSONString(linkedHashMap), new TypeReference<List>() {});
        pageInfo.setRows(list);//分页结果
        pageInfo.setRecords(records);//总记录数
        pageInfo.setTotal(total);//总页数
        return pageInfo;
    }

    public static PageInfo createPageInfo(int records,int pageSize,int total,List list){

        PageInfo<List> pageInfo = new PageInfo<>();
        pageInfo.setPageSize(pageSize);
        pageInfo.setRecords(records);
        pageInfo.setTotal(total);
        pageInfo.setRows(list);
        return pageInfo;
    }

    /**
     * 获取JPA的分页对象
     */
    public static Page getJPAPage(Query query, PageRequest pageRequest, EntityManager em) {
        query.setFirstResult((int) pageRequest.getOffset());
        query.setMaxResults(pageRequest.getPageSize());

        //获取分页结果
        return PageableExecutionUtils.getPage(query.getResultList(), pageRequest, () -> {
            //设置countQuerySQL语句
            Query countQuery = em.createNativeQuery("select count(*) from ( " + ((NativeQueryImpl) query).getQueryString() + " ) count_table");
            //设置countQuerySQL参数
            query.getParameters().forEach(parameter -> countQuery.setParameter(parameter.getName(), query.getParameterValue(parameter.getName())));
            //返回一个总数
            return Long.valueOf(countQuery.getResultList().get(0).toString());
        });
    }
}
