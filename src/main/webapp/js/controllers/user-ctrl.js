(function ()
{

    var mod = angular.module("main");

    mod.controller("UserController", ['User', function (User)
    {
        var ctrl = this;

        ctrl.users = User.query(function(value, headers){
            //console.log(JSON.stringify(value));
        }, function(errorResponse){
            console.log("error: " + JSON.stringify(errorResponse));
        });
    }]);

})();