(function () {
	'use strict';
	angular
		.module('marsApp')
		.factory('Comment', Comment);

	Comment.$inject = ['$resource'];

	function Comment($resource) {
		var resourceUrl = 'api/comments/:id';

		return $resource(resourceUrl, {}, {
			'query': { method: 'GET', isArray: true}
		});
	}
})();
