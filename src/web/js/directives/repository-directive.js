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
            },
            link: function (scope, element, attrs) {

                scope.activeTab = 1;

                scope.showTab = function(tabId) {
                    scope.activeTab = tabId;
              }
            }
        };
    });

})();