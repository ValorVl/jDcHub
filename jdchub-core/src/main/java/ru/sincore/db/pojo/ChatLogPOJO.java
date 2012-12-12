package ru.sincore.db.pojo;

/**
 * *****************************************************************************
 * Copyright (c) 2011  valor.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * <p/>
 * Package : ru.sincore.db.pojo
 * <p/>
 * Date: 03.09.11
 * Time: 11:52
 * <p/>
 * Contributors:
 * valor - initial API and implementation
 * ****************************************************************************
 */

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "chat_log")
public class ChatLogPOJO implements Serializable
{
    @Getter
    @Setter
    @Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id",columnDefinition = "INTEGER")
	private Long id;

    @Getter
    @Setter
    @Column(name = "nickname")
	private String nickName;

    @Getter
    @Setter
    @Column(name = "send_date")
	private Date sendDate;

    @Getter
    @Setter
    @Column(name = "message",columnDefinition = "TEXT")
	private String message;
}
