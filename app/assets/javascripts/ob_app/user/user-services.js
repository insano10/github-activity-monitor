define(['angular'], function (angular) {
    'use strict';

    var mod = angular.module('user.services', []);

    mod.factory('User', ["$resource", "hostName", function ($resource, hostName) {
        return $resource("http://" + hostName + ":8080/api/user", {}, {});
    }]);

    return mod;
});
