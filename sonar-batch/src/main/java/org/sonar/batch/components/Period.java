/*
 * SonarQube, open source software quality management tool.
 * Copyright (C) 2008-2013 SonarSource
 * mailto:contact AT sonarsource DOT com
 *
 * SonarQube is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * SonarQube is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package org.sonar.batch.components;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;

import java.util.Date;

public class Period {

  private int index;
  private Date targetDate;
  private Date date;

  public Period(int index, Date targetDate, @Nullable Date date) {
    this.index = index;
    this.targetDate = targetDate;
    this.date = date;
  }

  public int getIndex() {
    return index;
  }

  public Date getTargetDate() {
    return targetDate;
  }

  @CheckForNull
  public Date getDate() {
    return date;
  }
}
