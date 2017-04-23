(function ()
{
    var mod = angular.module("main");

    mod.directive("repository", function ()
    {
        return {
            restrict:     'E',
            templateUrl:  'app/repository/directive/repository-card.html',
            scope: {
                repository: "=",
                days: "@"
            },
            link: function (scope, element, attrs) {

                scope.totalPullRequests = scope.repository.pullRequests.length;

                scope.closedPullRequests = scope.repository.pullRequests.filter(function(pr) {
                   return pr.closedTimeMs !== null;
                }).length;

                scope.openPullRequests = scope.repository.pullRequests.filter(function(pr) {
                    return pr.closedTimeMs === null;
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