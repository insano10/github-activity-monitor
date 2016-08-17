(function ()
{
    var mod = angular.module("main");

    mod.directive("user", function ()
    {
        return {
            restrict:     'E',
            templateUrl:  'templates/directives/user-view.html',
            scope: {
                user: "="
            }
        };
    });

})();