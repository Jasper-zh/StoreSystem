new Vue({
    el:"#root",
    data:{
        contentList:[],
        searchContent:'',
    },
    methods:{
        loadCategoryDataById: function (categoryId) {
            var _this = this;
            axios.post("/content/findByCategoryId.do?categoryId="+categoryId)
                .then(function (response) {
                    //取服务端响应的结果
                    console.log(response.data);
                    _this.contentList[categoryId] = response.data;
                    // 在全局对象上通知
                    Vue.set(_this.contentList,categoryId,_this.contentList[categoryId]);
                }).catch(function (reason) {
                console.log(reason.data);
            });

        }
    },
    created: function() {
        this.loadCategoryDataById(1);
    },
});
