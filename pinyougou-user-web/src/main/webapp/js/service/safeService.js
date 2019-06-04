app.service('safeService',function($http){
    //读取列表数据绑定到表单中
    this.init=function(){
        return $http.get('../safe/init.do');
    }

    this.send=function(phone){
        return $http.post('../safe/send.do?phone='+phone);
    }
    this.check=function(phone,msgcode){
        return $http.post('../safe/check.do?phone='+phone+"&code="+msgcode);
    }

});