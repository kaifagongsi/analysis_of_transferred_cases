layui.use(['laydate','dropdown','element','table','echarts','form'], function () {
    // 日期组件
    var laydate = layui.laydate;
    //执行一个laydate实例
    laydate.render({
        elem: '#startDate' //指定元素
    });
    laydate.render({
        elem: '#endDate' //指定元素
    });
    // 下拉组件
    var dropdown = layui.dropdown
    dropdown.render({
        elem: '#casesTransferred' //可绑定在任意元素中，此处以上述按钮为例
        ,data: [{
            title: 'menu item 1'
            ,id: 100
            ,href: '#'
        },{
            title: 'menu item 2'
            ,id: 101
            ,href: 'https://www.layui.com/' //开启超链接
            ,target: '_blank' //新窗口方式打开
        },{type: '-'},{
            title: 'menu item 3'
            ,id: 102
            ,type: 'group'  //菜单类型，支持：normal/group/parent/-
            ,child: [{
                title: 'menu item 3-1'
                ,id: 103
            },{
                title: 'menu item 3-2'
                ,id: 104
                ,child: [{
                    title: 'menu item 3-2-1'
                    ,id: 105
                },{
                    title: 'menu item 3-2-2'
                    ,id: 106
                }]
            },{
                title: 'menu item 3-3'
                ,id: 107
            }]
        },{type: '-'},{
            title: 'menu item 4'
            ,id: 108
        },{
            title: 'menu item 5'
            ,id: 109
            ,child: [{
                title: 'menu item 5-1'
                ,id: 11111
                ,child: [{
                    title: 'menu item 5-1-1'
                    ,id: 2111
                },{
                    title: 'menu item 5-1-2'
                    ,id: 3111
                }]
            },{
                title: 'menu item 5-2'
                ,id: 52
            }]
        },{type:'-'},{
            title: 'menu item 6'
            ,id: 6
            ,type: 'group'
            ,isSpreadItem: false
            ,child: [{
                title: 'menu item 6-1'
                ,id: 61
            },{
                title: 'menu item 6-2'
                ,id: 62
            }]
        }]
        ,id: 'casesTransferred'
        //菜单被点击的事件
        ,click: function(obj){
            console.log(obj);
            layer.msg('回调返回的参数已显示再控制台');
        }
    });

    //下拉级联
    var form  = layui.form;

    form.on('select(firstClassify)',function (data) {
        console.log(data)
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
        //,skin: 'line' //表格风格
        even: true,
        page: true, //是否显示分页
        //,limits: [5, 7, 10]
        limit: 5 //每页默认显示的数量
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
            data: ['销量']
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

    var optionchartZhe = {
        title: {
            text: '商品订单'
        },
        tooltip: {},
        legend: { //顶部显示 与series中的数据类型的name一致
            data: ['销量', '产量', '营业额', '单价']
        },
        xAxis: {
            // type: 'category',
            // boundaryGap: false, //从起点开始
            data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
        },
        yAxis: {
            type: 'value'
        },
        series: [{
            name: '销量',
            type: 'line', //线性
            data: [145, 230, 701, 734, 1090, 1130, 1120],
        }, {
            name: '产量',
            type: 'line', //线性
            data: [720, 832, 801, 834, 1190, 1230, 1220],
        }, {
            smooth: true, //曲线 默认折线
            name: '营业额',
            type: 'line', //线性
            data: [820, 932, 901, 934, 1290, 1330, 1320],
        }, {
            smooth: true, //曲线
            name: '单价',
            type: 'line', //线性
            data: [220, 332, 401, 534, 690, 730, 820],
        }]
    };

    var optionchartBing = {
        title: {
            text: '商品订单',
            subtext: '纯属虚构', //副标题
            x: 'center' //标题居中
        },
        tooltip: {
            // trigger: 'item' //悬浮显示对比
        },
        legend: {
            orient: 'vertical', //类型垂直,默认水平
            left: 'left', //类型区分在左 默认居中
            data: ['单价', '总价', '销量', '产量']
        },
        series: [{
            type: 'pie', //饼状
            radius: '60%', //圆的大小
            center: ['50%', '50%'], //居中
            data: [{
                value: 335,
                name: '单价'
            },
                {
                    value: 310,
                    name: '总价'
                },
                {
                    value: 234,
                    name: '销量'
                },
                {
                    value: 135,
                    name: '产量'
                }
            ]
        }]
    };
    chartZhu.setOption(optionchart, true);
})