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
public class LastWeekActivityCFType extends WithIssuesFromSameProjectCFType implements SortableCustomField
{
    public LastWeekActivityCFType(SearchService searchService, JiraAuthenticationContext authenticationContext)
    {
    	super(searchService, authenticationContext);
    }
/*
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
*/
    public Object getValueFromIssue(CustomField field, Issue issue)
    {
        return getIssues(issue, "created >= -1w OR (resolution is not empty AND updated >= -1w) OR (created < -1w AND due >= -1w)");
    }
}
