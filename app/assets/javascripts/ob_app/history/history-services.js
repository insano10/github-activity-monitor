define(['angular'], function (angular) {
    'use strict';

    return angular.module('history.services', [])
        .factory("ReleaseHistory", ["$http", "hostName", function ($http, hostName) {

            function getHistory() {
                return $http({
                                 method: 'GET',
                                 url:    "http://" + hostName + ":8080/api/history/release"
                             });
            }

            return {
                'getHistory': getHistory
            };
        }]);
});
