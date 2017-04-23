define(['angular'], function (angular) {
    'use strict';

    return angular.module('user.services', ['yourprefix.common', 'ngCookies'])
        .factory('User', ["$resource", "hostName", function ($resource, hostName) {
            return $resource("http://" + hostName + ":8080/api/user", {}, {});
        }]);

});
