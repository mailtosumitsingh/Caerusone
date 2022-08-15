/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

package org.ptg.cluster;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.ptg.events.Event;

public class TickerEvent extends Event{
	@Override
	public String toString() {
		return "TickerEvent [marketName=" + marketName + ", price=" + price + ", ticker=" + ticker + "]";
	}

	private String ticker;
	private double price;
	private String marketName;
	
	public TickerEvent() {
		super();
	}

	public TickerEvent(String ticker, double price, String marketName) {
		super();
		this.ticker = ticker;
		this.price = price;
		this.marketName = marketName;
	}

	public String getMarketName() {
		return marketName;
	}

	public void setMarketName(String marketName) {
		this.marketName = marketName;
	}

	public String getTicker() {
		return ticker;
	}

	public void setTicker(String ticker) {
		this.ticker = ticker;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public void readExternal(ObjectInput in) throws IOException,	ClassNotFoundException {
		super.readExternal(in);
		ticker = in.readUTF();
		price = in.readDouble();
		marketName= in.readUTF();
		
	}

	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);
		out.writeUTF(ticker);
		out.writeDouble(price);
		out.writeUTF(marketName);
	}

}
