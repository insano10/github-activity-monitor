define([], function () {
    'use strict';

    var RepositoryCtrl = function (Repository, $http) {

        var ctrl = this;

        function refreshRepositories() {

            if(!ctrl.repositories) {
                ctrl.loading = true;
            }

            Repository.query(function(value, headers){
                ctrl.repositories = value;
                ctrl.loading = false;
            }, function(errorResponse){
                console.log("error: " + JSON.stringify(errorResponse));
            });
        }

        refreshRepositories();

        $http.get("http://" + AppConfig.hostName + ":9000/config")
            .then(function(response) {
                ctrl.days = response.data.daysData;
            });

        setInterval(refreshRepositories, 60000);
    };
    RepositoryCtrl.$inject = ['Repository', '$http'];

    return {
        RepositoryCtrl: RepositoryCtrl
    };

});