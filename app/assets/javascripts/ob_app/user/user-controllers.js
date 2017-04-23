define([], function () {
    'use strict';

    var UserCtrl = function (User) {

        var ctrl = this;

        ctrl.users = User.query(function (value, headers) {
            //console.log(JSON.stringify(value));
        }, function (errorResponse) {
            console.log("error: " + JSON.stringify(errorResponse));
        });
    };
    UserCtrl.$inject = ['User'];

    return {
        UserCtrl: UserCtrl
    };

});
