(function ()
{
    var mod = angular.module("main");

    mod.controller("RepositoryController", ['Repository', '$http', function (Repository, $http)
    {
        var ctrl = this;

        function refreshRepositories() {
            ctrl.repositories = Repository.query(function(value, headers){
            }, function(errorResponse){
                console.log("error: " + JSON.stringify(errorResponse));
            });
        }

        refreshRepositories();

        $http.get("http://localhost:8080/api/config")
            .then(function(response) {
                ctrl.days = response.data.daysData;
            });

        setInterval(refreshRepositories, 60000);
    }]);

})();