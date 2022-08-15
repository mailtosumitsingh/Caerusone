/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

var a  = {};
a.name= "dada";
a.value = "didi";
var str = a.toJSONString();
js_Print("Json test: " +str);
//re.addRoutes(new MyRouteBuilder());
//AppContext.getInstance().setVar("pagetrace","false");
//AppContext.getInstance().setVar("processtrace","false");
//var name = "TopStockEventsRoute";
//var desc = ' from("direct:topstockevent").inOnly().process(new myprojectmanager.website.listener.events.StockWebEventPublisherListener());';

//WebStartProcess.getInstance().getRoutingEngine().addRouteFromString(name,desc);
/*
name = "TopStockEventsRouteIn";
desc = 'from("direct:topstockeventin").process(new org.ptg.processors.EventFastProcessor());';

WebStartProcess.getInstance().getRoutingEngine().addRouteFromString(name,desc);
*/
//Thread.currentThread().sleep(2000);
//AnalyzeTopFeeds.run();



/*following deep json test is now disable enable for testing json at startup
 * there are two tests conducted one here and one in webstart
 * uncomment the webstart one as well. it is pretty stable now so disabling.
 * var json = 
{ "store": {
      "book": [ 
        { "category": "reference",
              "author": "Nigel Rees",
              "title": "Sayings of the Century",
              "price": 8.95
        },
        { "category": "fiction",
              "author": "Evelyn Waugh",
              "title": "Sword of Honour",
              "price": 12.99
        },
        { "category": "fiction",
              "author": "Herman Melville",
              "title": "Moby Dick",
              "isbn": "0-553-21311-3",
              "price": 8.99
        },
        { "category": "fiction",
              "author": "J. R. R. Tolkien",
              "title": "The Lord of the Rings",
              "isbn": "0-395-19395-8",
              "price": 22.99
        }
      ],
      "bicycle": {
        "color": "red",
        "price": 19.95
      }
}
},
out = "";

out += jsonPath(json, "$.store.book[*].author").toJSONString() + "\n>";
out += jsonPath(json, "$..author").toJSONString() + "\n";
out += jsonPath(json, "$.store.*").toJSONString() + "\n";
out += jsonPath(json, "$.store..price").toJSONString() + "\n";
out += jsonPath(json, "$..book[(@.length-1)]").toJSONString() + "\n";
out += jsonPath(json, "$..book[-1:]").toJSONString() + "\n";
out += jsonPath(json, "$..book[0,1]").toJSONString() + "\n";
out += jsonPath(json, "$..book[:2]").toJSONString() + "\n";
out += jsonPath(json, "$..book[?(@.isbn)]").toJSONString() + "\n";
out += jsonPath(json, "$..book[?(@.price<10)]").toJSONString() + "\n";
out += jsonPath(json, "$..*").toJSONString() + "\n";
js_Print("printing: "+out);*/
