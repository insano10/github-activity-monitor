define(['angular'], function (angular) {
    'use strict';

    return angular.module('user.directives', [])
        .directive("user", function () {
            return {
                restrict:    'E',
                templateUrl: '/assets/javascripts/ob_app/user/directive/user-card.html',
                scope:       {
                    user: "="
                }
            };
        });
});
