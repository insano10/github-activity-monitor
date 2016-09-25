(function ()
{
    var mod = angular.module("main");

    mod.controller("RepositoryController", ['Repository', '$http', function (Repository, $http)
    {
        var ctrl = this;

        ctrl.repositories = Repository.query(function(value, headers){
            //console.log(JSON.stringify(value));
        }, function(errorResponse){
            console.log("error: " + JSON.stringify(errorResponse));
        });

        $http.get("http://localhost:8080/api/config")
            .then(function(response) {
                ctrl.days = response.data.daysData;
            });
    }]);

})();