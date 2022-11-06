/*
 * Copyright 2016 Yuan Yao
 * University of Nottingham
 * Email: yvy@cs.nott.ac.uk (yuanyao1990yy@icloud.com)
 * 
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details 
 *  <http://www.gnu.org/licenses/gpl-3.0.html>.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package uno.gpt.structure;

import java.util.ArrayList;

/**
 * @version 1.0
 */
public abstract class Node
{
	/**  name */
	final private String name;

	/**
	 * get the list of prerequisite steps
	 */
	public abstract ArrayList<Node> getPreqList();
	/**
	 * get the list of dependent steps
	 */
	public abstract ArrayList<Node> getDepList();

	
	Node(String name)
	{
		this.name = name;
	}
	
	/** return the name */
	public String getName()
	{
		return this.name;
	}
}
