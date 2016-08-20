(function ()
{
    var mod = angular.module("main");

    mod.directive("repository", function ()
    {
        return {
            restrict:     'E',
            templateUrl:  'templates/directives/repository-card.html',
            scope: {
                repository: "="
            }
        };
    });

})();