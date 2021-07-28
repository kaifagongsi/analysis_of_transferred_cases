layui.use(['upload', 'element', 'layer'], function () {
    var $ = layui.jquery
        ,upload = layui.upload
        ,element = layui.element
        ,layer = layui.layer;
    var layer_load = null;
    var uploadListInsZ = upload.render({
        elem: '#fileListZ'
        ,elemList: $('#demoListZ') //列表元素对象
        ,url: '/importdata/importFileZ'
        ,accept: 'file'
        ,acceptMime: 'application/vnd.ms-excel, application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
        ,exts: 'xlsx|xls' //只允许上传excel文件
        ,multiple: true
        ,number: 10
        ,auto: false
        ,bindAction: '#listActionZ'
        ,before: function(obj){ //obj参数包含的信息，跟 choose回调完全一致，可参见上文。
            layer_load = layer.load(1); //上传loading
        }
        ,choose: function(obj){
            var that = this;
            var files = this.files = obj.pushFile(); //将每次选择的文件追加到文件队列
            //读取本地文件
            obj.preview(function(index, file, result){
                var tr = $(['<tr id="upload-'+ index +'">'
                    ,'<td>'+ file.name +'</td>'
                    ,'<td>'+ (file.size/1014).toFixed(1) +'kb</td>'
                    ,'<td><div class="layui-progress" lay-filter="progress-demo-'+ index +'"><div class="layui-progress-bar" lay-percent=""></div></div></td>'
                    ,'<td>'
                    ,'<button class="layui-btn layui-btn-xs demo-reload layui-hide">重传</button>'
                    ,'<button class="layui-btn layui-btn-xs layui-btn-danger demo-delete">删除</button>'
                    ,'</td>'
                    ,'</tr>'].join(''));

                //单个重传
                tr.find('.demo-reload').on('click', function(){
                    obj.upload(index, file);
                });

                //删除
                tr.find('.demo-delete').on('click', function(){
                    delete files[index]; //删除对应的文件
                    tr.remove();
                    uploadListInsZ.config.elem.next()[0].value = ''; //清空 input file 值，以免删除后出现同名文件不可选
                });

                that.elemList.append(tr);
                element.render('progress'); //渲染新加的进度条组件
            });
        }
        ,done: function(res, index, upload){ //成功的回调
            debugger
            var that = this;
            if(res.flag){ //上传成功
                var tr = that.elemList.find('tr#upload-'+ index)
                    ,tds = tr.children();
                tds.eq(3).html(''); //清空操作
                layer.msg(this.files[index].name+"上传成功！",{icon: 6});
                delete this.files[index]; //删除文件队列已经上传成功的文件
                return;
            }
            this.error(index, upload);
        }
        ,allDone: function(obj){ //多文件上传完毕后的状态回调
            layer.close(layer_load);
        }
        ,error: function(index, upload){ //错误回调
            var that = this;
            var tr = that.elemList.find('tr#upload-'+ index)
                ,tds = tr.children();
            tds.eq(3).find('.demo-reload').removeClass('layui-hide'); //显示重传
        }
        ,progress: function(n, elem, e, index){ //注意：index 参数为 layui 2.6.6 新增
            element.progress('progress-demo-'+ index, n + '%'); //执行进度条。n 即为返回的进度百分比
        }
    });

    var uploadListInsC = upload.render({
        elem: '#fileListC'
        ,elemList: $('#demoListC') //列表元素对象
        ,url: '/importdata/importFileC'
        ,accept: 'file'
        ,acceptMime: 'application/vnd.ms-excel, application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
        ,exts: 'xlsx|xls' //只允许上传excel文件
        ,multiple: true
        ,number: 10
        ,auto: false
        ,bindAction: '#listActionC'
        ,before: function(obj){ //obj参数包含的信息，跟 choose回调完全一致，可参见上文。
            layer_load = layer.load(); //上传loading
        }
        ,choose: function(obj){
            var that = this;
            var files = this.files = obj.pushFile(); //将每次选择的文件追加到文件队列
            //读取本地文件
            obj.preview(function(index, file, result){
                var tr = $(['<tr id="upload-'+ index +'">'
                    ,'<td>'+ file.name +'</td>'
                    ,'<td>'+ (file.size/1014).toFixed(1) +'kb</td>'
                    ,'<td><div class="layui-progress" lay-filter="progress-demo-'+ index +'"><div class="layui-progress-bar" lay-percent=""></div></div></td>'
                    ,'<td>'
                    ,'<button class="layui-btn layui-btn-xs demo-reload layui-hide">重传</button>'
                    ,'<button class="layui-btn layui-btn-xs layui-btn-danger demo-delete">删除</button>'
                    ,'</td>'
                    ,'</tr>'].join(''));

                //单个重传
                tr.find('.demo-reload').on('click', function(){
                    obj.upload(index, file);
                });

                //删除
                tr.find('.demo-delete').on('click', function(){
                    delete files[index]; //删除对应的文件
                    tr.remove();
                    uploadListInsC.config.elem.next()[0].value = ''; //清空 input file 值，以免删除后出现同名文件不可选
                });

                that.elemList.append(tr);
                element.render('progress'); //渲染新加的进度条组件
            });
        }
        ,done: function(res, index, upload){ //成功的回调
            var that = this;
            if(res.flag ){ //上传成功
                var tr = that.elemList.find('tr#upload-'+ index)
                    ,tds = tr.children();
                tds.eq(3).html(''); //清空操作
                layer.msg(this.files[index].name+"上传成功！",{icon: 6});
                delete this.files[index]; //删除文件队列已经上传成功的文件
                return;
            }
            this.error(index, upload);
        }
        ,allDone: function(obj){ //多文件上传完毕后的状态回调
            layer.close(layer_load);
        }
        ,error: function(index, upload){ //错误回调
            var that = this;
            var tr = that.elemList.find('tr#upload-'+ index)
                ,tds = tr.children();
            tds.eq(3).find('.demo-reload').removeClass('layui-hide'); //显示重传
        }
        ,progress: function(n, elem, e, index){ //注意：index 参数为 layui 2.6.6 新增
            element.progress('progress-demo-'+ index, n + '%'); //执行进度条。n 即为返回的进度百分比
        }
    });
});

