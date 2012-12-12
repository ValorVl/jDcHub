package ru.sincore.db.pojo;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

/**
 * If you do changes in class fields, please do the same changes in AbstractCommand class.
 */

/**
 * @author Valor
 * @since 14.09.2011
 * @author Alexey 'lh' Antonov
 */
@Entity
@Table(name = "cmd_list")
public class CmdListPOJO implements Serializable
{
    @Getter
    @Setter
    @Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long 	id;

    @Getter
    @Setter
    @Column(name = "command_name",columnDefinition = "VARCHAR(50)",nullable = false,unique = true)
	private String 	commandName;

    @Getter
    @Setter
    @Column(name = "command_weight",columnDefinition = "INT(3) DEFAULT 100")
	private Integer commandWeight = 100;

    @Getter
    @Setter
    @Column(name = "command_args",columnDefinition = "TEXT",nullable = true)
	private String 	commandArgs = "";

    @Getter
    @Setter
    @Column(name = "command_syntax",columnDefinition = "TEXT",nullable = true)
	private String 	commandSyntax = "";

    @Getter
    @Setter
    @Column(name = "commandDescription", columnDefinition = "TEXT",nullable = true)
	private String  commandDescription = "";

    @Getter
    @Setter
    @Column(name = "enabled",columnDefinition = "TINYINT(1) DEFAULT 1")
	private Boolean enabled = true;

    @Getter
    @Setter
    @Column(name = "logs",columnDefinition = "TINYINT(1) DEFAULT 1")
	private Boolean logs = true;
}
