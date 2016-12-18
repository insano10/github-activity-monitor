(function ()
{
    var mod = angular.module("main");

    mod.directive("loadingThingy", function ()
    {
        return {
            restrict:     'E',
            templateUrl:  'app/loading-thingy/loading-thingy.html',
            scope: {
                repository: "=",
                days: "@"
            },
            link: function (scope, element, attrs) {

                scope.range = function(n) {
                    return new Array(n);
                };
            }
        };
    });

})();