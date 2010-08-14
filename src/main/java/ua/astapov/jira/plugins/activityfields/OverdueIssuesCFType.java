/*
 * Copyright (c) 2010 Dmitry Astapov <dastapov@gmail.com>
 * Distributed under BSD License
 */
package ua.astapov.jira.plugins.activityfields;

import com.atlassian.jira.bc.issue.search.SearchService;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.customfields.SortableCustomField;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.security.JiraAuthenticationContext;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class OverdueIssuesCFType extends WithIssuesFromSameProjectCFType implements SortableCustomField
{
    public OverdueIssuesCFType(SearchService searchService, JiraAuthenticationContext authenticationContext)
    {
    	super(searchService, authenticationContext);
    }

    public Object getValueFromIssue(CustomField field, Issue issue)
    {
        return getIssues(issue, "due <= -1d AND resolution is empty");
    }
}
