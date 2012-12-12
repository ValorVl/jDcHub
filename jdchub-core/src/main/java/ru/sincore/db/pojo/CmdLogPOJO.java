/*
* CmdLogPOJO.java
*
*
* Copyright (C) 2011 Valor
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 2
* of the License, or any later version.

* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.

* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/

package ru.sincore.db.pojo;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Index;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "cmd_log")
public class CmdLogPOJO
{
    @Getter
    @Setter
    @Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	Long 		id;

    @Getter
    @Setter
    @Index(name = "nick_index")
	@Column(name = "nick_name", columnDefinition = "VARCHAR(150)")
	String 		nickName;

    @Getter
    @Setter
    @Index(name = "command_index")
	@Column(name = "command_name",columnDefinition = "VARCHAR(50)")
	String 		commandName;

    @Getter
    @Setter
    @Column(name = "command_args",columnDefinition = "VARCHAR(250)")
	String 		commandArgs;

    @Getter
    @Setter
    @Index(name = "date_index")
	@Column(name = "execute_date",columnDefinition = "DATETIME")
	Date		executeDate;

    @Getter
    @Setter
    @Column(name = "execute_result",columnDefinition = "TEXT")
	String		executeResult;
}
