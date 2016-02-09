package edu.kit.gik.STGIS.importer;

/*
 * Copyright (C) 2010 by Mathias Menninghaus (mmenning (at) uos (dot) de)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 * Interface to manage the Layers which are displayed in SurfaceVisualizer.
 * IMPORTANT: The Buffers must use allocateDirect(..) and nativeOrder(..)
 * 
 * @version 01.09.2009
 * @author Mathias Menninghaus
 * 
 */
public interface OGLLayer {
	/**
	 * returns a FloatBuffer of indices in the order of: x1,y1,z1,x2,y2...
	 * 
	 * @return
	 */
	public FloatBuffer getVertexBuffer() ;

	/**
	 * returns indices as an ShortBuffer in order of Index_1_of_Triangle_1,
	 * Index_2_of_Triangle_1, Index_3_of_Triangle_1, Index_1_of_Triangle_2,
	 * ... , Index_3_of_Triangle_n
	 * 
	 * The indices shall start by 0!
	 * 
	 * @return
	 */
	public IntBuffer getIndexBuffer() ;

	/**
	 * returns line indices as an ShortBuffer in order of Index_1_of_Line_1,
	 * Index_2_of_Line_1, Index_1_of_Line_2, ... , Index_2_of_Line_n
	 * 
	 * @return
	 */
	public IntBuffer getLineBuffer();
	
	/**
	 * returns the normals as an FloatBuffer in order of ...
	 * 
	 * @return
	 */
	public FloatBuffer getNormalBuffer();
	
	/**
	 * returns the name of the OGLLayer. If no name has been read a default
	 * name according to a number.
	 * 
	 * @return read name or default-name
	 */
	public String getName() ;
	

	/**
	 * returns color in android-format. if no color has been read a random
	 * color will be created.
	 * 
	 * @return read or random color (@see android.graphics.color)
	 */
	public int getColor();

	public boolean isFill();

	public void setFill(boolean fill);

	public boolean isVisible();
	
	public void setVisible(boolean visible);

	public boolean isSelected();

	public void setSelected(boolean selected);

	
}
