new Vue({
    el: "#app",
    data: {
        cartList: [],//购物车列表
        totalValue: {
            totalNum: 0,
            totalMoney: 0
        } //总价
    },
    methods:{
        loadCartData: function () {
            var _this = this;
            axios.post("/cart/findCartList.do")
                .then(function (response) {
                    console.log(response.data);
                    _this.cartList = response.data;
                    _this.totalValue = _this.sum(_this.cartList);
                    console.log(_this.totalValue);
                    console.log(_this.cartList);
                }).catch(function (reason) {
                console.log(reason);
            })
        },
        sum:function(cartList){
            var totalValue={totalNum:0,totalMoney:0 };
            for(var i=0;i<cartList.length ;i++){
                var cart=cartList[i];//购物车对象
                for(var j=0;j<cart.orderItemList.length;j++){
                    var orderItem=  cart.orderItemList[j];//购物车明细
                    totalValue.totalNum+=orderItem.num;//累加数量
                    totalValue.totalMoney+=orderItem.price*orderItem.num;//累加金额

                }
            }
            return totalValue;
        }
    },
    created:function () {
        this.loadCartData();
    }

})