function tcbd(type) {
    if(vaildateForm()){
        let infoForm = $("#infoForm").serializeObject();
        loadingSpinner = layer.msg('正在加载...', {icon: 16, shade: 0.3, time:0});
        $.post(ctx + "/tcbd/init/"+type,infoForm,function (response) {
            if(response.flag){
                tableThead = [];
                tableData = [];
                // 获取表头：
                let entityObj = response.data.rows[0];
                let h = [];
                // 拼装表头
                $.each(entityObj,function (index,obj) {
                    h.push({field: index, title: index});
                })
                tableThead.push(h);
                // 拼装 数据
                $.each(response.data.rows,function (index,object) {
                    tableData.push(object)
                })
                // 表格重载
                table.reload('demoTable',{

                    cols: tableThead ,
                    data: tableData,
                    limit: response.data.rows.length
                })
            }else{
                layer.msg('数据加载失败，请稍候重试', {icon: 5})
            }
            layer.close(loadingSpinner);
        })
    }else{
        layer.msg("请正确选择，开始时间、结束时间、以及统计维度")
    }

}
