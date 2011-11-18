
package ru.sincore.db.dao;

import ru.sincore.db.pojo.StopWordsPOJO;

import java.util.List;

public interface StopWordsDAO
{
	void addMatch(String match);
	List<StopWordsPOJO> getMatches();
	void delMatchById();
	void updateMatchCounter(Integer matchId);
}
