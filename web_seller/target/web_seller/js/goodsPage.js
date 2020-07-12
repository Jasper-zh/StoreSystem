new Vue({
    el: "#root",
    data: {
        page: 1,  //显示的是哪一页
        pageSize: 10, //每一页显示的数据条数
        total: 0, //记录总数
        maxPage: 0,
        searchEntity: {},
        goodsList: [],
        status:{0:"未申请",1:"申请中",2:"审核通过",3:"已驳回"},
        itemCatList:{},
        selectIds:[] //记录选择了哪些记录的id
    },
    methods: {
        pageHandler: function (page) {
            let _this = this;
            this.page = page;
            axios.post("/goods/search.do?page=" + page + "&rows=" + this.pageSize, this.searchEntity)
                .then(function (response) {
                    //取服务端响应的结果
                    _this.goodsList = response.data.rows;
                    _this.total = response.data.total;
                    console.log(response);
                }).catch(function (reason) {
                console.log(reason);
            })
        },
        loadCatData:function () {
            let _this = this;
            axios.post("/itemCat/findAll.do")
                .then(function (response) {
                    //取服务端响应的结果
                    console.log(response.data);
                    for (var i = 0; i < response.data.length; i++){
                        var category =  response.data[i];
                        _this.itemCatList[category.id] = category.name;

                    }
                    _this.pageHandler(1);
                }).catch(function (reason) {
                console.log(reason);
            })
        },
        deleteSelection:function (event,id) {
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
        deleteGoods:function () {

             _this = this;
            //使用qs插件 处理数组
            axios.post('/goods/delete.do',Qs.stringify({ids: _this.selectIds},{ indices: false }))
                .then(function (response) {
                    _this.pageHandler(_this.page);
                    _this.selectIds = [];
                    alert("删除成功");
                }).catch(function (reason) {
                alert(reason.message);
            })
        },
        updateStatus:function (status) {
            var ids = Qs.stringify({ids: this.selectIds},{ indices: false })
            var _this = this;
            axios.get("/goods/updateStatus.do?"+ids+"&status="+status)
                .then(function (response) {
                    _this.loadCategoryData();
                    _this.selectIds = [];
                    $("input[type='checkbox']").prop("checked",false);
                }).catch(function (reason) {
                console.log(reason);
            })
        }
    },
    created: function () {
        this.loadCatData();
    }

});