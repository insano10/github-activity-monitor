define(['angular'], function (angular) {
    'use strict';

    return angular.module('components.directives', [])
        .directive("loadingThingy", function () {
            return {
                restrict:    'E',
                templateUrl: '/assets/javascripts/ob_app/components/loading-thingy/loading-thingy.html',
                scope:       {
                    repository: "=",
                    days:       "@"
                },
                link:        function (scope, element, attrs) {

                    scope.range = function (n) {
                        return new Array(n);
                    };
                }
            };
        });
});