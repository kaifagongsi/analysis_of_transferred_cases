var tableThead = [];
var tableData = [];
var table ;
var loadingSpinner ;
var laypage;
var tableTheadDep = [];
var tableDataDep = [];
layui.use(['laydate','dropdown','element','table','echarts','form','layer','laypage'], function () {
    // 日期组件
    var laydate = layui.laydate;
    //执行一个laydate实例
    laydate.render({
        elem: '#startDate', //指定元素
        format: "yyyyMMdd"
        ,type: 'date'
    });
    laydate.render({
        elem: '#endDate', //指定元素
        format: "yyyyMMdd"
        ,type: 'date'
    });

    //下拉级联
    var form  = layui.form;

    form.on('select(firstClassify)',function (data) {
        console.log(data);
        $("#secondClassify").empty();
        if(data.value == '1'){
            findAllUser();
        }else if(data.value == '4'){
            findFieldGroup();
        }else{
            initSecondClassify(data.value);
        }
    });

    // 卡片切换
    var element = layui.element;
    //一些事件触发
    element.on('tab(tabBrief)', function(data){
        console.log(data);
    });

    // 表格
    table = layui.table;
    //展示已知数据
    table.render({
        elem: '#demoTable'
        ,cols: tableThead
        ,data: tableData
        ,skin: 'line' //表格风格
        ,even: true
        ,page: false //是否显示分页
    });



    laypage = layui.laypage;

    laypage.render({
        elem: 'pageNav' //注意，这里的 test1 是 ID，不用加 # 号
    });

    //展示已知数据
    table.render({
        elem: '#depTable'
        ,cols: tableTheadDep
        ,data: tableDataDep
        ,skin: 'line' //表格风格
        ,even: true
        ,page: false //是否显示分页
    });
    //柱状图
    var echarts = layui.echarts;
    var chartZhu = echarts.init(document.getElementById('demoEcharts'));
    //指定图表配置项和数据
    var optionchart = {
        title: {
            text: '商品订单'
        },
        tooltip: {},
        legend: {
            data: ['销量','产量']
        },
        toolbox:{
            show: true,
            feature:{
                magicType: {
                    type: ["line", "bar","stack"]
                },
                saveAsImage: {}
            }
        },
        xAxis: {
            data: ['周一', '周二', '周三', '周四', '周五', '周六', '周天']
        },
        yAxis: {
            type: 'value'
        },
        series: [{
            name: '销量',
            type: 'bar', //柱状
            data: [100,200,300,400,500,600,700],
            itemStyle: {
                normal: { //柱子颜色
                    color: 'red'
                }
            },
        },{
            name:'产量',
            type:'bar',
            data:[120,210,340,430,550,680,720],
            itemStyle:{
                normal:{
                    color:'blue'
                }
            }
        }]
    };
    chartZhu.setOption(optionchart, true);


})
/**
 * 有效转出率 Effective transfer out rate
 */
function effectiveTransferOutRate() {

   /* $.post(ctx + "/sys/sysAuthority/save",date,function (date) {
    })*/
}

/**
 * 查找所有用户，用于级联使用
 */
function findAllUser() {
    $.get(ctx + "/ceshi/findAllUser",function (response) {
        if(response.data.length != 0){
            //清空下拉框
            $.each(response.data,function (index,item) {
                //赋值
                $("#secondClassify").append(new Option(item.ename, item.classifiersCode));
            })
        }else{
            $("#secondClassify").append(new Option("暂无数据", ""));
        }
        layui.form.render("select");
    })
}

/**
 * 用于级联
 * @param value
 */
function initSecondClassify(value) {
    if("2" == value){
        $("#secondClassify").append(new Option("一部","一部"));
        $("#secondClassify").append(new Option("二部","二部"));
        $("#secondClassify").append(new Option("三部","三部"));
        $("#secondClassify").append(new Option("四部","四部"));
    }else if("3" == value){
        $("#secondClassify").append(new Option("一部一室","一部一室"));
        $("#secondClassify").append(new Option("一部二室","一部二室"));
        $("#secondClassify").append(new Option("一部三室","一部三室"));
        $("#secondClassify").append(new Option("二部一室","二部一室"));
        $("#secondClassify").append(new Option("二部二室","二部二室"));
        $("#secondClassify").append(new Option("二部三室","二部三室"));
        $("#secondClassify").append(new Option("三部一室","三部一室"));
        $("#secondClassify").append(new Option("三部二室","三部二室"));
        $("#secondClassify").append(new Option("三部三室","三部三室"));
        $("#secondClassify").append(new Option("三部四室","三部四室"));
        $("#secondClassify").append(new Option("三部五室","三部五室"));
        $("#secondClassify").append(new Option("四部一室","四部一室"));
        $("#secondClassify").append(new Option("四部二室","四部二室"));
        $("#secondClassify").append(new Option("四部三室","四部三室"));
        $("#secondClassify").append(new Option("四部四室","四部四室"));
    }
    layui.form.render("select");
}

function findFieldGroup() {
    $.get(ctx + "/ceshi/findAllFieldGroup", function (response) {
        console.log(response)
        if(response.data.length != 0){
            $.each(response.data,function (index,item) {
                $("#secondClassify").append(new Option(item, item));
            });
        }else{
            $("#secondClassify").append(new Option("暂无数据", ""));
        }
        layui.form.render("select");
    });
}

function ceshi() {
    loadingSpinner = layer.msg('正在加载...', {icon: 16, shade: 0.3, time:0});
    $.post(ctx + "/ceshi/count/count", function (response) {
        if(response.flag){
            // 获取表头：
            let entityObj = response.data[0];
            let h = [];
            // 拼装表头
            $.each(entityObj,function (index,obj) {
                h.push({field: index, title: index});
            })
            tableThead.push(h);
            // 拼装 数据
            $.each(response.data,function (index,object) {
                console.log(index)
                console.log(object)
                tableData.push(object)
            })
            // 表格重载
            table.reload('demoTable',{
                cols: tableThead ,
                data: tableData,
            })
        }else{
            layer.msg('数据加载失败，请稍候重试', {icon: 5})
        }
        layer.close(loadingSpinner);
    });
}

function testPost() {
    let infoForm = $("#infoForm").serializeObject();

    $.post(ctx + "/ceshi/testPost", infoForm,function (data) {
        alert(data);
    });
}


/**
 * 有效转入率
 * */
function etir( page,rows) {
    let infoForm = $.param({"rows":rows}) + "&" +  $.param({"page":page}) + "&" + $("#infoForm").serialize();
    if(vaildateForm()){
        loadingSpinner = layer.msg('正在加载...', {icon: 16, shade: 0.3, time:0});
        $.post(ctx + "/etir/init", infoForm,function (response) {
            tableThead = [];
            tableData = [];
            if(response.flag){
                // 获取表头：
                let entityObj = response.data.rows[0];
                let h = [];
                // 拼装表头
                $.each(entityObj,function (index,obj) {
                    h.push({field: index, title: index});
                });
                tableThead.push(h);
                // 拼装 数据
                $.each(response.data.rows,function (index,object) {
                    tableData.push(object)
                });
                // 表格重载
                table.reload('demoTable',{
                    cols: tableThead ,
                    data: tableData,
                    limit: rows
                });
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
                            etir( obj.curr,obj.limit);
                        }
                    }
                });
            }else{
                layer.msg('数据加载失败，请稍候重试', {icon: 5})
            }
            layer.close(loadingSpinner);
        });
    }else{
        layer.msg("请正确选择，开始时间、结束时间、以及统计维度")
    }
}

function etirAll( ) {
    let infoForm = $("#infoForm").serialize();
    let firstClassify =  $("#firstClassify").val();
    if(firstClassify != 0 && firstClassify != 1 ){
        if(vaildateForm()){
            if($("#secondClassify").val() != 1){
                $.post(ctx + "/etir/initAll",infoForm,function (response) {
                    console.log(response)
                    tableTheadDep = [];
                    tableDataDep = [];
                    if(response.flag){
                        // 获取表头：
                        let entityObj = response.data.rows[0];
                        let h = [];
                        // 拼装表头
                        $.each(entityObj,function (index,obj) {
                            h.push({field: index, title: index});
                        })
                        tableTheadDep.push(h);
                        // 拼装 数据
                        $.each(response.data.rows,function (index,object) {
                            tableDataDep.push(object)
                        })
                        // 表格重载
                        table.reload('depTable',{
                            cols: tableTheadDep ,
                            data: tableDataDep
                        })
                    }else{
                        layer.msg('数据加载失败，请稍候重试', {icon: 5})
                    }
                });
            }
        }
    }
}
function vaildateForm() {
    let startDate =  $("#startDate").val();
    let endDate =  $("#endDate").val();
    let secondClassify =  $("#secondClassify").val();
    if(startDate != "" && endDate != "" && secondClassify != ""){
        return true;
    }else{
        return  false;
    }
}