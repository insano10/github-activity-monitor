(function ()
{
    var mod = angular.module("main");

    mod.directive("user", function ()
    {
        return {
            restrict:     'E',
            templateUrl:  'app/user/directive/user-card.html',
            scope: {
                user: "="
            }
        };
    });

})();