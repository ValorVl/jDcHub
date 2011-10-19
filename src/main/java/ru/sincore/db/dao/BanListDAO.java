package ru.sincore.db.dao;

import ru.sincore.db.pojo.BanListPOJO;

import java.util.List;

public interface BanListDAO
{
	Boolean addBan(BanListPOJO ban);
	List<BanListPOJO> getBan(String ip, String nick);
	//Integer delBan(String nick);
	List<BanListPOJO> lsBan(Integer rowCount);
	List<BanListPOJO> userBanList(String nick);
}
