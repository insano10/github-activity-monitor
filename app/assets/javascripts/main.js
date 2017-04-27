// `main.js` is the file that sbt-web will use as an entry point
(function (requirejs) {
  'use strict';

  // -- RequireJS config --
  requirejs.config({
    // Packages = top-level folders; loads a contained file named 'main.js"
    packages: ['ob_app/user', 'ob_app/repository', 'ob_app/components', 'ob_app/history'],
    shim: {
      'jsRoutes': {
        deps: [],
        // it's not a RequireJS module, so we have to tell it what var is returned
        exports: 'jsRoutes'
      },
      // Hopefully this all will not be necessary but can be fetched from WebJars in the future
      'angular': {
        deps: ['jquery'],
        exports: 'angular'
      },
      'angular-resource': ['angular'],
      'angular-route': ['angular'],
      'angular-cookies': ['angular'],
      'bootstrap': ['jquery', 'tether']
    },
    paths: {
      'requirejs': ['../lib/requirejs/require.min'],
      'jquery': ['../lib/jquery/jquery.min'],
      'angular': ['../lib/angularjs/angular.min'],
      'angular-resource': ['../lib/angularjs/angular-resource.min'],
      'angular-route': ['../lib/angularjs/angular-route.min'],
      'angular-cookies': ['../lib/angularjs/angular-cookies.min'],
      'bootstrap': ['../lib/bootstrap/js/bootstrap.min'],
      'tether': ['../lib/tether/dist/js/tether.min'],
      'jsRoutes': ['/jsroutes']
    }
  });

  requirejs.onError = function (err) {
    console.log(err);
  };

  // Load the app. This is kept minimal so it doesn't need much updating.
  require(['angular', 'angular-cookies', 'angular-route', 'angular-resource', 'jquery', 'tether', 'bootstrap', './app'],
    function (angular) {
      angular.bootstrap(document, ['app']);
    }
  );
})(requirejs);
