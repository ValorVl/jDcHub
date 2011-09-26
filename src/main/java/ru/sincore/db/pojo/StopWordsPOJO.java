package ru.sincore.db.pojo;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author Valor
 */
@Entity
@Table(name = "stop_words")
public class StopWordsPOJO
{
	private Long id;
	private String patternHeader;
	private String patternBody;
}
