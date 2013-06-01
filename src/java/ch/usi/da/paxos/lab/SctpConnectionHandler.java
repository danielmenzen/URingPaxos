package ch.usi.da.paxos.lab;
/* 
 * Copyright (c) 2013 Università della Svizzera italiana (USI)
 * 
 * This file is part of URingPaxos.
 *
 * URingPaxos is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * URingPaxos is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with URingPaxos.  If not, see <http://www.gnu.org/licenses/>.
 */

import java.io.IOException;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import ch.usi.da.paxos.message.Message;

import com.sun.nio.sctp.AbstractNotificationHandler;
import com.sun.nio.sctp.HandlerResult;
import com.sun.nio.sctp.MessageInfo;
import com.sun.nio.sctp.SctpChannel;
import com.sun.nio.sctp.ShutdownNotification;

/**
 * Name: ConnectionHandler<br>
 * Description: <br>
 * 
 * Creation date: Sep 12, 2012<br>
 * $Id$
 * 
 * @author Samuel Benz <benz@geoid.ch>
 */
public class SctpConnectionHandler implements Runnable {

	private final SctpChannel channel;
	
	private final ByteBuffer buffer = ByteBuffer.allocate(16384);
	
	private long total_r = 0;
	
	private long total_b = 0;
	
	private long start_time;
	
	List<Long> t = new ArrayList<Long>();

	AssociationHandler assocHandler = new AssociationHandler();
	
	/**
	 * @param channel
	 */
	public SctpConnectionHandler(SctpChannel channel) {
		this.channel = channel;
	}

	@Override
	public void run() {
		try {
			MessageInfo messageInfo = null;
			start_time = System.nanoTime();
			do {
				messageInfo = channel.receive(buffer,System.out,assocHandler);
				buffer.flip();
				int	count = buffer.remaining();
				if (count > 0){
					byte[] bytes = new byte[count];
					buffer.get(bytes);
					total_b = total_b+bytes.length;
					total_r++;
					Message m = Message.fromWire(bytes);
					long latency = System.nanoTime()-Long.parseLong(m.getValue().getID());
					t.add(latency);
					if(total_r % 20000 == 0){
						float runtime = (float)(System.nanoTime()-start_time)/(1000*1000*1000);
						System.out.println("r: " + total_r + " " + (float)total_r/runtime + " msg/s (" + (float)(total_b/runtime)/1024 + " kbytes/s) (" + (float)8*(total_b/runtime)/1024/1024 + " Mbit/s)");
						print();
					}
            	}
            	buffer.clear();
			} while (messageInfo != null);
			channel.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	static class AssociationHandler extends AbstractNotificationHandler<PrintStream> {

		public HandlerResult handleNotification(ShutdownNotification not,PrintStream stream) {
			stream.printf("The association has been shutdown.\n");
			return HandlerResult.RETURN;
		}
	}
	
	/**
	 * print stats
	 */
	public void print(){
		long sum = 0;
		for(Long l : t){
			sum = sum + l;
		}
		System.err.println("avg:" + (float)(sum/t.size()));
	}

}
