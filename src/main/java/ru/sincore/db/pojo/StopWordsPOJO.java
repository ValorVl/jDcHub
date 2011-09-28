package ru.sincore.db.pojo;

import javax.persistence.*;

/**
 * @author Valor
 */
@Entity
@Table(name = "stop_words")
public class StopWordsPOJO
{
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long 		id;

	@Column(name = "pattern",columnDefinition = "TEXT")
	private String 		pattern;

	@Column(name = "match_count", nullable = false)
	private Long 		matchCount = 0L;

	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public String getPattern()
	{
		return pattern;
	}

	public void setPattern(String pattern)
	{
		this.pattern = pattern;
	}

	public Long getMatchCount()
	{
		return matchCount;
	}

	public void setMatchCount(Long matchCount)
	{
		this.matchCount = matchCount;
	}
}
