new Vue({
    el: "#root",
    data: {
        //多级分类列表
        categoryList1: [],//分类1数据列表
        categoryList2: [],//分类2数据列表
        categoryList3: [],//分类3数据列表
        grade: 1,  //记录当前级别
        catSelected1: -1,//分类1选中的id
        catSelected2: -1,//分类2选中的id
        catSelected3: -1,//分类3选中的id
        tempType: 0,
        //品牌列表
        brandList: [],
        selBrand: -1,
        imgColor: '',
        imgURL: '',
        imgEntitys: [],
        //规格列表
        specList: [],//从服务器获取的所有规格列表,
        //[
        // {"attributeName":"选择颜色","attributeValue":["秘境黑","星云紫"]},
        // {"attributeName":"套餐","attributeValue":["套餐1","套餐2"]}
        // ]
        specificationItems: [],//当前选择的规格集合,
        rowList:[],
        isEnableSpec:1,
        //商品实体
        goodsEntity:{
            goods:{},
            goodsDesc:{},
            itemList:[],
        }
    },
    methods: {
        GetQueryString: function (name) {
            var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i");
            var r = window.location.search.substr(1).match(reg);
            if (r != null) return (r[2]);
            return null;
        },
        loadCateData: function (id) {
            _this = this;
            axios.post("/itemCat/findByParentId.do?parentId=" + id)
                .then(function (response) {
                    if (_this.grade == 1) {
                        //取服务端响应的结果
                        _this.categoryList1 = response.data;
                    }
                    if (_this.grade == 2) {
                        //取服务端响应的结果
                        _this.categoryList2 = response.data;
                    }
                    if (_this.grade == 3) {
                        //取服务端响应的结果
                        _this.categoryList3 = response.data;
                    }
                }).catch(function (reason) {
                console.log(reason);
            })
        },
        getCatSelected: function (grade) {//选项改变时调用
            if (grade == 1) { //第1级选项改变
                this.categoryList2 = [];//清空二级分类数据
                this.catSelected2 = -1;   //默认选择
                this.categoryList3 = []; //清空三级分类数据
                this.catSelected3 = -1; //默认选择
                this.grade = grade + 1; // 加载第2级的数据
                this.loadCateData(this.catSelected1);
            }
            if (grade == 2) { //第2级选项改变
                this.categoryList3 = [];//清空三级分类数据
                this.catSelected3 = -1;//默认选择
                this.grade = grade + 1;// 加载第3级的数据
                this.loadCateData(this.catSelected2);
            }
            if (grade == 3) { //第3级选项改变
                //加载模板
                _this = this;
                axios.post("/itemCat/findOne.do?id=" + this.catSelected3).then(function (response) {
                    _this.tempType = response.data.typeId;
                }).catch(function (reason) {
                    console.log(reason);
                })
            }
        },
        uploadFile: function () {
            var formData = new FormData();
            _this = this;
            formData.append('file', file.files[0])
            var instance = axios.create({
                withCredentials: true
            });
            instance.post('/upload/uploadFile.do', formData).then(function (response) {
                console.log(response.data);
                _this.imgURL = response.data.msg;
            }).catch(function (reason) {
                console.log(reason);
            });
        },
        imgSave: function () {
            if (this.imgColor == '' || this.imgUrl == '') {
                alert("请输入颜色或上传图片");
                return;
            }
            var imgObj = {color: this.imgColor, url: this.imgURL};
            this.imgEntitys.push(imgObj);
            _this.imgColor = null;
            _this.imgURL = null;
        },
        imgDelete: function (url, index) {
            var _this = this;
            axios.get("/upload/deleteImg.do?url=" + url)
                .then(function (response) {
                    console.log(response.data);
                    // 从数组中移除
                    _this.imgEntitys.splice(index, 1);
                }).catch(function (reason) {
                console.log(reason);
            });
        },
        //       [
//             {"attributeName":"选择颜色","attributeValue":["秘境黑","星云紫"]},
// {"attributeName":"套餐","attributeValue":["套餐1","套餐2"]}
// ]
        searchObjectByKey: function (list, keyName, keyValue) {
            for (var i = 0; i < list.length; i++) {
                if (list[i][keyName] == keyValue) {
                    return list[i];
                }
            }
            return null;
        },
        updateSpecAttribute: function (event, name, value) { //name: 规格名称  value:规格选项
            // 调用封装的方法判断 勾选的名称是否存在:
            var object = this.searchObjectByKey(this.specificationItems, "attributeName", name);
            if (object != null) {//如果已经有对应的规格
                if (event.target.checked) { //查看当前是否为选中
                    object.attributeValue.push(value);//如果为选中 ,把规格选项添加到规格当中
                    console.log('已选择' + this.specificationItems);
                } else {
                    //取消选中, 把规格选项移除
                    object.attributeValue.splice(object.attributeValue.indexOf(value), 1);
                    console.log('已选择' + this.specificationItems);
                }
                if (object.attributeValue.length == 0) { //如果没有规格选项了, 就把规格移除
                    var idx = this.specificationItems.indexOf(object);
                    this.specificationItems.splice(idx, 1);
                    console.log('已选择' + this.specificationItems);
                }
            } else {
                // 如果没有选择名称的规格, 就把规格添加进去, 并添加规格选项
                this.specificationItems.push({"attributeName": name, "attributeValue": [value]});
                console.log('已选择' + this.specificationItems);
            }
            this.createRowList();
        },
        createRowList: function () {
            var rowList = [
                {spec: {}, price: 0, num: 9999, status: '0', isDefault: '0'}
            ];
            for (var i = 0; i < this.specificationItems.length; i++) {
                var specObj = this.specificationItems[i];
                var specName = specObj.attributeName; //选择版本
                var specOptions = specObj.attributeValue; //["6G+64G","8G+128G"]
                var newRowList = [];
                for (var j = 0; j < rowList.length; j++) {
                    var oldRow = rowList[j]; //{spec:{选择颜色:星云紫},price:0,num:9999,status:'0',isDefault:'0'}
                    for (var k = 0; k < specOptions.length; k++) {
                        var newRow = JSON.parse(JSON.stringify(oldRow));
                        //{spec:{选择颜色:星云紫,选择版本:8G+128G},price:0,num:9999,status:'0',isDefault:'0'}
                        newRow.spec[specName] = specOptions[k];
                        newRowList.push(newRow);
                    }
                }
                rowList = newRowList;
            }
            this.rowList = rowList;
            console.log('选择模板' + rowList)
        },
        saveGoods: function () {//保存商品

            this.goodsEntity.goods.category1Id = this.catSelected1;
            this.goodsEntity.goods.category2Id = this.catSelected2;
            this.goodsEntity.goods.category3Id = this.catSelected3;
            this.goodsEntity.goods.typeTemplateId = this.tempType,
                this.goodsEntity.goods.brandId = this.selBrand,
                this.goodsEntity.goods.isEnableSpec = this.isEnableSpec,

                this.goodsEntity.goodsDesc.itemImages = this.imgEntitys,
                this.goodsEntity.goodsDesc.specificationItems = this.specificationItems,
                this.goodsEntity.goodsDesc.introduction = UE.getEditor('editor').getContent()

            this.goodsEntity.itemList = this.rowList;

            var id = this.GetQueryString("id");
            var url = '';
            if (id != null) {
                url = '/goods/update.do';
            } else {
                url = '/goods/add.do';
            }
            //发送请求
            axios.post(url, this.goodsEntity)
                .then(function (response) {
                    console.log("goods实体：" + response.data);
                    location.href = "goods.html";
                }).catch(function (reason) {
                alert(response.data.message);
            });

        },
        checkAttributeValue: function (specName, optionName) { //判断当前规格是否为选中状态
            var items = this.specificationItems;
            var object = this.searchObjectByKey(items, "attributeName", specName);
            if (object != null) {
                if (object.attributeValue.indexOf(optionName) >= 0) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
    },
    watch: { //监听属性的变化
        tempType:function(newValue, oldValue) {
            var _this = this;
            _this.brandList = [];
            _this.selBrand = -1;
            //更新对应品牌选项
            axios.post("/temp/findOne.do?id=" + newValue)
                .then(function (response) {
                    _this.brandList = JSON.parse(response.data.brandIds);
                    console.log('品牌选项' + _this.brandList);
                    console.log(response.data);
                    if (_this.goodsEntity.goods.brandId != null){
                        _this.selBrand = _this.goodsEntity.goods.brandId;
                    }
                }).catch(function (reason) {
                console.log(reason);
            });

            _this.specList = [];
            //对应更新规格选项
            axios.get("/temp/findBySpecList.do?id=" + newValue)
                .then(function (response) {
                    _this.specList = response.data;
                    console.log('规格选项' + _this.specList);
                    console.log(response.data);
                }).catch(function (reason) {
                console.log(reason);
            })

        },

    },
    created: function () {
        this.loadCateData(0);
    },
    mounted:function () {
        var id = this.GetQueryString("id");
        var _this = this;
        if (id != null){
            //根据id查询当前商品
            axios.get("/goods/findOne.do?id="+id)
                .then(function (response) {
                    console.log(response.data);
                    //1.回显商品
                    _this.goodsEntity.goods = response.data.goods;
                    //2.商品描述
                    _this.goodsEntity.goodsDesc = response.data.goodsDesc;
                    //3.分类模板
                    _this.typeId = response.data.goods.typeTemplateId;
                    //回显图片
                    _this.imgEntitys = JSON.parse(response.data.goodsDesc.itemImages);
                    //回显富文本
                    UE.getEditor("editor").ready(function () {
                        UE.getEditor("editor").setContent(response.data.goodsDesc.introduction);
                    });
                    //选中规格
                    _this.specificationItems  = JSON.parse(response.data.goodsDesc.specificationItems);
                    //库存列表
                    _this.rowList = response.data.itemList;
                    for (var i = 0; i< _this.rowList.length; i++){
                        _this.rowList[i].spec =  JSON.parse(_this.rowList[i].spec);
                    }

                    //分类选中
                    _this.catSelected1 = response.data.goods.category1Id;
                    //控制选项默认选中状态
                    if (response.data.goods.category1Id >= 0){
                        _this.grade  = 2
                        _this.loadCateData(_this.catSelected1);
                        _this.catSelected2 = response.data.goods.category2Id;
                    }

                    if (_this.catSelected2 >= 0){
                        axios.post("/itemCat/findByParentId.do?parentId="+_this.catSelected2)
                            .then(function (response) {
                                //取服务端响应的结果
                                _this.categoryList3 = response.data;
                            }).catch(function (reason) {
                                console.log(reason);
                            })
                        _this.catSelected3 = response.data.goods.category3Id;
                        _this.getCatSelected(3);

                    }
                }).catch(function (reason) {
                console.log(reason);
            });
        }
    }
});
