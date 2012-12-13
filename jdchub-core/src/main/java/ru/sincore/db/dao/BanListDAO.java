package ru.sincore.db.dao;

import ru.sincore.db.pojo.BanListPOJO;

import java.util.List;

public interface BanListDAO
{
	Boolean addBan(BanListPOJO ban);
	BanListPOJO getBan(String nick, String ip);
    BanListPOJO getLastBan(String nick, String ip);
	//Integer delBan(String nick);
	List<BanListPOJO> lsBan(Integer rowCount);
    List<BanListPOJO> getAllBans(int banShowType, int page, int count);
}
