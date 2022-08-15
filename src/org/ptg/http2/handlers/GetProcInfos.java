package org.ptg.http2.handlers;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.ptg.processors.ProcessorInfo;
import org.ptg.util.CommonUtil;
import org.ptg.util.closures.IStateAwareClosure;
import org.ptg.util.db.DBHelper;

public class GetProcInfos   extends AbstractHandler {

	public void handle(String arg0, Request arg1, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		response.setHeader("Access-Control-Allow-Origin", "*");
		try {
			final List<org.ptg.processors.ProcessorInfo> names = new ArrayList<org.ptg.processors.ProcessorInfo>();
			StringBuilder sb  = new StringBuilder();
			
			DBHelper.getInstance().forEach("select name,shortname,icon,code,id from procinfos",new IStateAwareClosure() {
				@Override
				public void init() {
					names.clear();
				}
				
				@Override
				public void finish() {
					
				}
				
				@Override
				public void execute(ResultSet rs) throws SQLException {
					String name = rs.getString(1);
					String shortname = rs.getString(2);
					String icon = rs.getString(3);
					String code = rs.getString(4);
					int id = rs.getInt(5);
					ProcessorInfo info = new ProcessorInfo();
					info.setCode(code);
					info.setIcon(icon);
					info.setShortName(shortname);
					info.setName(name);
					info.setId(id);
					names.add(info);
				}
			});
			sb.append(CommonUtil.jsonFromCollection(names));
			response.getOutputStream().print(sb.toString());
		} catch (Exception e) {
			response.getOutputStream().print("Cannot retreive processor infos");
			e.printStackTrace();
		}
		arg1.setHandled(true);
	}
}

/*
@Entity
public class ProcessorInfo {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Key id;
	@Basic
	protected String name;
	@Basic
	protected String shortName;
	@Basic
	protected String icon;
	@Basic
	protected Text code;

	public Key getId() {
		return id;
	}

	public void setId(Key id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public Text getCode() {
		return code;
	}

	public void setCode(Text code) {
		this.code = code;
	}

}
 
 * 
 */