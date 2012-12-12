package ru.sincore.db.pojo;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "big_text_data")
public class BigTextDataPOJO implements Serializable
{
    @Getter
    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Long id;

    @Getter
    @Setter
    @Column(name = "title",length = 250)
	private String title;

    @Getter
    @Setter
    @Lob
	@Column(name = "data",columnDefinition = "LONGBLOB",nullable = false)
	private byte[] data;

    @Getter
    @Setter
    @Column(name = "locale",length = 7,nullable = true)
	private String locale;
}
