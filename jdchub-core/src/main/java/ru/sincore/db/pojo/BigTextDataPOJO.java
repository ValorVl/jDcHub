package ru.sincore.db.pojo;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "big_text_data")
public class BigTextDataPOJO implements Serializable
{
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Long id;
	@Column(name = "title",length = 250)
	private String title;
	@Lob
	@Column(name = "data",columnDefinition = "LONGBLOB",nullable = false)
	private byte[] data;

	@Column(name = "locale",length = 7,nullable = true)
	private String locale;

	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public byte[] getData()
	{
		return data;
	}

	public void setData(byte[] data)
	{
		this.data = data;
	}

	public String getLocale()
	{
		return locale;
	}

	public void setLocale(String locale)
	{
		this.locale = locale;
	}
}
