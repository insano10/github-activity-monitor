/**
 * User package module.
 * Manages all sub-modules so other RequireJS modules only have to import the package.
 */
define(['angular', './repository-routes', './repository-services', './directive/repository-directive'], function(angular) {
  'use strict';

  return angular.module('observation-deck.repository', ['repository.routes', 'repository.services', 'repository.directives']);
});
