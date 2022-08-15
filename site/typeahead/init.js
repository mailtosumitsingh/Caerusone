$(document).ready(function() {
  $('.example-countries .typeahead').typeahead({
    name: 'countries',
    prefetch: '../data/countries.json',
    remote:{
    url:	'/site/AutoCompleteHelper?sq=%QUERY',
    filter: function (parsedResponse) {
        console.log(parsedResponse);
        return parsedResponse;
    }
    },
    engine: Hogan,
    limit: 10
  });

});
