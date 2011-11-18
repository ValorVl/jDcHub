package ru.sincore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import ru.sincore.db.dao.StopWordsDAOImpl;
import ru.sincore.db.pojo.StopWordsPOJO;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *  The class contains methods for validate many strings by match list
 *
 *  @author Valor
 */
public class StopWord
{

	private static final Logger log = LoggerFactory.getLogger(StopWord.class);
	private String marker = Marker.ANY_MARKER;

	private StopWordsDAOImpl matchDao = new StopWordsDAOImpl();

	public StopWord()
	{
	}

	/**
	 * Method checked source string by matched regexp
	 * @param sourceStr source string, must be matched
	 * @return true if and only if source string not matched on pattern, return false if otherwise.
	 */
	public boolean check(String sourceStr)
	{

		List<StopWordsPOJO> matchList = matchDao.getMatches();

		StringBuilder patternBuilder = new StringBuilder();
		// Build compiled match
		for (Object obj : matchList)
		{
			if (patternBuilder.length() > 0)
			{
				patternBuilder.append('|');
			}
			patternBuilder.append("(");
			patternBuilder.append(obj);
			patternBuilder.append(")");
		}

		if (log.isDebugEnabled())
		{
			log.debug("StopWord final str : " + patternBuilder.toString());
		}

		Pattern pattern = Pattern.compile(patternBuilder.toString());

		Matcher matcher = pattern.matcher(sourceStr);

		if (matcher.matches())
		{
			return false;
		}

		return true;
	}

}
