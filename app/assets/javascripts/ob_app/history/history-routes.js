define(['angular', './history-controllers'], function (angular, controllers) {
    'use strict';

    return angular.module('history.routes', [])
        .config(['$routeProvider', function ($routeProvider) {

            $routeProvider
                .when("/history", {
                    templateUrl:  "/assets/javascripts/ob_app/history/history-view.html",
                    controller:   controllers.HistoryCtrl,
                    controllerAs: "historyCtrl"
                });
        }]);
});
