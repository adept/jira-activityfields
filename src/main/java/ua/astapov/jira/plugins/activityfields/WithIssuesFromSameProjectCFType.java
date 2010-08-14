/*
 * Copyright (c) 2010 Dmitry Astapov <dastapov@gmail.com>
 * Distributed under BSD License
 */
package ua.astapov.jira.plugins.activityfields;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import com.atlassian.jira.bc.issue.search.SearchService;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.customfields.SortableCustomField;
import com.atlassian.jira.issue.customfields.impl.CalculatedCFType;
import com.atlassian.jira.issue.customfields.impl.FieldValidationException;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.search.SearchException;
import com.atlassian.jira.issue.search.SearchResults;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.web.bean.PagerFilter;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class WithIssuesFromSameProjectCFType extends CalculatedCFType implements SortableCustomField
{
    private static final Logger log = Logger.getLogger(WithIssuesFromSameProjectCFType.class);

    private final SearchService searchService;
    private final JiraAuthenticationContext authenticationContext;

    public WithIssuesFromSameProjectCFType(SearchService searchService, JiraAuthenticationContext authenticationContext)
    {
        this.searchService = searchService;
        this.authenticationContext = authenticationContext;
    }

    public List<Issue> getIssues(Issue issue, String additionalClauses)
    {
        final String projectKey = issue.getProjectObject().getKey();
        final String issueType = issue.getIssueTypeObject().getName();
        String jqlQuery = "project = " + projectKey + " and issuetype != \"" + issueType + "\"";
        if (!additionalClauses.isEmpty()) {
        	jqlQuery = jqlQuery + " and (" + additionalClauses + ") ORDER BY key DESC";
        } else {
            jqlQuery = jqlQuery + " ORDER BY key DESC";
        }
        
        final SearchService.ParseResult parseResult = searchService.parseQuery(authenticationContext.getUser(), jqlQuery);

        if (parseResult.isValid())
        {
            try
            {
                final SearchResults results = searchService.search(authenticationContext.getUser(),
                        parseResult.getQuery(), PagerFilter.getUnlimitedFilter());
                final List<Issue> issues = results.getIssues();
                return issues;
            }
            catch (SearchException e)
            {
                log.error("Error running search", e);
                return new LinkedList<Issue> ();
            }
        }
        else
        {
            log.error("Error parsing jqlQuery: " + parseResult.getErrors());
            return new LinkedList<Issue> ();
        }
    }

	public Object getValueFromIssue(CustomField field, Issue iss) {
		// TODO Auto-generated method stub
		//return getIssues(iss,"");
		return null;
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
}
