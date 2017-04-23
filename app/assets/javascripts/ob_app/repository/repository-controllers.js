define([], function () {
    'use strict';

    var RepositoryCtrl = function (Repository, $http, hostName) {

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

        $http.get("http://" + hostName + ":8080/api/config")
            .then(function(response) {
                ctrl.days = response.data.daysData;
            });

        setInterval(refreshRepositories, 60000);
    };
    RepositoryCtrl.$inject = ['Repository', '$http', 'hostName'];

    return {
        RepositoryCtrl: RepositoryCtrl
    };

});