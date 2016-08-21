(function ()
{
    var mod = angular.module("main");

    mod.directive("repository", function ()
    {
        return {
            restrict:     'E',
            templateUrl:  'templates/directives/repository-card.html',
            scope: {
                repository: "=",
                months: "@"
            },
            link: function (scope, element, attrs) {

                console.log(scope.repository.name);
                console.log(scope.months);

                scope.totalPullRequests = scope.repository.pullRequests.length;

                scope.closedPullRequests = scope.repository.pullRequests.filter(function(pr) {
                   return pr.closed != null;
                }).length;

                scope.openPullRequests = scope.repository.pullRequests.filter(function(pr) {
                    return pr.closed == null;
                }).length;

                scope.activeTab = 1;

                scope.showTab = function(tabId) {
                    scope.activeTab = tabId;
              }
            }
        };
    });

})();