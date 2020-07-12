new Vue({
    el:"#app",
    data:{
        specList:[],
        searchSpec:{
            specName:''
        },
        specEntity:{
            spec:{},
            specOption:[],
        },
        page: 1,  //显示的是哪一页
        pageSize: 10, //每一页显示的数据条数
        total: 150, //记录总数
        maxPage:9,
        selectList:[], //记录选择了哪些记录的id
    },
    methods: {
        pageHandler: function (page) {
            _this = this;
            this.page = page;
            axios.post("/spec/getPage.do?page="+page+"&pageSize="+this.pageSize+"",this.searchSpec)
                .then(function (response) {
                    //取服务端响应的结果
                    _this.specList = response.data.rows;
                    _this.total = response.data.total;
                }).catch(function (reason) {
                console.log(reason);
            })
        },
        addRow:function () {
            this.specEntity.specOption.push({});
        },
        deleteRow:function(index){
            this.specEntity.specOption.splice(index,1);
        },
        specSave:function () {
            var _this = this;
            var url = '';
            if(this.specEntity.spec.id!=null){
                url='/spec/specUpdate.do';
            }else {
                url='/spec/specSave.do';
            }
            axios.post(url,this.specEntity).then(function (response) {
                console.log(_this.specEntity)
                if(response.data.success){
                    _this.specEntity.spec = {};
                    _this.specEntity.specOption=[];
                    _this.pageHandler(1);
                }
            }).catch(function (reason) {
                console.log(reason);
            })
        },
        getSpec:function (id) {
            var _this = this;
            axios.get('/spec/getSpecEntityById.do',{params:{id:id}}).then(function (response) {
                 console.log(response.data.specOption)
                _this.specEntity = response.data;
            }).catch(function (reason) {
                console.log(reason);
            })
        },
        selects:function (even,id) {
            if(even.target.checked){
                this.selectList.push(id);
            }else {
                var index = this.selectList.indexOf(id);
                this.selectList.splice(index,1);
            }
        },
        specDelete:function () {
            var _this = this;
            axios.post('/spec/delete.do',Qs.stringify({selectList:_this.selectList},{indices:false})).then(function (response) {
                window.location.reload();
                _this.selectList = [];
            })
        }
    },
    created: function() {
        this.pageHandler(1);
    }

});