package ru.sincore.db.pojo;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

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

@Entity
@Table(name = "chat_log")
public class ChatLogPOJO implements Serializable
{
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id", nullable = false)
	private Long id;


	private String nikName;
	private Date sendDate;
	private String message;

}
