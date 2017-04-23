define(['angular'], function (angular) {
    'use strict';

    return angular.module('components.directives', [])
        .directive("dashHeader", function ()
        {
            return {
                restrict:     'E',
                templateUrl:  '/assets/javascripts/ob_app/components/dash-header/dash-header.html',
                controller: ['$scope', '$http', 'hostName', function($scope, $http, hostName){

                    $http.get("http://" + hostName + ":8080/api/config")
                        .then(function(response) {
                            $scope.boardName = response.data.boardName;
                        });
                }]
            };
        });
});