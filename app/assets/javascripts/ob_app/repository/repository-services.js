define(['angular'], function (angular) {
    'use strict';

    return angular.module('repository.services', [])
        .factory('Repository', ["$resource", "hostName", function ($resource, hostName) {
            return $resource("http://" + hostName + ":8080/api/repository", {}, {});
        }]);

});
