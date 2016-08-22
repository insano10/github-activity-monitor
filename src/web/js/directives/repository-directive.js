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

                scope.totalPullRequests = scope.repository.pullRequests.length;

                scope.closedPullRequests = scope.repository.pullRequests.filter(function(pr) {
                   return pr.closed != null;
                }).length;

                scope.openPullRequests = scope.repository.pullRequests.filter(function(pr) {
                    return pr.closed == null;
                }).length;

                var lastCommitMsg = scope.repository.mostRecentCommit.message;

                if(lastCommitMsg.length > 80) {
                    scope.truncatedLastCommitMessage = lastCommitMsg.substring(0, 80) + "...";
                } else {
                    scope.truncatedLastCommitMessage = lastCommitMsg;
                }
            }
        };
    });

})();