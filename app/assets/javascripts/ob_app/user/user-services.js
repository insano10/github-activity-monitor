define(['angular'], function (angular) {
    'use strict';

    var mod = angular.module('user.services', []);

    mod.factory('User', ["$resource", function ($resource) {
        return $resource("http://" + AppConfig.hostName + ":8080/api/user", {}, {});
    }]);

    return mod;
});
