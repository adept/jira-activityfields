/*
 * Copyright (c) 2010 Dmitry Astapov <dastapov@gmail.com>
 * Distributed under BSD License
 */
package ua.astapov.jira.plugins.activityfields;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.customfields.SortableCustomField;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.bc.issue.search.SearchService;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class NextWeekActivityCFType extends WithIssuesFromSameProjectCFType implements SortableCustomField
{
    public NextWeekActivityCFType(SearchService searchService, JiraAuthenticationContext authenticationContext)
    {
    	super(searchService, authenticationContext);
    }

        public Object getValueFromIssue(CustomField field, Issue issue)
    {
        return getIssues(issue, "resolution is empty and ((due >= 0 AND due <= 1w)  OR (due > 1w AND status in (\"In Progress\",\"Open\")))");
    }
}
