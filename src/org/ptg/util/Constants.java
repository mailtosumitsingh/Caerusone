/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

package org.ptg.util;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

public class Constants {
	public static final int UNKNOWN = -1;
	public static final String NA = "N/A";
	public static final String DefaultExceptionHandler = "DefaultExceptionHandler";
	public static final String NewLine = System.getProperty("line.separator")==null?"\n":System.getProperty("line.separator");
	public static final String DataSource = "dataSource";
	public static final String Asterix = "*";
	public static final Date SERVER_START = Calendar.getInstance().getTime();
	public static final String First  = "first";
	public static final String Second = "second";
	public static final String Third  = "third";
	public static final String Fourth = "fourth";
	public static final String Fifth  = "fifth";
	public static final String Last   = "last";
	public static boolean  DEBUG = true;
	public static final String VELOCITY_CONFIG_PATH = "config"+File.separator+"velocity.properties";
	public static final String Transformer = "Transformer";
	public static final String StreamTransformer = "StreamTransformer";
	public static final String Searchable = "searchable";
	public static final String Index = "index";
	public static final String Column = "col";
	public static final String FormatterObject = "obj";
	public static final String StmtObject = "stmt";
	public static final String RSObject = "$1";
	public static final String EventA = "a";
	public static final String Property = "property";
	public static final String Body = "body";
	public static final String PropertyXmlExpr = "propertyXmlExpr";
	public static final String BodyXmlExpr = "bodyXmlExpr";
	public static final String StreamTranformMethod = "transformStream";
	public static final String ObjectTranformMethod = "transformObject";
	public static final String ResultSetTransformMethod = "transformResultSet";
	public static final String MapTransformMethod = "transformMap";
	public static final String HTTPTransformMethod = "transformHTTP";
	public static final String StreamTranformJsonMethod = "transformJsonStream";
	public static final String EM_STREAM = "EM_STREAM";
	public static final String STREAMIN = "STREAMIN";
	public static final String Resources  = "extra";
	public static final String ScriptPath  = "scripts";
	public static final String Handle = "Handle";
	public static final String Id = "Id";
	public static final String Id2 = "Id2";
	public static final String CompiledCode = "CompiledCode";
	public static final String Action = "Action";
	public static final String ActionAdd = "Add";
	public static final String ActionRemove = "Remove";
	public static final String TaskString = "TaskString";
	public static final String CodeString = "CodeString";
	public static final String TaskTime = "TaskTime";
	public static final String LogTime = "LogTime";
	public static final String LogStmt = "LogStmt";
	public static final String LogEventType = "LogEventType";
	public static final String RouteName = "RouteName";
	public static final String RouteDescription = "RouteDescription";
	public static final String RouteType = "RouteType";
	public static final String LogSedaRoute = "direct:Log";
	public static final String DefaultEventTable =		"current_events";
	public static final String RouteEvent  = "org.ptg.util.events.RouteEvent";
	public static final int  Persistant = 1;
	public static final String RouteStore = "route_events";
	public static final String EventOutStreamXML = "eventoutXML";
	public static final String EventOutRouteXML = "direct:"+EventOutStreamXML;
	public static final String EventOutStreamJson = "eventoutJson";
	public static final String EventOutRouteJson = "direct:"+EventOutStreamJson;
	public static final String EventOutStreamMap = "eventoutMAP";
	public static final String EventOutRouteMap = "direct:"+EventOutStreamMap;
	public static final String Map = "MAP";
	
	public static final String EventList = "EventList";
	public static final String EventDoc = "EventDoc";
	
	public static final String ComponentName = "name";
	public static final String ComponentURL = "url";
	public static final String ComponentClass = "classname";
	public static final String ComponentsStore = "components";
	public static final String UUID = "UUID";
	public static final String TIMESTAMP = "TIMESTAMP";
	public static final String XML = "XML";
	public static final String JSON = "JSON";
	public static final String EventType = "EventType";
	public static final String RequestType  ="RequestType";
	public static final String EventCreateRequest ="EventCreateRequest";
	public static final String VelocityTemplate = "TemplateFile";
	public static final String TaskName = "TaskName";
	public static final String GetEventsRequest = "GetEventsRequest";
	public static final String GetEventDefinitionRequest = "GetEventDefinitionRequest";
	public static final String StreamName = "StreamName";
	public static final String WireTapStream = "WireTapStream";
	public static final String InstanceVarName = "$inst";
	public static final String OutInstanceVarName = "$outinst";
	public static final String InstanceVarValue = "in";
	public static final String OutInstanceVarValue = "out";
}




