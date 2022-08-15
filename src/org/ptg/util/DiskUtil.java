/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
 */

package org.ptg.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.Closure;
import org.ptg.events.Event;
import org.ptg.events.KeyEvent;

public class DiskUtil {
	private String table;
	private String database;
	private boolean startEmpty;

	public String getTable() {
		return table;
	}

	public void setTable(String table) {
		this.table = table;
	}

	public String getDatabase() {
		return database;
	}

	public void setDatabase(String database) {
		this.database = database;
	}

	public boolean isStartEmpty() {
		return startEmpty;
	}

	public void setStartEmpty(boolean startEmpty) {
		this.startEmpty = startEmpty;
	}

	static {
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

	}
	Connection connection = null;

	public void init() throws Exception {
		try {
			connection = DriverManager.getConnection(database);
			connection.setAutoCommit(true);
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30); // set timeout to 30 sec.
			if (startEmpty) {
				statement.executeUpdate("drop table if exists " + table);
				statement.executeUpdate("create table " + table + " (itemkey  text PRIMARY KEY unique, itemdata BLOB,test status,text corid ,itemversion integer default 1)");

				statement.execute(getTriggerString());
			} else {
				statement.executeUpdate("create table if not exists " + table + " (itemkey  text PRIMARY KEY unique, itemdata BLOB,test status,text corid ,itemversion integer default 1) ");
				statement.execute(getTriggerString());
			}
		} catch (SQLException e) {
			System.err.println(e.getMessage());
		} finally {

		}
	}

	public void update(String key, Event o, String status, String corid) throws Exception {
		byte[] bts = CommonUtil.write(o);
		update(key, bts, status, corid);
	}

	public void add(String key, Event o, String status, String corid) throws Exception {
		byte[] bts = CommonUtil.write(o);
		add(key, bts, status, corid);
	}

	public void update(String key, byte[] bts, String status, String corid) throws Exception {
		try {
			if (connection == null) {
				connection = DriverManager.getConnection(database);
			}
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30); // set timeout to 30 sec.
			PreparedStatement pstmt = connection.prepareStatement("update " + table + " set itemdata=?,status=?,corid=? where itemkey=?");
			pstmt.setBytes(1, bts);
			pstmt.setString(2, status);
			pstmt.setString(3, corid);
			pstmt.setString(4, key);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			System.err.println(e.getMessage());
		} finally {

		}
	}

	public void add(String key, byte[] bts, String status, String corid) throws Exception {
		try {
			if (connection == null) {
				connection = DriverManager.getConnection(database);
			}
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30); // set timeout to 30 sec.
			PreparedStatement pstmt = connection.prepareStatement("insert or replace into " + table + " (itemkey,itemdata,status,corid) values (?,?)");
			pstmt.setString(1, key);
			pstmt.setBytes(2, bts);
			pstmt.setString(3, status);
			pstmt.setString(4, corid);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			System.err.println(e.getMessage());
			if (e.getMessage().contains("SQLITE_CONSTRAINT")) {
				update(key, bts, status, corid);
			}

		} finally {

		}
	}

	public void delete(String key) throws Exception {
		try {
			if (connection == null) {
				connection = DriverManager.getConnection(database);
			}

			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30); // set timeout to 30 sec.
			PreparedStatement pstmt = connection.prepareStatement("delete from " + table + " where itemkey in(?)");
			pstmt.setString(1, key);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			System.err.println(e.getMessage());
		} finally {

		}
	}

	public byte[] get(String key) throws Exception {
		try {
			if (connection == null) {
				connection = DriverManager.getConnection(database);
			}
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30); // set timeout to 30 sec.
			PreparedStatement pstmt = connection.prepareStatement("select itemdata from " + table + " where itemkey =?");
			pstmt.setString(1, key);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next() == true) {
				return rs.getBytes(1);
			}
		} catch (SQLException e) {
			System.err.println(e.getMessage());
		} finally {

		}
		return null;
	}

	// todo to delete allindex upon all deletion
	public void deleteAll() throws Exception {
		try {
			if (connection == null) {
				connection = DriverManager.getConnection(database);
			}

			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30); // set timeout to 30 sec.
			PreparedStatement pstmt = connection.prepareStatement("truncate table " + table);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			System.err.println(e.getMessage());
		} finally {

		}
	}

	public void forEach(Closure c) {
		try {
			if (connection == null) {
				connection = DriverManager.getConnection(database);
			}

			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30); // set timeout to 30 sec.
			PreparedStatement pstmt = connection.prepareStatement("select itemdata from " + table);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				c.execute(rs.getBytes(1));
			}
		} catch (SQLException e) {
			System.err.println(e.getMessage());
		} finally {

		}
	}

	public List<KeyEvent> getKeys() {
		List<KeyEvent> l = new ArrayList<KeyEvent>();
		try {
			if (connection == null) {
				connection = DriverManager.getConnection(database);
			}

			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30); // set timeout to 30 sec.
			PreparedStatement pstmt = connection.prepareStatement("select itemkey,itemversion from " + table);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				String k = rs.getString("itemkey");
				int v = rs.getInt("itemversion");
				KeyEvent key = new KeyEvent();
				key.setVersion(v);
				key.setKey(k);
				if (k != null) {
					l.add(key);
				}
			}
		} catch (SQLException e) {
			System.err.println(e.getMessage());
		} finally {

		}
		return l;
	}

	public Map<String, KeyEvent> getKeyMap() {
		Map<String, KeyEvent> l = new HashMap<String, KeyEvent>();
		try {
			if (connection == null) {
				connection = DriverManager.getConnection(database);
			}

			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30); // set timeout to 30 sec.
			PreparedStatement pstmt = connection.prepareStatement("select itemkey,itemversion from " + table);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				String k = rs.getString("itemkey");
				int v = rs.getInt("itemversion");
				if (k != null) {
					l.put(k, new KeyEvent(k, v));
				}
			}
		} catch (SQLException e) {
			System.err.println(e.getMessage());
		}
		return l;
	}

	private String getTriggerString() {
		return "CREATE TRIGGER if not exists update_itemversion after UPDATE  ON " + table + "\n" + "BEGIN\n" + "UPDATE " + table + " SET itemversion = old.itemversion+1 WHERE  rowid = new.rowid;\n"
				+ "END;\n";
	}

	public Object[] getWithVersion(String key) throws Exception {
		Object[] objs = null;
		try {
			if (connection == null) {
				connection = DriverManager.getConnection(database);
			}

			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30); // set timeout to 30 sec.
			PreparedStatement pstmt = connection.prepareStatement("select itemdata,itemversion from " + table + " where itemkey =?");
			pstmt.setString(1, key);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next() == true) {
				objs = new Object[2];
				int v = rs.getInt("itemversion");
				byte[] data = rs.getBytes(1);
				objs[0] = v;
				objs[1] = data;
			}
		} catch (SQLException e) {
			System.err.println(e.getMessage());
		} finally {

		}
		return objs;
	}

	public int getVersion() {
		try {
			if (connection == null) {
				connection = DriverManager.getConnection(database);
			}

			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30);
			PreparedStatement pstmt = connection.prepareStatement("select count(itemversion) as dbversion from " + table);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next() == true) {
				return rs.getInt("dbversion");
			}
		} catch (SQLException e) {
			System.err.println(e.getMessage());
		}
		return -1;
	}
}
