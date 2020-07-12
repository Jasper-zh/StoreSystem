new Vue({
    el:"#root",
    data:{
        page: 1,  //显示的是哪一页
        pageSize: 10, //每一页显示的数据条数
        total: 0, //记录总数
        maxPage:0,
        searchEntity:{},
        categoryList:[],
        selectIds:[], //记录选择了哪些记录的id
        categoryEntity:{},
    },
    methods:{
        pageHandler: function (page) {
            _this = this;
            this.page = page;
            axios.post("/contentCategory/search.do?page="+page+"&rows="+this.pageSize,this.searchEntity)
                .then(function (response) {
                    //取服务端响应的结果
                    _this.categoryList = response.data.rows;
                    _this.total = response.data.total;

                }).catch(function (reason) {
                console.log(reason);
            })
        },
        updateSelection:function (event,id) {
            // 复选框选中
            if(event.target.checked){
                // 向数组中添加元素
                this.selectIds.push(id);
            }else{
                // 从数组中移除
                var idx = this.selectIds.indexOf(id);
                this.selectIds.splice(idx,1);
            }
        },
        deleteCategory:function () {
            var _this = this;
            //使用qs插件 处理数组
            axios.post('/contentCategory/delete.do',Qs.stringify({ids: _this.selectIds},{ indices: false }))
                .then(function (response) {
                    _this.pageHandler(_this.page);
                    _this.selectIds = [];
                    alert("删除成功")
                }).catch(function (reason) {
                alert(reason.message);
            })
        },
        saveCategory:function () {
            let _this = this;
            var url = "";
            if (_this.categoryEntity.id != null){
                url = "/contentCategory/update.do";
            } else {
                url = "/contentCategory/add.do";
            }
            axios.post(url, _this.categoryEntity)
                .then(function (value) {
                    console.log(value.data);
                    //刷新页面
                    _this.pageHandler(_this.page);
                    _this.categoryEntity = {};
                }).catch(function (reason) {
                console.log(reason);
            });
        },
        findOne:function(id){
            var _this = this;
            axios.post("/contentCategory/findOne.do?id="+id)
                .then(function (response) {
                    //刷新页面
                    _this.categoryEntity  = response.data;
                }).catch(function (reason) {
                console.log(reason);
            });
        }
    },
    created: function() {
        this.pageHandler(1);
    },
});