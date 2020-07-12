Vue.component('v-select', VueSelect.VueSelect);
new Vue({
    el: "#app",
    data: {
        tempList: [],
        searchTemp: {
            name: '',
        },
        addName:'',
        page: 1,  //显示的是哪一页
        pageSize: 10, //每一页显示的数据条数
        total: 150, //记录总数
        maxPage: 9,
        selectList: [], //记录选择了哪些记录的id

        placeholder:'可以多选',

        brandsOptions:[],
        selectedBrands:[],
        brands_obj:[],

        specsOptions:[],
        selectedSpecs:[],
        specs_obj:[]
    },
    methods:{
        pageHandler: function (page) {
            _this = this;
            this.page = page;
            axios.post("/temp/getPage.do?page="+page+"&pageSize="+this.pageSize+"",this.searchTemp)
                .then(function (response) {
                    //取服务端响应的结果
                    _this.tempList = response.data.rows;
                    _this.total = response.data.total;
                }).catch(function (reason) {
                console.log(reason);
            })
        },
        // 定义方法：获取JSON字符串中的某个key对应值的集合
        jsonToString : function(jsonStr,key){
            // 将字符串转成JSOn:
            var jsonObj = JSON.parse(jsonStr);
            var value = "";
            for(var i=0;i<jsonObj.length;i++){
                if(i>0){
                    value += ",";
                }
                value += jsonObj[i][key];
            }
            return value;
        },
        selected_brand: function(values){
            console.log(this.brands_obj);
            this.selectedBrands =values.map(function(obj){
                return obj;
            });
        },
        selected_spec: function(values){
            this.selectedSpecs =values.map(function(obj){
                return obj;
            });
        },
        selLoadData:function () {
            _this = this;
            axios.get("/brand/selectOptionList.do").then(function (response) {
                    _this.brandsOptions = response.data;
                }).catch(function (reason) {
                console.log(reason);
            });
            axios.get("/spec/selectOptionList.do").then(function (response) {
                _this.specsOptions = response.data;
            }).catch(function (reason) {
                console.log(reason);
            })
        },
        save:function () {
            var entity = {
                name:this.addName,
                specIds:this.specs_obj,
                brandIds:this.brands_obj,
            };
            axios.post("/temp/add.do",entity)
                .then(function (response) {
                    console.log(response);
                    _this.pageHandler(1);
                }).catch(function (reason) {
                console.log(reason);
            });
        },
        selects:function (event,id) {
            if(event.target.checked){
                this.selectList.push(id);
            }else {
                var index = this.selectList.indexOf(id);
                this.selectList.splice(index,1);
            }
        },
        tempDelete:function () {
            var _this = this;
            axios.post('/temp/delete.do',Qs.stringify({selectList:_this.selectList},{indices:false})).then(function (response) {
                console.log(_this.selectList);
                window.location.reload();
                _this.selectList = [];
            })
        },
        tempEdit(tempItem){
            console.log("还未做此功能");
        }
    },
    created:function () {
        this.pageHandler(1);
        this.selLoadData();
    }
})