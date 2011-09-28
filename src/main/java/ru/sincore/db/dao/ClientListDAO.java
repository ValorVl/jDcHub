package ru.sincore.db.dao;

import ru.sincore.db.pojo.ClientListPOJO;

import java.util.List;

public interface ClientListDAO
{
	boolean addClient(ClientListPOJO params);
	boolean delClient(String nickName);
	ClientListPOJO getClientByNick(String nick);
	List<ClientListPOJO> getClientList(Boolean regOnly);
	boolean updateClient(ClientListPOJO object);
}
