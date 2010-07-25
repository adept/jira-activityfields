/*
 * Copyright (c) 2010 Dmitry Astapov <dastapov@gmail.com>
 * Distributed under Apache License 2.0
 */
package ua.astapov.jira.plugins.activityfields;

import java.util.Calendar;
import java.util.Date;
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
public class WhatsOverdueCFType extends WithIssuesFromSameProjectCFType implements SortableCustomField
{
    private static final Logger log = Logger.getLogger(WhatsOverdueCFType.class);

    public WhatsOverdueCFType(SearchService searchService, JiraAuthenticationContext authenticationContext)
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
        boolean issuesOverdue = false;
        boolean projectOverdue = false;

        try
        {
        	// Note: does not take into account issues of the same type as current
        	final List<Issue> issues = getIssues(issue, "(due <= -1d) AND resolution is empty ORDER BY key DESC");
        	if (! issues.isEmpty()) {
        		issuesOverdue = true;
        	}
        	Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 0); // 24-hour clock, so - not "HOUR"
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            Date today = calendar.getTime();
            
        	if ( (issue.getDueDate() != null) && issue.getDueDate().before(today) ) {
        		projectOverdue = true;
        	}
        }
        catch (SearchException e)
        {	
        	log.error("Error running search", e);
        }

        String whatsOverdueText = "";
        if (issuesOverdue && projectOverdue) {
        	whatsOverdueText = "Project, Issues ";
        } else if (issuesOverdue) {
        	whatsOverdueText = "Issues";
        } else if (projectOverdue) {
        	whatsOverdueText = "Project ";
        } else {
        	whatsOverdueText = "";
        }
        return whatsOverdueText;
    }
}
