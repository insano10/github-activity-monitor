define(['angular', './repository-controllers'], function (angular, controllers) {
    'use strict';

    return angular.module('repository.routes', [])
        .config(['$routeProvider', function ($routeProvider) {

            $routeProvider
                .when("/", {
                    templateUrl:  "/assets/javascripts/ob_app/repository/repository-view.html",
                    controller:   controllers.RepositoryCtrl,
                    controllerAs: "repoCtrl"
                });
        }]);
});
