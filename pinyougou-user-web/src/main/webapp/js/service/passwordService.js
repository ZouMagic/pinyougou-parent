app.service('passwordService',function($http){
    //读取列表数据绑定到表单中
    this.change=function(pwdid,password,pwd){
        return $http.post('../password/change.do?username='+pwdid+"&password="+password+"&pwd="+pwd);
    }
});