package ru.sincore.db.pojo;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "ban_list")
public class BanListPOJO
{
	@Getter
    @Setter
    @Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", columnDefinition = "INTEGER", length = 11)
	private Long id;

    @Getter
    @Setter
    @Column(name = "ip", columnDefinition = "VARCHAR(18)")
	private String ip;

    @Getter
    @Setter
    @Column(name = "nick", length = 150)
	private String nick;

    @Getter
    @Setter
    @Column(name = "ban_type", columnDefinition = "TINYINT")
	private Integer banType;

    @Getter
    @Setter
    @Column(name = "host_name", length = 150)
	private String hostName;

    @Getter
    @Setter
    @Column(name = "date_start")
	private Date dateStart = new Date();

    @Getter
    @Setter
    @Column(name = "date_stop")
	private Date dateStop;

    @Getter
    @Setter
    @Column(name = "op_nick")
	private String opNick;

    @Getter
    @Setter
    @Column(name = "reason", columnDefinition = "TEXT")
	private String reason;

    @Getter
    @Setter
    @Column(name = "email",length = 200,nullable = true)
	private String email;
}
