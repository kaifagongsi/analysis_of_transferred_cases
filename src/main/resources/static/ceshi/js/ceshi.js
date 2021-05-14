layui.use(['laydate','dropdown','element','table','echarts','form','layer'], function () {
    // 日期组件
    var laydate = layui.laydate;
    //执行一个laydate实例
    laydate.render({
        elem: '#startDate', //指定元素
        format: "yyyy-MM-dd HH:mm:ss"
        ,type: 'datetime'
    });
    laydate.render({
        elem: '#endDate', //指定元素
        format: "yyyy-MM-dd HH:mm:ss"
        ,type: 'datetime'
    });

    //下拉级联
    var form  = layui.form;

    form.on('select(firstClassify)',function (data) {
        console.log(data);
        layer.msg(data.value,{icon: 1,time: 2000}, function () {});
        $("#secondClassify").empty();
        if(data.value == '个人转案情况'){
            findAllUser();
        }else if(data.value == '领域转案情况'){
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
    var table = layui.table;
    //展示已知数据
    table.render({
        elem: '#demoTable',
        cols: [[ //标题栏
            {field: 'id', title: 'ID', width: 80, sort: true}
            ,{field: 'username', title: '用户名', width: 120}
            ,{field: 'email', title: '邮箱', minWidth: 150}
            ,{field: 'sign', title: '签名', minWidth: 160}
            ,{field: 'sex', title: '性别', width: 80}
            ,{field: 'city', title: '城市', width: 100}
            ,{field: 'experience', title: '积分', width: 80, sort: true}
        ]],
        data: [{
            "id": "10001"
            ,"username": "杜甫"
            ,"email": "xianxin@layui.com"
            ,"sex": "男"
            ,"city": "浙江杭州"
            ,"sign": "人生恰似一场修行"
            ,"experience": "116"
            ,"ip": "192.168.0.8"
            ,"logins": "108"
            ,"joinTime": "2016-10-14"
        }, {
            "id": "10002"
            ,"username": "李白"
            ,"email": "xianxin@layui.com"
            ,"sex": "男"
            ,"city": "浙江杭州"
            ,"sign": "人生恰似一场修行"
            ,"experience": "12"
            ,"ip": "192.168.0.8"
            ,"logins": "106"
            ,"joinTime": "2016-10-14"
            ,"LAY_CHECKED": true
        }, {
            "id": "10003"
            ,"username": "王勃"
            ,"email": "xianxin@layui.com"
            ,"sex": "男"
            ,"city": "浙江杭州"
            ,"sign": "人生恰似一场修行"
            ,"experience": "65"
            ,"ip": "192.168.0.8"
            ,"logins": "106"
            ,"joinTime": "2016-10-14"
        }, {
            "id": "10004"
            ,"username": "贤心"
            ,"email": "xianxin@layui.com"
            ,"sex": "男"
            ,"city": "浙江杭州"
            ,"sign": "人生恰似一场修行"
            ,"experience": "666"
            ,"ip": "192.168.0.8"
            ,"logins": "106"
            ,"joinTime": "2016-10-14"
        }, {
            "id": "10005"
            ,"username": "贤心"
            ,"email": "xianxin@layui.com"
            ,"sex": "男"
            ,"city": "浙江杭州"
            ,"sign": "人生恰似一场修行"
            ,"experience": "86"
            ,"ip": "192.168.0.8"
            ,"logins": "106"
            ,"joinTime": "2016-10-14"
        }, {
            "id": "10006"
            ,"username": "贤心"
            ,"email": "xianxin@layui.com"
            ,"sex": "男"
            ,"city": "浙江杭州"
            ,"sign": "人生恰似一场修行"
            ,"experience": "12"
            ,"ip": "192.168.0.8"
            ,"logins": "106"
            ,"joinTime": "2016-10-14"
        }, {
            "id": "10007"
            ,"username": "贤心"
            ,"email": "xianxin@layui.com"
            ,"sex": "男"
            ,"city": "浙江杭州"
            ,"sign": "人生恰似一场修行"
            ,"experience": "16"
            ,"ip": "192.168.0.8"
            ,"logins": "106"
            ,"joinTime": "2016-10-14"
        }, {
            "id": "10008"
            ,"username": "贤心"
            ,"email": "xianxin@layui.com"
            ,"sex": "男"
            ,"city": "浙江杭州"
            ,"sign": "人生恰似一场修行"
            ,"experience": "106"
            ,"ip": "192.168.0.8"
            ,"logins": "106"
            ,"joinTime": "2016-10-14"
        }],
        skin: 'line', //表格风格
        even: true,
        page: false //是否显示分页
        //,limit: 10 //每页默认显示的数量
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
   // let value = $("#firstClassify").val();
    if("部级转案情况" == value){
        $("#secondClassify").append(new Option("一部","一部"));
        $("#secondClassify").append(new Option("二部","二部"));
        $("#secondClassify").append(new Option("三部","三部"));
        $("#secondClassify").append(new Option("四部","四部"));
    }else if("室级转案情况" == value){
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
    //alert("ceshi")
    $.post(ctx + "/ceshi/count/count", function (data) {
        alert(data);
    });
}