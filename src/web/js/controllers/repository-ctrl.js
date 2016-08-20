(function ()
{
    var mod = angular.module("main");

    mod.controller("RepositoryController", ['Repository', function (Repository)
    {
        var ctrl = this;

        ctrl.repositories = Repository.query(function(value, headers){
            //console.log(JSON.stringify(value));
        }, function(errorResponse){
            console.log("error: " + JSON.stringify(errorResponse));
        });
    }]);

})();