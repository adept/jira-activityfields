/*
 * Copyright (c) 2010 Dmitry Astapov <dastapov@gmail.com>
 * Distributed under Apache License 2.0
 */
package ua.astapov.jira.plugins.activityfields;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.comments.Comment;
import com.atlassian.jira.issue.comments.CommentManager;
import com.atlassian.jira.issue.customfields.SortableCustomField;
import com.atlassian.jira.issue.customfields.impl.CalculatedCFType;
import com.atlassian.jira.issue.customfields.impl.FieldValidationException;
import com.atlassian.jira.issue.fields.CustomField;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class LastWeekCommentsCFType extends CalculatedCFType implements SortableCustomField
{
    private static final Logger log = Logger.getLogger(LastWeekCommentsCFType.class);

    private final CommentManager commentManager;

    public LastWeekCommentsCFType(CommentManager commentManager)
    {
        this.commentManager = commentManager;
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
    	String lastWeekCommentsText = null;
        try
        {
            List<Comment> comments = commentManager.getComments(issue);

            if (comments == null || comments.isEmpty()) {
                lastWeekCommentsText = "";
            } else {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, 0); // 24-hour clock, so - not "HOUR"
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.add(Calendar.DATE, -7); // 1 week
                Date weekAgo = calendar.getTime();

            	Comment currComment = null;
            	Iterator<Comment> i = comments.iterator();
                while (i.hasNext()) {
            	    currComment = i.next();
        		    Date latestDate = getLatestUpdate(currComment);
            	    if (latestDate.compareTo(weekAgo) >= 0) {
                        if (lastWeekCommentsText == null || lastWeekCommentsText.isEmpty()) {
                            lastWeekCommentsText = currComment.getBody();
                        } else {
                            lastWeekCommentsText = lastWeekCommentsText + "<br /><hr />" + currComment.getBody();
                        }
            	    }
                }
            }
        }
        catch (Exception e)
        {
            log.error(e.getMessage(), e);
            lastWeekCommentsText = "";
        }

        return lastWeekCommentsText;
    }

	private Date getLatestUpdate(Comment comment) {
		Date latestDate = null;
		if (comment.getUpdated() == null) {
			latestDate = comment.getCreated();
		} else {
			latestDate = comment.getUpdated();
		}
		return latestDate;
	}
}
