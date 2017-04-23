define(['angular', './user-controllers'], function (angular, controllers) {
    'use strict';

    var mod = angular.module('user.routes', []);
    mod.config(['$routeProvider', function ($routeProvider) {
        $routeProvider
            .when("/users", {
                templateUrl:  "/assets/javascripts/ob_app/user/user-view.html",
                controller:   controllers.UserCtrl,
                controllerAs: "userCtrl"
            });
    }]);
    return mod;
});
