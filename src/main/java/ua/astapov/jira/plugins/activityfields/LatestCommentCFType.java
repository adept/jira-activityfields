/*
 * Copyright (c) 2010 Dmitry Astapov <dastapov@gmail.com>
 * Distributed under BSD License
 */
package ua.astapov.jira.plugins.activityfields;

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
public class LatestCommentCFType extends CalculatedCFType implements SortableCustomField
{
    private static final Logger log = Logger.getLogger(LatestCommentCFType.class);

    private final CommentManager commentManager;

    public LatestCommentCFType(CommentManager commentManager)
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
        String latestCommentText = null;
        try
        {
            List<Comment> comments = commentManager.getComments(issue);

            if (comments == null || comments.isEmpty()) {
                latestCommentText = "";
            } else {
                Comment latestComment = null;
                Iterator<Comment> i = comments.iterator();
                latestComment = i.next();
                Date latestDate = getLatestUpdate(latestComment);
                while (i.hasNext()) {
                    Comment nextComment = (Comment) i.next();
                    Date nextDate = getLatestUpdate(nextComment);
                    if (nextDate.compareTo(latestDate) > 0) {
                        latestComment = nextComment;
                        latestDate = getLatestUpdate(latestComment);
                    }
                }
                latestCommentText = latestComment.getBody();
            }
        }
        catch (Exception e)
        {
            log.error(e.getMessage(), e);
            latestCommentText = "";
        }

        return latestCommentText;
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
