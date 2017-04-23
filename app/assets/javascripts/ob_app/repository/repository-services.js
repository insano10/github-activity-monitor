define(['angular'], function (angular) {
    'use strict';

    var mod = angular.module('repository.services', []);

    mod.factory('Repository', ["$resource", "hostName", function ($resource, hostName) {
        return $resource("http://" + hostName + ":8080/api/repository", {}, {});
    }]);

    return mod;
});
