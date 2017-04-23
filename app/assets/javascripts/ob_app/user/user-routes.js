define(['angular', './user-controllers'], function (angular, controllers) {
    'use strict';

    return angular.module('user.routes', ['user.services'])
        .config(['$routeProvider', function ($routeProvider) {

            $routeProvider
                .when("/users", {
                    templateUrl:  "/assets/javascripts/ob_app/user/user-view.html",
                    controller:   controllers.UserCtrl,
                    controllerAs: "userCtrl"
                });
        }]);
});
