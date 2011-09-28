package ru.sincore.db.pojo;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "chat_list")
public class ChatListPOJO
{

	private Long 		id;

	private String 		chatName;
	private String 		chatDescription;
	private Integer 	rightWeight;
	private String		chatCid;
	private Boolean		enabled;
}
