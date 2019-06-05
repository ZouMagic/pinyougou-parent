//首页控制器
app.controller('infoController',function($scope,infoService,uploadService){

    //加载当前用户数据
    $scope.initial=function () {
        infoService.initial().success(
            function (response) {
                $scope.entity = response;
                $scope.entity.name=JSON.parse($scope.entity.name);
                $scope.site();
                $scope.province();
            }
        )
    }
    $scope.judgeChecked=function (x) {

      if( $scope.entity.sex == x){
          return true;
      }
        return false;
    }

    $scope.site=function () {
        if($scope.entity.name != null){
            $scope.provinceStr=$scope.entity.name.pId;
            $scope.cityStr=$scope.entity.name.cId;
            $scope.areaStr=$scope.entity.name.aId;
        }



    }
    $scope.provinceList={};
    $scope.province=function () {
        infoService.province().success(
            function (response) {
                $scope.provinceList=response;
            }
        )
    };


    $scope.ceshi=function () {
        $scope.alterName ={pId:$scope.provinceStr,cId:$scope.cityStr,aId:$scope.areaStr};
        $scope.entity.name= JSON.stringify($scope.alterName);
        alert($scope.entity.name)
    };

    $scope.$watch("provinceStr" ,function (newValue,oldValue) {

        infoService.city(newValue).success(
            function (response) {
                $scope.cityList=response;
            }
        )
    });
    $scope.$watch("cityStr" ,function (newValue,oldValue) {

        infoService.areas(newValue).success(
            function (response) {
                $scope.areasList=response;
            }
        )

    });

    $scope.causes=[{work:'程序员',id:"程序员"}, {work:'产品经理',id:"产品经理"}, {work:'UI设计师',id:"UI设计师"}];

    $scope.uploadFile=function () {
        alert(123);
        uploadService.uploadFile().success(
            function (response) {
                if(response.success){
                    $scope.entity.headPic=response.message;
                }else{
                    alert(response.message);
                }
            }
        )
    }
    $scope.save=function () {
        $scope.ceshi();
        infoService.save($scope.entity).success(
            function (response) {
                if(response.success){
                    alert(response.message);
                }else {
                    alert(response.message);
                }


            }
        )
    }




});