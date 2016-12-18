(function ()
{
    angular.module('myApp', ['ngRoute', 'ngResource', 'main']);

    var app = angular.module("main", []);

    //need to put this in config somewhere
    app.value('hostName', 'localhost');
})();
