define([], function () {
    'use strict';

    var DashHeaderCtrl = function ($scope, $http){

        $http.get("http://" + AppConfig.hostName + ":9000/config")
            .then(function(response) {
                $scope.boardName = response.data.boardName;
            });
    };

    return {
        DashHeaderCtrl: DashHeaderCtrl
    };

});