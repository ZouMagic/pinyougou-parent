//首页控制器
app.controller('safeController',function($scope,safeService,passwordService){

    $scope.change=function(){
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



    $scope.initPhone=function(){
        safeService.init().success(
            function(response){
                $scope.phone=response.phone;
            }
        );
    }

    $scope.sendCode=function(){
        safeService.send($scope.phone);
    }

    $scope.checkCode=function(){
        safeService.check($scope.phone,$scope.msgcode).success(
            function (response) {
                if(response.success){
                    location.href='home-setting-address-phone.html';
                }else {
                alert(response.message);
                }
            }
        );
    }
});