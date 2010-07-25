/*
 * Copyright (c) 2010 Dmitry Astapov <dastapov@gmail.com>
 * Distributed under Apache License 2.0
 */
package ua.astapov.jira.plugins.activityfields;

import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.customfields.SortableCustomField;
import com.atlassian.jira.issue.customfields.impl.FieldValidationException;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.issue.search.SearchException;
import com.atlassian.jira.bc.issue.search.SearchService;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class LastWeekActivityCFType extends WithIssuesFromSameProjectCFType implements SortableCustomField
{
    private static final Logger log = Logger.getLogger(LastWeekActivityCFType.class);

    public LastWeekActivityCFType(SearchService searchService, JiraAuthenticationContext authenticationContext)
    {
    	super(searchService, authenticationContext);
    }

    public String getStringFromSingularObject(Object value)
    {
        return value != null ? value.toString() : Boolean.FALSE.toString();
    }

    public Object getSingularObjectFromString(String string) throws FieldValidationException
    {
        if (string != null)
        {
            return (string);
        }
        else
        {
            return Boolean.FALSE.toString();
        }
    }

    public Object getValueFromIssue(CustomField field, Issue issue)
    {
        String lastWeekIssuesText = "";

        try
        {
        	// Note: does not take into account issues of the same type as current
        	final List<Issue> issues = getIssues(issue, "(due <= -1d or due is empty) AND resolution is empty ORDER BY key DESC");
        	for (Iterator<Issue> iterator = issues.iterator(); iterator.hasNext();)
        	{
        		Issue iss = iterator.next();
        		if (lastWeekIssuesText.isEmpty()) {
        			lastWeekIssuesText = iss.getKey();
        		} else {
        			lastWeekIssuesText = lastWeekIssuesText + ", " + iss.getKey();
        		}
        	}

        }
        catch (SearchException e)
        {
        	log.error("Error running search", e);
        	lastWeekIssuesText = "";
        }

        return lastWeekIssuesText;
    }
}
