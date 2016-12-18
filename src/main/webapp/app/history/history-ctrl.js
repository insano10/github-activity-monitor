(function ()
{
    var mod = angular.module("main");

    mod.controller("HistoryController", ['ReleaseHistory', 'hostName', function (ReleaseHistory, hostName)
    {
        var ctrl = this;

        ReleaseHistory.query(function(value, headers) {
            ctrl.releaseHistory = value;
        });
    }]);

})();