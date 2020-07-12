new Vue({
    el:"#root",
    data:{
        page: 1,  //显示的是哪一页
        pageSize: 10, //每一页显示的数据条数
        total: 0, //记录总数
        maxPage:0,
        searchEntity:{},
        contentList:{},
        categoryList:[],
        selectIds:-1,
        selectIdList:[],
        status:{0:"有效",1:"无效"},
        contentEntity:{}
    },
    methods:{
        pageHandler: function (page) {
            var _this = this;
            this.page = page;
            axios.post("/content/search.do?page="+page+"&rows="+this.pageSize,this.searchEntity)
                .then(function (response) {
                    //取服务端响应的结果
                    _this.contentList = response.data.rows;
                    _this.total = response.data.total;
                    console.log(response.data.rows)
                }).catch(function (reason) {
                console.log(reason);
            })
        },
        loadCategoryContent:function () {
            var _this = this;
            axios.post("/contentCategory/findAll.do")
                .then(function (response) {
                    //取服务端响应的结果
                    console.log(response.data);
                    _this.categoryList = response.data;
                    console.log(response.data);
                }).catch(function (reason) {
                console.log(reason);
            })
        },
        uploadFile:function () {
            var formData = new FormData();
            formData.append('file', file.files[0])
            const instance=axios.create({
                withCredentials: true
            });
            var _this = this;
            instance.post("/upload/uploadFile.do", formData).then(function (response) {
                console.log(response.data);
                _this.contentEntity.pic = response.data.msg;
            }).catch(function (reason) {
                console.log(reason);
            });
        },
        saveContent:function () {
            var _this = this;
            var url = "";
            if (_this.contentEntity.id != null){
                url = "/content/update.do";
            } else {
                url = "/content/add.do";
            }
            axios.post(url,this.contentEntity)
                .then(function (response) {
                    //取服务端响应的结果
                    _this.pageHandler(1);
                    _this.contentEntity={};
                    _this.selectIds = -1;
                }).catch(function (reason) {
                console.log(reason);
            })
        },
        getCatSelected(){
            console.log(this.selectIds);
            this.contentEntity.categoryId = this.selectIds;
        },
        findOne:function(id){
            var _this = this;
            axios.post("/content/findOne.do?id="+id)
                .then(function (response) {
                    //刷新页面
                    console.log(response.data)
                    _this.contentEntity  = response.data;
                }).catch(function (reason) {
                console.log(reason);
            });
        },
        updateSelection:function (event,id) {
            // 复选框选中
            if(event.target.checked){
                // 向数组中添加元素
                this.selectIdList.push(id);
            }else{
                // 从数组中移除
                var idx = this.selectIdList.indexOf(id);
                this.selectIdList.splice(idx,1);
            }
        },
        deleteContent:function () {
            var _this = this;
            //使用qs插件 处理数组
            axios.post('/content/delete.do',Qs.stringify({ids: _this.selectIdList},{ indices: false }))
                .then(function (response) {
                    _this.pageHandler(_this.page);
                    _this.selectIds = [];
                }).catch(function (reason) {
                alert(reason.message);
            })
        }
    },
    created: function() {
        this.pageHandler(1);
        this.loadCategoryContent();
    },
});