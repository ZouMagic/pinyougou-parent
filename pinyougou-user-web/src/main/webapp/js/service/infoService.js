app.service("infoService",function ($http) {
    //查询当前用户的个人信息
    this.initial=function () {
        return $http.get("../info/initial.do");
    }

    //查询省
    this.province=function () {
        return $http.get("../info/province.do");
    }

    //根据省查询市
    this.city=function (provinceId) {
        return $http.get("../info/city.do?provinceId="+provinceId);
    }
  /*  $scope.provinceStr=$scope.entity.name[provinceId];
    $scope.cityStr=$scope.entity.name[cityId];
    $scope.areaStr=$scope.entity.name[areaId];*/
    //根据省查询市
    this.areas=function (cityId) {
        return $http.get("../info/areas.do?cityId="+cityId);
    }
    this.save=function (user) {
        return $http.post("../info/save.do",user);
    }
});