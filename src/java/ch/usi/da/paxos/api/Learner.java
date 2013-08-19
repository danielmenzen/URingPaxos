package ch.usi.da.paxos.api;
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

import java.util.concurrent.BlockingQueue;

import ch.usi.da.paxos.storage.Decision;

/**
 * Name: Learner<br>
 * Description: <br>
 * 
 * Creation date: Jun 21, 2013<br>
 * $Id$
 * 
 * @author leandro.pacheco.de.sousa@usi.ch
 */
public interface Learner {
	public BlockingQueue<Decision> getDecisions();
	
	public void setSafeInstance(Integer instance);
}
