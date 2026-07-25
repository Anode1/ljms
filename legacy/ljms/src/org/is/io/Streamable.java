/**
 * @(#)Streamable.java
 * Copyright (C) 2001 Vasili Gavrilov
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.is.io;

import java.io.IOException;

/**
 * Analog of Externalizable but with Data streams instead of Object ones
 *
 * @since jdk1.0
 */
public interface Streamable{

    void toStream(java.io.DataOutputStream out) throws IOException;

    void fromStream(java.io.DataInputStream in) throws IOException;
}
