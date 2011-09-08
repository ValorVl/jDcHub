package ru.sincore.db.dao;

import ru.sincore.db.pojo.BanListPOJO;

import java.util.Date;
import java.util.List;

public interface BanListDAO
{
	Boolean addBan(String nick,String ip, String host, Integer banType, Date start, Date end, String banOwner, String reason, Long shareSize, String email);
	BanListPOJO getBan(String ip, String nick);
	//Integer delBan(String nick);
	List<BanListPOJO> lsBan(Integer rowCount);
	List<BanListPOJO> userBanList(String nick);
}
