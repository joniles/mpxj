/*
 * file:       FieldLists.java
 * author:     Jon Iles
 * copyright:  (c) Timephased Limited 2023
 * date:       2023-02-03
 */

/*
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation; either version 2.1 of the License, or (at your
 * option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307, USA.
 */

package net.sf.mpxj.common;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.sf.mpxj.FieldType;

/**
 * Fields grouped into logical collections.
 */
public final class FieldLists
{
   public static final List<FieldType> CUSTOM_FIELDS = Stream.of(TaskFieldLists.CUSTOM_FIELDS, ResourceFieldLists.CUSTOM_FIELDS, AssignmentFieldLists.CUSTOM_FIELDS).flatMap(Collection::stream).collect(Collectors.toList());

   public static final Set<FieldType> CUSTOM_FIELDS_SET = new HashSet<>(CUSTOM_FIELDS);
}
