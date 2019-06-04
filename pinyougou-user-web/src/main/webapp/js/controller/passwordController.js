//首页控制器
app.controller('passwordController',function($scope,passwordService){
    $scope.changePassword=function(){
        passwordService.change($scope.pwdid,$scope.password,$scope.pwd).success(
            function (response) {
                if(response.success){
                    location.href='home-setting-address-complete.html';
                }else {
                    alert(response.message);
                }
            }
        )
    }
});