new Vue({
    el:"#root",
    data:{
        brandList:[],
        brand:{
            name:'',
            firstChar:'',
        },
        searchBrand:{
            name:'',
            firstChar:'',
        },
        page: 1,  //显示的是哪一页
        pageSize: 10, //每一页显示的数据条数
        total: 150, //记录总数
        maxPage:9,
        selectList:[],
    },
    methods:{
        pageHandler: function (page) {
            _this = this;
            this.page = page;
            axios.post('/brand/getPage.do?page='+page+'&pageSize='+this.pageSize+'',this.searchBrand)
                .then(function (response) {
                    //取服务端响应的结果
                    _this.brandList = response.data.rows;
                    _this.total = response.data.total;
                    console.log(_this.searchBrand);
                }).catch(function (reason) {
                    console.log(reason);
            })
        },
        brandSave:function () {
             var url='';
            _this = this;
            if(this.brand.id==null){
                url='/brand/add.do';
            }else {
                url='/brand/update.do';
            }
            axios.post(url,_this.brand).then(function (response) {
                alert(response.data.msg);
                _this.pageHandler(1);
                _this.brand={};
            })
        },
        edit:function (id) {
            _this = this;
            axios.get('/brand/getBrandById.do',{params:{id:id}}).then(function (response) {
                _this.brand = response.data;
                console.log(response.data);
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
            console.log(this.selectList);
        },
        deleteBrand:function () {
            var _this = this;
            axios.post('/brand/delete.do',Qs.stringify({selectList:_this.selectList},{indices:false})).then(function (response) {
                _this.selectList = [];
                window.location.reload();
            })
        },

    },
    created: function() {
        this.pageHandler(1);
    }
});

