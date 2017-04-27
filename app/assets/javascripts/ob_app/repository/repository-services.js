define(['angular'], function (angular) {
    'use strict';

    var mod = angular.module('repository.services', []);

    mod.factory('Repository', ["$resource", function ($resource) {
        return $resource("http://" + AppConfig.hostName + ":9000/repository", {}, {});
    }]);

    return mod;
});
