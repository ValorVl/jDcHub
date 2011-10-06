package ru.sincore.db.dao;

import ru.sincore.db.pojo.KickListPOJO;

import java.util.List;

public interface KickListDAO
{
	void addKickedClient(KickListPOJO kick);
	List<KickListPOJO> getKicked();
	KickListPOJO getKickedByNick(String nick);
	void updateKickStatus(KickListPOJO kick);
}
