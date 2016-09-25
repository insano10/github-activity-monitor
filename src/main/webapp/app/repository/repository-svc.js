(function() {

    var mod = angular.module("main");

    mod.factory("Repository", ["$resource", function RepositoryFactory($resource){

        return $resource("http://localhost:8080/api/repository", {}, {});

    }]);

})();