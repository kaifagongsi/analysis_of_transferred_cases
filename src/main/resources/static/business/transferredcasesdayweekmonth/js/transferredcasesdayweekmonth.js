function tcbd(type,page,rows) {
    if(vaildateForm()){
       // let infoForm = $("#infoForm").serializeObject();
        let infoForm = $.param({"rows":rows}) + "&" +  $.param({"page":page}) + "&" + $("#infoForm").serialize();
        loadingSpinner = layer.msg('正在加载...', {icon: 16, shade: 0.3, time:0});
        $.post(ctx + "/tcbd/init/"+type,infoForm,function (response) {
            if(response.flag){
                tableThead = [];
                tableData = [];
                tableTheadDep = [];
                tableDataDep = [];
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
                table.reload('depTable',{
                    cols: tableTheadDep ,
                    data: tableDataDep
                });
                // 表格重载
                table.reload('demoTable',{
                    cols: tableThead ,
                    data: tableData,
                    limit: response.data.rows.length
                })
                laypage.render({
                    elem : 'pageNav',
                    count : response.data.records,
                    limit : rows
                    ,layout: ['count', 'prev', 'page', 'next', 'limit', 'refresh', 'skip']
                    ,curr: page
                    ,jump: function(obj, first){
                        //obj包含了当前分页的所有参数，比如：
                        console.log(obj.curr); //得到当前页，以便向服务端请求对应页的数据。
                        console.log(obj.limit); //得到每页显示的条数
                        console.log(obj);
                        //首次不执行
                        if(!first){
                            tcbd( type,obj.curr,obj.limit);
                        }
                    }
                });
            }else{
                layer.msg(response.msg, {icon: 5})
            }
            layer.close(loadingSpinner);
        })
    }else{
        layer.msg("请正确选择，开始时间、结束时间、以及统计维度")
    }
}
