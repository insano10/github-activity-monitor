define(['angular'], function (angular) {
    'use strict';

    var mod = angular.module('history.services', []);
    mod.factory("ReleaseHistory", ["$http", function ($http) {

        function getHistory() {
            return $http({
                             method: 'GET',
                             url:    "http://" + AppConfig.hostName + ":8080/api/history/release"
                         });
        }

        return {
            'getHistory': getHistory
        };
    }]);

    return mod;
});
